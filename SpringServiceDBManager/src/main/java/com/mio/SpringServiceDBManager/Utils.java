package com.mio.SpringServiceDBManager;

import static com.mio.aop.utils.Escaper.unescapeURLWith0;

import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mio.SpringCustomAspectBaseLib.BaseUtils;
import com.mio.SpringServiceDBManager.table.operation.EntityOperation;
import com.mio.SpringServiceDBManager.table.operation.ServiceOperation;

public class Utils extends BaseUtils {

	private static final Log LOGGER = LogFactory.getLog(Utils.class);
	private static final Lock lockWrite = new ReentrantLock();
	private static final Queue<String> waitingRequests = new ConcurrentLinkedQueue<String>();

	public static boolean isAnyOperationRunning(String intMask) {
		StringTokenizer st = new StringTokenizer(intMask, ":");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if (tok.equals("0")) {
				continue;
			} else {
				return true;
			}
		}
		return false;
	}

	public static String updateIntMask(String oldMask, String newMask) {
		String[] oldm = oldMask.split(":");
		String[] newm = newMask.split(":");
		String ris = "";
		for (int i = 0; i < oldm.length; i++) {
			int x = Integer.parseInt(oldm[i]);
			int y = Integer.parseInt(newm[i]);
			ris += (x + y);
			ris += (i + 1 >= oldm.length) ? "" : ":";
		}
		return ris;
	}

	public static int getNumberOperationInPosition(String name, String intMask, ServiceOperation serviceOperation) {

		if (!serviceOperation.existsByName(name))
			return 0;

		EntityOperation ope = serviceOperation.getByName(name);
		String current = ope.getIntMask();
		String[] in = intMask.split("\\:");
		for (int i = 0; i < in.length; i++) {
			if (in[i].equals("0"))
				continue;
			return Integer.parseInt(current.split("\\:")[i]);
		}

		return -1;
	}

	/**
	 * L'idea di base e' che: vista dal punto di vista del ManagerDB (il solo ad
	 * accedere al DB) gli altri microservizi espongono una stringa del tipo
	 * "*:*:*:*" dove la posizione degli '*' e' una precisa funzione, poi quando la
	 * stringa diventa per esempio "0:2:1:0" significa che un certo servizio ha la
	 * seconda funzione attiva due volte e la terza 1, zero le altre. Cosi' facendo
	 * guarcando il DB posso sapere chi fa cosa e quanto.
	 */
	public static void updateOperation(String name, String intMask, ServiceOperation serviceOperation) {

		try {

			lockWrite.lock();

			EntityOperation ope = null;

			if (!serviceOperation.existsByName(name)) {
				ope = BeanFactory.makeEntityOperation();
				ope.setServiceName(name);
				ope.setIntMask(unescapeURLWith0(intMask).replace("+", ""));
			} else {
				ope = serviceOperation.getByName(name);
				intMask = updateIntMask(ope.getIntMask(), unescapeURLWith0(intMask));
				ope.setIntMask(intMask);
			}

			serviceOperation.save(ope);

		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			lockWrite.unlock();
		}
	}

	public static void begin(ScheduledTasks scheduledTasks, ServiceOperation serviceOperation, String name,
			String intMask) {

		LOGGER.info("{begin} for " + name);
		String idWaiting = null;

		while (scheduledTasks.isCleanCacheRunning()) {
			if (idWaiting == null) {
				idWaiting = waiting(name);
			}
			System.out.println("...waiting");
		}

		if (idWaiting != null) {
			removeWaiting(idWaiting);
		}

		updateOperation(name, intMask, serviceOperation);
	}

	public static void end(ServiceOperation serviceOperation, String name, String intMask) {
		LOGGER.info("{end} for " + name);
		updateOperation(name, intMask, serviceOperation);
	}

	public static Queue<String> getWaitingRequests() {
		return waitingRequests;
	}

	public static String waiting(String name) {
		String id = name + "-" + System.currentTimeMillis() + "-" + Thread.currentThread().getName();
		waitingRequests.offer(id);
		return id;
	}

	public static void removeWaiting(String idWaiting) {
		waitingRequests.remove(idWaiting);
	}
}
