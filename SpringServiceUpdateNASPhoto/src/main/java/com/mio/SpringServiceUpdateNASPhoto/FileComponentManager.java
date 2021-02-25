package com.mio.SpringServiceUpdateNASPhoto;

import static com.mio.SpringCustomAspectBaseLib.BaseUtils.getEnvOrDefault;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;

import com.mio.SpringCustomAspectBaseLib.BaseUtils;
import com.mio.SpringCustomAspectBaseLib.BaseUtils.IndexMap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class FileComponentManager {

	public static int THREADS_POOL = 10;
	public static final List<String> LIST_ACCEPTED = Arrays.asList(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".jfif",
			".exif", ".tiff", ".hdr", ".ppm", ".mp4", ".mov", ".mkv", ".mpeg", ".3gp");
	public static File DATA_DIR = null;

	public boolean accepted(@Nonnull File file) {
		boolean result = false;
		try {
			String mime = Files.probeContentType(Paths.get(file.toString()));
			result = mime != null
					&& (mime.toLowerCase().trim().startsWith("video") || mime.toLowerCase().trim().startsWith("image"));
			if (!result) {
				String ext = Utils.getExtensionFileName(file);
				result = LIST_ACCEPTED.contains(ext);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public ProgressMessage initProgress(@Nonnegative int size) {
		return setProgress(String.valueOf(size), "starting", "header");
	}

	public ProgressMessage emptyProgress() {
		return setProgress(String.valueOf(0), "starting", "header");
	}

	public ProgressMessage setProgress(@Nonnull String value, @Nonnull String info, @Nonnull String data) {
		ProgressMessage message = new ProgressMessage();
		message.setValue(value);
		message.setInfo(info);
		message.setData(data);
		return message;
	}

	private void emitExceptionAndComplete(@Nonnull Exception exc, @Nonnull Sinks.Many<ProgressMessage> sink) {
		exc.printStackTrace();
		sink.tryEmitNext(ProgressMessage.onException(exc));
		sink.tryEmitComplete();
	}

	public Flux<ProgressMessage> reorder() {
		Sinks.Many<ProgressMessage> sink = BeanFactory.newSinks();
		Flux<ProgressMessage> flux = BeanFactory.newFlux(sink);
		reorderExecute(sink);
		return flux;
	}

	private void initDataDir() {
		if (DATA_DIR == null) {
			DATA_DIR = new File(getEnvOrDefault("DATA_DIR", "Data"));
			if (!DATA_DIR.exists()) {
				DATA_DIR.mkdirs();
			}
		}
	}

	private void reorderExecute(final @Nonnull Sinks.Many<ProgressMessage> sink) {
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				System.out.println("reorderExecute");
				File uploads = new File(getEnvOrDefault("UPLOADS_DIR", "Uploads"));

				if (uploads.exists() && uploads.isDirectory()) {

					initDataDir();

					File[] files = uploads.listFiles(f -> f.isFile() && f.canRead() && accepted(f));
					if (files != null && files.length > 0) {

						List<FileOperation> list = Collections.synchronizedList(new ArrayList<>());
						Stream.of(files).map(FileOperation::new).map(f -> f.setTotal(files.length)).forEach(list::add);

						if (sink.tryEmitNext(initProgress(list.size())).isSuccess()) {
							Utils.sleep(1000);
							execute(list, sink);
						}
					} else {
						sink.tryEmitNext(emptyProgress());
						sink.tryEmitComplete();
					}
				}
			} catch (Exception exc) {
				emitExceptionAndComplete(exc, sink);
			}
		});
	}

	public Flux<ProgressMessage> reorderAllData() {

		Sinks.Many<ProgressMessage> sink = BeanFactory.newSinks();
		Flux<ProgressMessage> flux = BeanFactory.newFlux(sink);

		initDataDir();

		if (!DATA_DIR.exists()) {
			sink.tryEmitNext(emptyProgress());
			sink.tryEmitComplete();
		} else {
			reorderAllDataExecute(sink);
		}

		return flux;
	}

	private void reorderAllDataExecute(final @Nonnull Sinks.Many<ProgressMessage> sink) {
		Executors.newSingleThreadExecutor().execute(() -> {
			try {

				File dirOld = Utils.getOldUniquePath(DATA_DIR);
				Path old = Files.move(DATA_DIR.toPath(), dirOld.toPath(), REPLACE_EXISTING);
				DATA_DIR.mkdirs();

				List<FileOperation> list = Collections.synchronizedList(new ArrayList<>());
				walk(old.toString(), list);

				if (!list.isEmpty()) {
					list.stream().forEach(l -> l.setTotal(list.size()));
					if (sink.tryEmitNext(initProgress(list.size())).isSuccess()) {
						Utils.sleep(1000);
						execute(list, sink);
					}
				} else {
					sink.tryEmitNext(emptyProgress());
					sink.tryEmitComplete();
				}
			} catch (Exception exc) {
				emitExceptionAndComplete(exc, sink);
			}
		});
	}

	public void walk(String path, List<FileOperation> list) {

		File root = new File(path);
		File[] files = root.listFiles();

		if (files == null)
			return;

		for (File f : files) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath(), list);
			} else {
				list.add(new FileOperation(f));
			}
		}
	}

	public void execute(List<FileOperation> list, Sinks.Many<ProgressMessage> sink) throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(THREADS_POOL);
		list.forEach(es::execute);
		es.shutdown();
		Executors.newSingleThreadExecutor().execute(() -> {
			while (!list.isEmpty()) {
				int size = list.size();
				for (int i = 0; i < size; i++) {
					FileOperation ope = list.get(i);
					if (ope.isDone()) {
						synchronized (sink) {
							sink.tryEmitNext(ope.getResult());
							list.remove(i);
						}
						break;
					}
				}
			}
		});
		for (; !list.isEmpty();) {
		}
		sink.tryEmitComplete();
//		List<Future<String>> future = es.invokeAll(list);
//		es.shutdown();
//		for (Future<String> f : future) {
//			try {
//				LOGGER.info("reorder: " + f.get());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		sink.tryEmitComplete();
	}

	public IndexMap getInfoFile(String fileName) {
		initDataDir();
		IndexMap map = BaseUtils.newIndexMap();
		map.put("name", fileName);
		map.put("exists", "false");
		if (fileName != null && fileName.length() > 0) {
			LocationSpecifier lc = new LocationSpecifier();
			String path = lc.getProposalPathByName(fileName);
			File file = new File(DATA_DIR.toString() + File.separator + path + File.separator + fileName);
			if (file.exists()) {
				map.put("exists", "true");
			} else {
				try {
					@SuppressWarnings("resource")
					Stream<Path> stream = Files.find(DATA_DIR.toPath(), 5,
							(p, attr) -> p.getFileName().toString().equals(fileName));
					if (stream != null) {
						long num = stream.peek(p -> map.iput("path", p.toString())).count();
						if (num > 0) {
							map.put("exists", "true");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}
}
