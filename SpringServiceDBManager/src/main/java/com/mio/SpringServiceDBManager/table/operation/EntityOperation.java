package com.mio.SpringServiceDBManager.table.operation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Classe Entity per remap su DB dei servizi attivi con dati generici per
 * ricordarsi ultima volta startup ecc...
 */
@Entity
@Table
public class EntityOperation {

	@Id
	@Column
	private String serviceName = null;
	@Column
	private String intMask = null;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getIntMask() {
		return intMask;
	}

	public void setIntMask(String b) {
		this.intMask = b;
	}

	@Override
	public String toString() {
		return "EntityOperation [serviceName=" + serviceName + ", intMask=" + intMask + "]";
	}
}
