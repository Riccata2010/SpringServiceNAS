package com.mio.SpringCustomAspectBaseLib;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseUtils {

	@SuppressWarnings("serial")
	public static class IndexMap extends HashMap<String, Object> {

		private int index = 0;

		public void iput(String k, Object v) {
			put(k + "." + (++index), v);
		}
	}

	public static IndexMap newIndexMap() {
		return new IndexMap();
	}

	public static class Resp {

		private Map<String, Object> response = null;

		public Resp() {
			response = new HashMap<>();
		}

		public Resp addParams(String k, Object v) {
			response.put(k, v);
			return this;
		}

		public Resp addParams(Map<String, Object> map) {
			response.putAll(map);
			return this;
		}

		public ResponseEntity<Map<String, Object>> get() {
			return ResponseEntity.ok(response);
		}
	}

	public static ResponseEntity<Map<String, Object>> responseError(@NonNull Exception exc) {
		Optional.ofNullable(exc).ifPresent(Exception::printStackTrace);
		Map<String, Object> response = new HashMap<>();
		response.put("exc", exc);
		response.put("response", "ERROR");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	public static ResponseEntity<Map<String, Object>> responseWarning(@NonNull String message) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", message);
		response.put("response", "WARNING");
		return ResponseEntity.status(HttpStatus.CONTINUE).body(response);
	}

	public static Resp responseOk() {
		return new Resp();
	}

	@SuppressWarnings("static-access")
	public static void sleep(final int time) {
		try {
			Thread.currentThread().sleep(time);
		} catch (Exception ignore) {
		}
	}

	public static String getEnvOrDefault(final String var, final String def) {
		String val = System.getenv(var);
		return val != null ? val : def;
	}

	public static Integer getEnvOrDefault(final String var, final int def) {
		String val = System.getenv(var);
		return val != null ? Integer.valueOf(val) : def;
	}

	public static Boolean getEnvOrDefault(final String var, final boolean def) {
		String val = System.getenv(var);
		return val != null ? Boolean.valueOf(val) : def;
	}

	public static Properties getPropertiesAsResources(final String name) throws IOException {
		Properties prop = new Properties();
		prop.load(BaseUtils.class.getClassLoader().getResourceAsStream(name));
		return prop;
	}

	public static String makeLine(final char sep, String... args) {
		String line = Arrays.stream(args).reduce("",
				(partialString, element) -> partialString + ((partialString.length() == 0) ? "" : sep) + element);
		return line;
	}

	public static Map<String, Object> fromFormattedLineToMap(final char sep, final String line) {
		Map<String, Object> map = Arrays.stream(line.split("[" + sep + "]"))
				.collect(Collectors.toMap(p -> p.substring(0, p.indexOf("=")), p -> p.substring(p.indexOf("=") + 1)));
		return map;
	}
}
