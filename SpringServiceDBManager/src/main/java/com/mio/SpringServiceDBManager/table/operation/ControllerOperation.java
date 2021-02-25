package com.mio.SpringServiceDBManager.table.operation;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mio.SpringServiceDBManager.Utils;

@RestController
@RequestMapping(path = "/ss-nas-manager/operation")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SuppressWarnings("all")
public class ControllerOperation {

	private static final Log LOGGER = LogFactory.getLog(ControllerOperation.class);

	@Autowired
	private ServiceOperation service;

	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> add(@RequestBody EntityOperation entry) {
		try {
			service.save(entry);
			return Utils.responseOk().addParams("entry", entry).addParams("response", "OK").get();
		} catch (Exception exc) {
			return Utils.responseError(exc);
		}
	}

	@GetMapping("/get-int-mask/{name}")
	private ResponseEntity<Map<String, Object>> getIntMaskByName(@PathVariable("name") String name) {
		String intMask = null;
		try {
			EntityOperation eo = service.getByName(name);
			if (Objects.nonNull(eo)) {
				intMask = eo.getIntMask();
				return Utils.responseOk().addParams("intMask", intMask).addParams("response", "OK").get();
			}
			return Utils.responseWarning(name + " not found!");
		} catch (Exception exc) {
			return Utils.responseError(exc);
		}
	}

	@PutMapping("/set-int-mask/{name}/{bitMask}")
	private ResponseEntity<Map<String, Object>> setIntMaskByName(@PathVariable("name") String name,
			@PathVariable("intMask") String intMask) {
		try {
			EntityOperation eo = service.getByName(name);
			if (Objects.nonNull(eo) && Objects.nonNull(intMask)) {
				eo.setIntMask(intMask);
				service.save(eo);
				return Utils.responseOk().addParams("response", "OK").get();
			}
			return Utils.responseWarning("int mask not setted!");
		} catch (Exception exc) {
			return Utils.responseError(exc);
		}
	}

	@GetMapping("/get/{name}")
	private EntityOperation getByName(@PathVariable("name") String name) {
		return service.getByName(name);
	}

	@GetMapping("/exists/{name}")
	private boolean existsByName(@PathVariable("name") String name) {
		return service.existsByName(name);
	}

	@DeleteMapping("/delete-by-name/{name}")
	private ResponseEntity<Map<String, Object>> deleteByName(@PathVariable("name") String name) {
		try {
			service.deleteByName(name);
			return Utils.responseOk().addParams("response", "OK").get();
		} catch (Exception exc) {
			return Utils.responseError(exc);
		}
	}

	@DeleteMapping("/delete-all")
	private void deleteAll() {
		service.deleteAll();
	}
}
