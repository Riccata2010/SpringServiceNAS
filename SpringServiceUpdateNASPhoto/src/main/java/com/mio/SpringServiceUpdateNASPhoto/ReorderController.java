package com.mio.SpringServiceUpdateNASPhoto;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mio.aop.utils.AfterExecution;
import com.mio.aop.utils.BeforeExecution;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/ss-nas-photo/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SuppressWarnings("all")
public class ReorderController {

	private static final Log LOGGER = LogFactory.getLog(FileOperation.class);

	@Autowired
	private FileComponentManager manager;

	@GetMapping(value = "/reorder", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@BeforeExecution(id = "ss-nas-photo", operation = "*:+1:*:*:*:*:*:*:*")
	@AfterExecution(id = "ss-nas-photo", operation = "*:-1:*:*:*:*:*:*:*")
	public Flux<ProgressMessage> reorderUploads() {
		return manager.reorder();
	}

	@GetMapping(value = "/reorder-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@BeforeExecution(id = "ss-nas-photo", operation = "*:*:+1:*:*:*:*:*:*")
	@AfterExecution(id = "ss-nas-photo", operation = "*:*:-1:*:*:*:*:*:*")
	public Flux<ProgressMessage> reorderData() {
		return manager.reorderAllData();
	}

	@GetMapping("/info-file/{name}")
	@BeforeExecution(id = "ss-nas-photo", operation = "*:*:*:+1:*:*:*:*:*")
	@AfterExecution(id = "ss-nas-photo", operation = "*:*:*:-1:*:*:*:*:*")
	public ResponseEntity<Map<String, Object>> infoFile(@PathVariable("name") String name) {
		LOGGER.info("infoFile - name: " + name);
		Map<String, Object> map = manager.getInfoFile(name);
		LOGGER.info("--------------------------------------------------------");
		LOGGER.info("--------------------------------------------------------");
		LOGGER.info("--------------------------------------------------------");
		LOGGER.info("infoFile - map : " + map);
		LOGGER.info("--------------------------------------------------------");
		LOGGER.info("--------------------------------------------------------");
		LOGGER.info("--------------------------------------------------------");
		return Utils.responseOk().addParams("response", "OK").addParams(map).get();
	}
}
