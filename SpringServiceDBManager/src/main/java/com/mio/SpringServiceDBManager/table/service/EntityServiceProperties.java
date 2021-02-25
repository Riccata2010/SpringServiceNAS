package com.mio.SpringServiceDBManager.table.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.lang.NonNull;

/**
 * Classe Entity per remap su DB dei servizi attivi con dati generici per
 * ricordarsi ultima volta startup ecc...
 */
@Entity
@Table
public class EntityServiceProperties {

	@Id
	@Column
	private String serviceName = null;
	@Column
	private String serviceDesc = null;
	@Column
	private String lastStarting = null;
	@Column
	private String optional = null;
	@Column
	private String args = null;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(@NonNull String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(@NonNull String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public String getLastStarting() {
		return lastStarting;
	}

	public void setLastStarting(@NonNull String lastStarting) {
		this.lastStarting = lastStarting;
	}

	public String getOptional() {
		return optional;
	}

	public void setOptional(@NonNull String optional) {
		this.optional = optional;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(@NonNull String args) {
		this.args = args;
	}
}
