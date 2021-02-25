package com.mio.SpringServiceDBManager;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/ss-nas-manager")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ShutdownController implements ApplicationContextAware {

	private ApplicationContext context;

	@PostMapping("/shutdown")
	public void shutdown() {
		System.out.println("{shutdown}");
		((ConfigurableApplicationContext) context).close();
		System.exit(0);
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.context = ctx;
	}
}
