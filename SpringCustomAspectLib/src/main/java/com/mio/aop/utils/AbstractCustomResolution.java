package com.mio.aop.utils;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public abstract class AbstractCustomResolution {

	public static int DEBUG_SLEEP_TIME = -1;

	public void before(BeforeExecution anno) {
	}

	public void after(AfterExecution anno) {
	}

	@SuppressWarnings("static-access")
	private final void sleepDebug() {
		if (DEBUG_SLEEP_TIME > 0) {
			try {
				Thread.currentThread().sleep(DEBUG_SLEEP_TIME);
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * E' IMPORTANTE I METODI DI ACCESSO CON ASPECTJ DEVONO ESSERE PUBLIC <br>
	 * Per solo quelli di un package specifico:<br>
	 * <br>
	 * @Before("execution(* com.mio.*.table.service.*.*(..))
	 * && @annotation(com.aop.utils.BeforeExecution)")
	 */
	@Before("@annotation(com.mio.aop.utils.BeforeExecution)")
	public final void beforeExecution(JoinPoint jp) throws Throwable {

		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method method = signature.getMethod();
		before(method.getAnnotation(BeforeExecution.class));

		if (DEBUG_SLEEP_TIME > 0) {
			sleepDebug();
		}
	}

	@After("@annotation(com.mio.aop.utils.AfterExecution)")
	public final void afterExecution(JoinPoint jp) throws Throwable {

		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method method = signature.getMethod();
		after(method.getAnnotation(AfterExecution.class));

		if (DEBUG_SLEEP_TIME > 0) {
			sleepDebug();
		}
	}
}
