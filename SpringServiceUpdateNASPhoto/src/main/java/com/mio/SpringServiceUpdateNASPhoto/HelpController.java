package com.mio.SpringServiceUpdateNASPhoto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PropertySource("classpath:help.properties")
public class HelpController {

	private static final Log log = LogFactory.getLog(HelpController.class);

	@Value("${help.message}")
	private String helpMessage;

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	private Environment env;

	@RequestMapping("/ss-nas-photo/help")
	private String help() {
		log.info("\n-- HELP CALLED --\n");
		log.info("env test: " + env.getProperty("mio.test", "not-set"));
		return helpMessage.replace("{server.port}", serverPort);
	}
}
