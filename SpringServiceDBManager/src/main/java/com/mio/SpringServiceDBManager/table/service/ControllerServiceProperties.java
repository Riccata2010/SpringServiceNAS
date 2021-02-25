package com.mio.SpringServiceDBManager.table.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mio.SpringServiceDBManager.Utils;
import com.mio.aop.utils.AfterExecution;
import com.mio.aop.utils.BeforeExecution;

@RestController
@RequestMapping(path = "/ss-nas-manager/service")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SuppressWarnings("all")
public class ControllerServiceProperties {

	private static final Log LOGGER = LogFactory.getLog(ControllerServiceProperties.class);

	@Autowired
	public ServiceServiceProperties service;

	@GetMapping("/test")
	@BeforeExecution(id = "ss-nas-manager", operation = "+1:*:*:*:*:*:*:*:*")
	@AfterExecution(id = "ss-nas-manager", operation = "-1:*:*:*:*:*:*:*:*")
	public ResponseEntity<Map<String, Object>> test() {
		LOGGER.info("<SERVICE_PROPERTIES> test CALLED");
		Map<String, Object> map = new HashMap<>();
		map.put("result", "test ok!");
		return ResponseEntity.ok(map);
	}

	@PostMapping("/add")
	@BeforeExecution(id = "ss-nas-manager", operation = "*:+1:*:*:*:*:*:*:*")
	@AfterExecution(id = "ss-nas-manager", operation = "*:-1:*:*:*:*:*:*:*")
	public ResponseEntity<Map<String, Object>> add(@RequestBody EntityServiceProperties entry) {
		try {
			LOGGER.info("<SERVICE_PROPERTIES> add CALLED");
			service.save(entry);
			return Utils.responseOk().addParams("entry", entry).addParams("response", "OK").get();
		} catch (Exception exc) {
			return Utils.responseError(exc);
		}
	}

	@GetMapping("/get-all")
	@BeforeExecution(id = "ss-nas-manager", operation = "*:*:+1:*:*:*:*:*:*")
	@AfterExecution(id = "ss-nas-manager", operation = "*:*:-1:*:*:*:*:*:*")
	public List<EntityServiceProperties> getAll() {
		LOGGER.info("<SERVICE_PROPERTIES> getAll CALLED");
		return service.getAll();
	}

	@GetMapping("/get/{name}")
	@BeforeExecution(id = "ss-nas-manager", operation = "*:*:*:+1:*:*:*:*:*")
	@AfterExecution(id = "ss-nas-manager", operation = "*:*:*:-1:*:*:*:*:*")
	public EntityServiceProperties getByName(@PathVariable("name") String name) {
		LOGGER.info("<SERVICE_PROPERTIES> getByName CALLED - name: " + name);
		return service.getByName(name);
	}

	@GetMapping("/exists/{name}")
	@BeforeExecution(id = "ss-nas-manager", operation = "*:*:*:*:+1:*:*:*:*")
	@AfterExecution(id = "ss-nas-manager", operation = "*:*:*:*:-1:*:*:*:*")
	public boolean existsByName(@PathVariable("name") String name) {
		LOGGER.info("<SERVICE_PROPERTIES> existsByName CALLED - name: " + name);
		return service.existsByName(name);
	}

	@DeleteMapping("/delete-by-name/{name}")
	@BeforeExecution(id = "ss-nas-manager", operation = "*:*:*:*:*:+1:*:*:*")
	@AfterExecution(id = "ss-nas-manager", operation = "*:*:*:*:*:-1:*:*:*")
	public void deleteByName(@PathVariable("name") String name) {
		LOGGER.info("<SERVICE_PROPERTIES> deleteByName CALLED - name: " + name);
		service.deleteByName(name);
	}

	@DeleteMapping("/delete-all")
	@BeforeExecution(id = "ss-nas-manager", operation = "*:*:*:*:*:*:+1:*:*")
	@AfterExecution(id = "ss-nas-manager", operation = "*:*:*:*:*:*:-1:*:*")
	public void deleteAll() {
		LOGGER.info("<SERVICE_PROPERTIES> deleteAll CALLED");
		service.deleteAll();
	}
}
