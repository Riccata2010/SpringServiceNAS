package com.mio.SpringServiceDBManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Ho usato il db H2 e volevo vedre la cosole, ma dato che ho anche inserito
 * nelle dipendenze WebFlux (per gestire in moto reactive non blocking le
 * richieste) allora H2 non fa vedere la console. Per superare il problema
 * faccio partire un secondo server alla porta 8081.
 * 
 * Per collegarsi usare: http://localhost:8081
 *
 * NOTA: prima h2 era come dipendenza sono a runtime ora non piu!!! (vedi il
 * file pom.xml)
 * 
 * In sostanza questo componente parte allo startup.
 */
@Component
@Profile("h2")
public class ConfigurationBase {

	private static final Log LOGGER = LogFactory.getLog(ConfigurationBase.class);

	private Server webServer;

	@Value("${webclientexample.postsapi.h2-console-port}")
	Integer h2ConsolePort;

	@EventListener(ContextRefreshedEvent.class)
	public void start() throws java.sql.SQLException {
		LOGGER.info("starting h2 console at port " + h2ConsolePort);
		this.webServer = org.h2.tools.Server.createWebServer("-webPort", h2ConsolePort.toString(), "-tcpAllowOthers").start();
	}

	@EventListener(ContextClosedEvent.class)
	public void stop() {
		LOGGER.info("stopping h2 console at port " + h2ConsolePort);
		this.webServer.stop();
	}
}
