package com.mio.aop.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("rawtypes")
public @interface BeforeExecution {

	public String id() default "";

	public String name() default "";

	public String operation() default "";

	public String data() default "";

	public String value() default "";

	public String args() default "";

	public Class classData() default Object.class;

	public boolean status() default false;

	public boolean flag() default false;

	public int intvalue() default -1;

	public short shortvalue() default -1;

	public long longvalue() default -1;

	public float floatvalue() default -1;

	public double doublevalue() default -1;
}
