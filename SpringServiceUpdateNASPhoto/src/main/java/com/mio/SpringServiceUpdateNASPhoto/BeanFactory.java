package com.mio.SpringServiceUpdateNASPhoto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class BeanFactory {

	@Bean
	public static Sinks.Many<ProgressMessage> newSinks() {
		return Sinks.many().replay().latest();
	}

	@Bean
	public static Flux<ProgressMessage> newFlux(Sinks.Many<ProgressMessage> sink) {
		return sink.asFlux();
	}
}
