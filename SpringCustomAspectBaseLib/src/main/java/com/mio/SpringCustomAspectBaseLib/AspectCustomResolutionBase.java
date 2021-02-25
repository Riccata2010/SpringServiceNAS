package com.mio.SpringCustomAspectBaseLib;

import static com.mio.aop.utils.Escaper.escapeURL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mio.aop.utils.AbstractCustomResolution;
import com.mio.aop.utils.AfterExecution;
import com.mio.aop.utils.BeforeExecution;

import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class AspectCustomResolutionBase extends AbstractCustomResolution {

	public static String managerAddress = "localhost";
	public static int managerPort = 8801;
	public static int managerPortBegin = 8802;
	public static int managerPortEnd = 8803;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
	private static WebClient clientBeforeOpe = null;
	private static WebClient clientAfterOpe = null;

	private static final Log LOGGER = LogFactory.getLog(AspectCustomResolutionBase.class);

	/**
	 * Ogni microservizio ha una classe che estende questa e puo' auto configurarsi
	 * le proprie porte per comunicare col manager in base ad una GET che viene
	 * fatta al menager stesso e che ritorna le porte da usare.
	 */
	public static void autoConfigurePorts() {

		// Per evitare che alla compilazione si attivi la registrazione
		if (BaseUtils.getEnvOrDefault("RUNNING_MODE", 0) == 0) {
			return;
		}

		boolean result = repeatUntil(120, 1000, () -> {
			try {
				String json = getManagerPorts();
				if (json != null) {

					json = json.replace("{", "").replace("}", "").replace("\"", "");
					Map<String, String> map = Arrays.stream(json.split("\\,")).collect(Collectors
							.toMap(k -> k.substring(0, k.indexOf(":")), v -> v.substring(v.indexOf(":") + 1)));

					setManagerPort(Integer.parseInt(map.get("manager.port")));
					setManagerPortBegin(Integer.parseInt(map.get("ope.begin.port")));
					setManagerPortEnd(Integer.parseInt(map.get("ope.end.port")));

					LOGGER.info("ManagerPort       : " + getManagerPort());
					LOGGER.info("ManagerPortBegin  : " + getManagerPortBegin());
					LOGGER.info("ManagerPortEnd    : " + getManagerPortEnd());

					return true;
				}
			} catch (Exception exc) {
			}
			return false;
		});
		LOGGER.info("autoConfigurePorts - result : " + result);
	}

	/**
	 * Ripete una certa funzione un tot di volte con un tempo di attesa se e'
	 * ritornata false (tutto male)
	 */
	public static boolean repeatUntil(int max, final int time, Supplier<Boolean> sup) {
		boolean repeat = false;
		while (!(repeat = sup.get()) && (--max) > 0) {
			sleep(time);
		}
		return repeat;
	}

	public static void insertService(final String name, final boolean trace) {

		// Per evitare che alla compilazione si attivi la registrazione
		if (BaseUtils.getEnvOrDefault("RUNNING_MODE", 0) == 0) {
			return;
		}

		Executors.newSingleThreadExecutor().execute(() -> repeatUntil(120, 1000, () -> {
			try {
				String url = "http://" + managerAddress + ":" + managerPort + "/ss-nas-manager/service/add";
				String result = postForObject(url, formatServiceRequest(name));
				return result != null && result.indexOf("response=OK") != 0;
			} catch (Exception exc) {
				if (trace) {
					LOGGER.warn("EXC: " + exc);
				}
			}
			return false;
		}));
	}

	private static class Request {

		String serviceName = "";
		String serviceDesc = "";
		String lastStarting = "";
		String optional = "";
		String args = "";

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public String getServiceDesc() {
			return serviceDesc;
		}

		public void setServiceDesc(String serviceDesc) {
			this.serviceDesc = serviceDesc;
		}

		public String getLastStarting() {
			return lastStarting;
		}

		public void setLastStarting(String lastStarting) {
			this.lastStarting = lastStarting;
		}

		public String getOptional() {
			return optional;
		}

		public void setOptional(String optional) {
			this.optional = optional;
		}

		public String getArgs() {
			return args;
		}

		public void setArgs(String args) {
			this.args = args;
		}

		static Request make(final String name) {
			Request rq = new Request();
			rq.serviceName = name;
			rq.lastStarting = LocalDate.now().format(FORMATTER);
			return rq;
		}
	}

	public static String formatServiceRequest(final String name) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(Request.make(name));
	}

	@SuppressWarnings("static-access")
	public static void sleep(final int time) {
		try {
			Thread.currentThread().sleep(time);
		} catch (Exception ignore) {
		}
	}

	public static int getManagerPort() {
		return managerPort;
	}

	public static void setManagerPort(int managerPort) {
		AspectCustomResolutionBase.managerPort = managerPort;
	}

	public static String getManagerAddress() {
		return managerAddress;
	}

	public static void setManagerAddress(String managerAddress) {
		AspectCustomResolutionBase.managerAddress = managerAddress;
	}

	public static int getManagerPortBegin() {
		return managerPortBegin;
	}

	public static void setManagerPortBegin(int managerPortBegin) {
		AspectCustomResolutionBase.managerPortBegin = managerPortBegin;
	}

	public static int getManagerPortEnd() {
		return managerPortEnd;
	}

	public static void setManagerPortEnd(int managerPortEnd) {
		AspectCustomResolutionBase.managerPortEnd = managerPortEnd;
	}

	public static void getWebFlux(boolean before, String uri) {

		if (clientBeforeOpe == null) {
			clientBeforeOpe = WebClient.builder().baseUrl("http://" + managerAddress + ":" + managerPortBegin)
					.defaultCookie("cookieKey", "cookieValue").defaultHeader(HttpHeaders.CONTENT_TYPE,
							MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE)
					.build();
		}

		if (clientAfterOpe == null) {
			clientAfterOpe = WebClient.builder().baseUrl("http://" + managerAddress + ":" + managerPortEnd)
					.defaultCookie("cookieKey", "cookieValue").defaultHeader(HttpHeaders.CONTENT_TYPE,
							MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE)
					.build();
		}

		WebClient client = before ? clientBeforeOpe : clientAfterOpe;

		client.get().uri(uri).exchangeToMono(response -> {
			System.out.println("-------------------- response: " + response);
			return Mono.empty();
		}).subscribe(c -> {
			System.out.println("-------------------- c: " + c);
		});
	}

	public static String getForObject(String url) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, String.class);
	}

	public static String postForObject(String url, String body) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);
		return restTemplate.postForObject(url, entity, String.class);
	}

	public void webClientCall_TEST(BeforeExecution anno) {
		WebClient client = WebClient.builder().baseUrl("http://" + managerAddress + ":" + managerPortBegin)
				.defaultCookie("cookieKey", "cookieValue")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
		LOGGER.info("after XXX: " + client.get().uri("/end-operation/" + anno.id() + "/" + anno.operation()).retrieve()
				.bodyToMono(String.class).block());
	}

	@Override
	public void before(BeforeExecution anno) {
		if (!BaseUtils.getEnvOrDefault("ENABLE_INTERCEPT_AOP", true))
			return;
		String name = anno.id();
		String ope = escapeURL(anno.operation());
		String uri = "http://" + managerAddress + ":" + managerPortBegin + "/ss-nas-manager/manager/begin-operation/"
				+ name + "/" + ope;
		getForObject(uri);
	}

	@Override
	public void after(AfterExecution anno) {
		if (!BaseUtils.getEnvOrDefault("ENABLE_INTERCEPT_AOP", true))
			return;
		after(anno.id(), anno.operation());
	}

	/**
	 * Con la gestione asincrona di WebFlux non posso usare l'AOP After perche' in
	 * modo asincrono mi scatta subito il metodo (non blocking) e quindi lo invio
	 * con una statica.
	 * 
	 * @param name
	 * @param ope
	 */
	public static void after(String name, String ope) {
		ope = escapeURL(ope);
		String uri = "http://" + managerAddress + ":" + managerPortEnd + "/ss-nas-manager/manager/end-operation/" + name
				+ "/" + ope;
		getForObject(uri);
	}

	public static String getManagerPorts() {
		String url = "http://" + managerAddress + ":" + managerPort + "/ss-nas-manager/manager/ports";
		return getForObject(url);
	}
}
