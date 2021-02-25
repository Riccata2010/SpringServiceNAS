package com.mio.SpringServiceDBManager.manager;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterAspectOperation {

	/**
	 * Se voglio sfruttare WebFlux per gestire le richieste di begin ed end
	 * operation (attualmente non usate) devo istruire il framework per lavore con
	 * una mia rotta. In sostanza rimappo i percorsi URL alle mie rest con una
	 * classe handler che fa lei il lavoro.
	 * 
	 * @param handler
	 * @return
	 */
	@Bean
	public RouterFunction<ServerResponse> productsRoute(HandlerAspectOperation handler) {

		return RouterFunctions
				.route(GET("/ss-nas-services/manager/begin-operation_TEST/{name}/{intMask}")
						.and(accept(MediaType.APPLICATION_JSON)), handler::beginOperation_TEST)
				.andRoute(GET("/ss-nas-services/manager/end-operation_TEST/{name}/{intMask}")
						.and(accept(MediaType.APPLICATION_JSON)), handler::endOperation_TEST);

	}
}
