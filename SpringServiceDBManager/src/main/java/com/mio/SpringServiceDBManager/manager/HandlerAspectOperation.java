package com.mio.SpringServiceDBManager.manager;

import static com.mio.SpringServiceDBManager.Utils.removeWaiting;
import static com.mio.SpringServiceDBManager.Utils.updateOperation;
import static com.mio.SpringServiceDBManager.Utils.waiting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mio.SpringServiceDBManager.ScheduledTasks;
import com.mio.SpringServiceDBManager.table.operation.ServiceOperation;

import reactor.core.publisher.Mono;

@Component
public class HandlerAspectOperation {

	static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

	@Autowired
	private ScheduledTasks scheduledTasks;

	@Autowired
	private ServiceOperation serviceOperation;

	@Value("${server.port}")
	private String serverPort;

	/**
	 * Questa versione usa WebFlux per gestire le operazioni di begin ed end ma
	 * attenzione non sono sincrone! Attualmente non usata.
	 * 
	 * @param serverRequest
	 * @return
	 */
	public Mono<ServerResponse> beginOperation_TEST(ServerRequest serverRequest) {

		System.out.println("<HANDLER> " + Thread.currentThread().getName() + " - {beginOperation} - serverRequest: "
				+ serverRequest);

		String name = serverRequest.pathVariable("name");
		String intMask = serverRequest.pathVariable("intMask");

		String idWaiting = null;

		while (scheduledTasks.isCleanCacheRunning()) {
			if (idWaiting == null) {
				idWaiting = waiting(name);
			}
		}

		if (idWaiting != null) {
			removeWaiting(idWaiting);
		}

		scheduledTasks.stopCleanCacheRunning();

		updateOperation(name, intMask, serviceOperation);

		return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("OK"), String.class);
	}

	public Mono<ServerResponse> endOperation_TEST(ServerRequest serverRequest) {

		System.out.println("<HANDLER> " + Thread.currentThread().getName() + " - {endOperation} - serverRequest: "
				+ serverRequest);

		String name = serverRequest.pathVariable("name");
		String intMask = serverRequest.pathVariable("intMask");

		updateOperation(name, intMask, serviceOperation);

		scheduledTasks.startCleanCacheRunning();

		return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("OK"), String.class);
	}
}
