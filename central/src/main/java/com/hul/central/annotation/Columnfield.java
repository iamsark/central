package com.hul.central.annotation;

/**
 * 
 *
 *
 *
 * Custom annotation to handle model objects in db layer.
 * 
 * sp_help tablename;
 * 
 * 
 * possible type names
 * db				java
 * 
 * tinyint			short
 * varchar			String
 * bit				boolean
 * numeric			bigdecimal
 * int				int
 * datetime			Date
 * float			float
 * 
 * 
 *  @author Jagath.M
 * 
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Columnfield {
	 public String name();
	 public String type();
	 public int dpCount() default 5;
}