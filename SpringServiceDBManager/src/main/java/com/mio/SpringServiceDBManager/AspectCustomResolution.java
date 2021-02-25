package com.mio.SpringServiceDBManager;

import static com.mio.aop.utils.Escaper.escapeURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mio.SpringCustomAspectBaseLib.AspectCustomResolutionBase;
import com.mio.SpringServiceDBManager.table.operation.ServiceOperation;
import com.mio.aop.utils.AfterExecution;
import com.mio.aop.utils.BeforeExecution;

@Component
public class AspectCustomResolution extends AspectCustomResolutionBase {

	@SuppressWarnings("unused")
	private static final Log LOGGER = LogFactory.getLog(AspectCustomResolution.class);

	@Autowired
	private ScheduledTasks scheduledTasks;

	@Autowired
	private ServiceOperation serviceOperation;

	@Override
	public void before(BeforeExecution anno) {
		String name = anno.id();
		String ope = escapeURL(anno.operation());
		Utils.begin(scheduledTasks, serviceOperation, name, ope);
	}

	@Override
	public void after(AfterExecution anno) {
		String name = anno.id();
		String ope = escapeURL(anno.operation());
		Utils.end(serviceOperation, name, ope);
	}
}
