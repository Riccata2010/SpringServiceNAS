package com.mio.SpringServiceUpdateNASPhoto;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Stream;

public class ProgressMessage implements Serializable {

	private static final long serialVersionUID = -87174319595677666L;

	private String value = null;
	private String data = null;
	private String info = null;
	private String file = null;
	private Map<String, String> args = null;

	static ProgressMessage onException(Exception exc) {
		ProgressMessage message = new ProgressMessage();
		message.setInfo("error");
		message.setValue(exc.getMessage());
		message.setData(Stream.of(exc.getStackTrace()).map(StackTraceElement::toString).reduce(String::concat).get());
		return message;
	}

	static ProgressMessage onComplete(String file, String data, String value) {
		ProgressMessage message = new ProgressMessage();
		message.setValue(value);
		message.setData(data);
		message.setFile(file);
		message.setInfo("complete");
		return message;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public void setArgs(Map<String, String> args) {
		this.args = args;
	}
}
