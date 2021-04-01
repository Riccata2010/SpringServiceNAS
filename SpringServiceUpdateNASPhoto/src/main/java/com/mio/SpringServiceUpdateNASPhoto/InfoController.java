package com.mio.SpringServiceUpdateNASPhoto;

import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping(path = "/ss-nas-photo/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SuppressWarnings("all")
public class InfoController {

	private static final Log LOGGER = LogFactory.getLog(InfoController.class);

	@GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
	public String ping() {
		return "pong!";
	}

	@GetMapping(value = "/info", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ProgressMessage> info() {
		Sinks.Many<ProgressMessage> sink = BeanFactory.newSinks();
		Flux<ProgressMessage> flux = BeanFactory.newFlux(sink);
		Executors.newSingleThreadExecutor().execute(() -> {

			for (int i = 0; i < 10; i++) {
				Utils.sleep(1000);
				ProgressMessage pd = new ProgressMessage();
				pd.setValue("" + i);
				sink.tryEmitNext(pd);
			}
			sink.tryEmitComplete();
		});
		// return Flux.just("a", "b", "c").delayElements(Duration.ofSeconds(1)).log();
		return flux;
	}
}
