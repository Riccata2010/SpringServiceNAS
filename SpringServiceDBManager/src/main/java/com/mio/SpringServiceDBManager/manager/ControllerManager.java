package com.mio.SpringServiceDBManager.manager;

import static com.mio.SpringServiceDBManager.Utils.responseOk;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mio.SpringServiceDBManager.AspectCustomResolution;
import com.mio.SpringServiceDBManager.ScheduledTasks;
import com.mio.SpringServiceDBManager.Utils;
import com.mio.SpringServiceDBManager.table.operation.ServiceOperation;

@RestController
@RequestMapping(path = "/ss-nas-manager/manager")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SuppressWarnings("all")
public class ControllerManager {

	private static final Log LOGGER = LogFactory.getLog(ControllerManager.class);

	@Autowired
	private ScheduledTasks scheduledTasks;

	@Autowired
	private ServiceOperation serviceOperation;

	@Value("${server.port}")
	private String serverPort;

	@GetMapping("/ports")
	private ResponseEntity<Map<String, Object>> ports() {
		LOGGER.info("<MANAGER> ports CALLED");
		return responseOk().addParams("manager.port", AspectCustomResolution.getManagerPort())
				.addParams("ope.begin.port", AspectCustomResolution.getManagerPortBegin())
				.addParams("ope.end.port", AspectCustomResolution.getManagerPortEnd()).addParams("response", "OK")
				.get();
	}

	@GetMapping("/begin-operation/{name}/{intMask}")
	public ResponseEntity<Map<String, Object>> beginOperation(@PathVariable("name") String name,
			@PathVariable("intMask") String intMask) {
		LOGGER.info("<MANAGER> beginOperation CALLED - name: " + name);
		Utils.begin(scheduledTasks, serviceOperation, name, intMask);
		return Utils.responseOk().addParams("response", "OK").get();
	}

	@GetMapping("/end-operation/{name}/{intMask}")
	public ResponseEntity<Map<String, Object>> endOperation(@PathVariable("name") String name,
			@PathVariable("intMask") String intMask) {
		LOGGER.info("<MANAGER> endOperation CALLED - name: " + name);
		Utils.end(serviceOperation, name, intMask);
		return Utils.responseOk().addParams("response", "OK").get();
	}
}
