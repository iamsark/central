package com.hul.central.annotation;

/**
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
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableRef {
	 public String name();
}