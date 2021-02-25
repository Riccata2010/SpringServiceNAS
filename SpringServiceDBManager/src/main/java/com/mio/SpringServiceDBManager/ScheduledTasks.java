package com.mio.SpringServiceDBManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mio.SpringServiceDBManager.table.operation.EntityOperation;
import com.mio.SpringServiceDBManager.table.operation.ServiceOperation;

@Component
public class ScheduledTasks {

	private static final Log LOGGER = LogFactory.getLog(ScheduledTasks.class);

	private boolean cleanCache = false;

	@Autowired
	private ServiceOperation serviceOperation;

	public boolean isCleanCacheRunning() {
		return cleanCache;
	}

	public void stopCleanCacheRunning() {
		cleanCache = false;
	}

	public void startCleanCacheRunning() {
		cleanCache = true;
	}

	private boolean isRunningAnyOperation() {
		return serviceOperation.getAll().stream().map(EntityOperation::getIntMask)
				.anyMatch(Utils::isAnyOperationRunning);
	}

	@Scheduled(initialDelay = 60000, fixedRate = 120000)
	public void execute() {
		LOGGER.info("ScheduledTasks - isCleanCacheRunning: " + isCleanCacheRunning());
		if (!isCleanCacheRunning() && !isRunningAnyOperation()) {
			startCleanCacheRunning();
			LOGGER.info("ScheduledTasks - NO OPERATION RUNNING - CLEAN CACHE!");
			stopCleanCacheRunning();
		}
	}
}
