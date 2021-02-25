package com.mio.SpringServiceDBManager;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mio.SpringCustomAspectBaseLib.BaseUtils;
import com.mio.aop.utils.AbstractCustomResolution;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringServiceDbManagerApplication implements ApplicationRunner {

	private static final Log LOGGER = LogFactory.getLog(SpringServiceDbManagerApplication.class);

	@Autowired
	private HttpHandler httpHandler;

	private WebServer httpBeginOperation;
	private WebServer httpEndOperation;

	@Value("${server.port}")
	private String serverPort;

	public static Properties properties = null;

	public static void main(String[] args) throws IOException {
		properties = BaseUtils.getPropertiesAsResources("application.properties");
		String port = Utils.getEnvOrDefault("SERVER_PORT", properties.getProperty("server.port", "8801"));
		args = new String[] { "--server.port=" + port };
		SpringApplication.run(SpringServiceDbManagerApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
	}

	@PostConstruct
	public void start() {

		serverPort = Utils.getEnvOrDefault("SERVER_PORT", serverPort);
		String sbe = Utils.getEnvOrDefault("SERVER_PORT_BEGIN", serverPort);
		String sen = Utils.getEnvOrDefault("SERVER_PORT_END", serverPort);
		boolean auto = Utils.getEnvOrDefault("AUTO_PORTS", true);

		LOGGER.info("SERVER_PORT       : " + serverPort);
		LOGGER.info("SERVER_PORT_BEGIN : " + sbe);
		LOGGER.info("SERVER_PORT_END   : " + sen);
		LOGGER.info("AUTO_PORTS        : " + auto);

		AspectCustomResolution.setManagerPort(Integer.parseInt(serverPort));

		int bport = auto ? Integer.parseInt(serverPort) + 1 : Integer.parseInt(sbe);
		int eport = auto ? Integer.parseInt(serverPort) + 2 : Integer.parseInt(sen);

		ReactiveWebServerFactory factory1 = new NettyReactiveWebServerFactory(bport);
		this.httpBeginOperation = factory1.getWebServer(this.httpHandler);
		this.httpBeginOperation.start();

		ReactiveWebServerFactory factory2 = new NettyReactiveWebServerFactory(eport);
		this.httpEndOperation = factory2.getWebServer(this.httpHandler);
		this.httpEndOperation.start();

		AspectCustomResolution.setManagerPortBegin(bport);
		AspectCustomResolution.setManagerPortEnd(eport);
		AbstractCustomResolution.DEBUG_SLEEP_TIME = Utils.getEnvOrDefault("DEBUG_SLEEP_TIME", 0);

		LOGGER.info("-------------------------------------------------");
		LOGGER.info("MANAGER RUNNING AT         : " + serverPort);
		LOGGER.info("OPERATION MANAGEMENT BEGIN : " + bport);
		LOGGER.info("OPERATION MANAGEMENT END   : " + eport);
		LOGGER.info("DEBUG SLEEP TIME           : " + AbstractCustomResolution.DEBUG_SLEEP_TIME);
		LOGGER.info("-------------------------------------------------");
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doAfterStartup() {
		AspectCustomResolution.insertService("ss-nas-manager", true);
	}

	@PreDestroy
	public void stop() {
		this.httpBeginOperation.stop();
		this.httpEndOperation.stop();
	}
}
