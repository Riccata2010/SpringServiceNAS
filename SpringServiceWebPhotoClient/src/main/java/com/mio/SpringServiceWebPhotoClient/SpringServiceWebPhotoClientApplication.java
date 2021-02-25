package com.mio.SpringServiceWebPhotoClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.mio.SpringCustomAspectBaseLib.BaseUtils;

@SpringBootApplication
public class SpringServiceWebPhotoClientApplication implements CommandLineRunner, ApplicationRunner {

	private static final Log LOGGER = LogFactory.getLog(SpringServiceWebPhotoClientApplication.class);

	public static Properties properties = null;

	public static void main(String[] args) throws IOException {
		properties = BaseUtils.getPropertiesAsResources("application.properties");
		String port = Utils.getEnvOrDefault("SERVER_PORT", properties.getProperty("server.port", "8800"));
		args = new String[] { "--server.port=" + port };
		SpringApplication.run(SpringServiceWebPhotoClientApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Optional.ofNullable(args)
				.ifPresent(a -> Arrays.stream(a).peek(i -> LOGGER.info("ARGS: " + i)).forEach(LOGGER::debug));
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Optional.ofNullable(args).ifPresent(a -> {
			Arrays.stream(a.getSourceArgs()).forEach(LOGGER::debug);
		});
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doAfterStartup() {
//		AspectCustomResolution.setManagerPort(getEnvOrDefault("SERVER_MASTER_PORT", 8801));
//		AspectCustomResolution.autoConfigurePorts();
//		AspectCustomResolution.insertService("ss-nas-photo-client", true);
//		AbstractCustomResolution.DEBUG_SLEEP_TIME = Utils.getEnvOrDefault("DEBUG_SLEEP_TIME", 0);
	}
}
