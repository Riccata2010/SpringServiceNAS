package com.mio.SpringServiceUpdateNASPhoto;

import static com.mio.SpringCustomAspectBaseLib.BaseUtils.getEnvOrDefault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mio.aop.utils.BeforeExecution;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/ss-nas-photo/upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SuppressWarnings("all")
public class UploderFileController {

	private static final Log LOGGER = LogFactory.getLog(UploderFileController.class);

	@BeforeExecution(id = "ss-nas-photo", operation = "+1:*:*:*:*:*:*:*:*")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<String> uploadHandler(@RequestBody Flux<Part> parts) {
		return parts.filter(part -> part instanceof FilePart).ofType(FilePart.class).flatMap(this::store);
	}

	private void sendEndOpe() {
		AspectCustomResolution.after("ss-nas-photo", "-1:*:*:*:*:*:*:*:*");
	}

	public static String getFileChecksum(MessageDigest digest, File file) throws IOException {

		StringBuilder sb = new StringBuilder();
		FileInputStream fis = null;

		try {

			fis = new FileInputStream(file);
			byte[] byteArray = new byte[1024];
			int bytesCount = 0;

			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}

			byte[] bytes = digest.digest();

			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
		} catch (Exception exc) {
			throw exc;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception exc) {
			}
		}

		return sb.toString();
	}

	private String getChecksum(String file) {
		return getChecksum(new File(file));
	}

	private String getChecksum(File file) {
		try {
			return getFileChecksum(MessageDigest.getInstance("SHA-256"), file);
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
	}

	private Mono<String> store(FilePart filePart) {

		LOGGER.info("store - filePart: " + filePart.filename());

		try {
			File fd = new File(getEnvOrDefault("UPLOADS_DIR", "Uploads"));
			if (!fd.exists()) {
				fd.mkdirs();
			}
		} catch (Exception e) {
			sendEndOpe();
			return Mono.error(e);
		}

		final String filename = filePart.filename();

		File file = new File(getEnvOrDefault("UPLOADS_DIR", "Uploads") + File.separator + filename);

		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			sendEndOpe();
			return Mono.error(e);
		}

		try {

			FileOutputStream fos = new FileOutputStream(file);
			WritableByteChannel channel = fos.getChannel();

			return filePart.content().doOnEach(dataBufferSignal -> {

				if (dataBufferSignal.hasValue()) {

					try {

						DataBuffer dataBuffer = dataBufferSignal.get();
						int count = dataBuffer.readableByteCount();
						byte[] bytes = new byte[count];
						dataBuffer.read(bytes);

						final ByteBuffer byteBuffer = ByteBuffer.allocate(count);
						byteBuffer.put(bytes);
						byteBuffer.flip();

						channel.write(byteBuffer);

						byteBuffer.clear();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).doOnComplete(() -> {

				try {
					channel.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					fos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					fos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}).doOnError(t -> {
				LOGGER.info("ERROR: " + t);
			}).last().map(x -> {
				return "END SHA-256:" + getChecksum(file) + ":" + filename;
			}).map(x -> {
				sendEndOpe();
				return x;
			});

		} catch (Exception e) {
			sendEndOpe();
			return Mono.error(e);
		}
	}
}
