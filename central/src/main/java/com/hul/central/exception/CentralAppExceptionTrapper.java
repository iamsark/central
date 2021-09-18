package com.hul.central.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 
 * Exception trapper for any exception happens during the normal Http req - res cycle<br>
 * This also determine whether the request that is being served is Normal or Ajax and respond accordingly
 * 
 * @author  Sark
 * @since   1.0.0
 * 
 **/

@EnableWebMvc
@ControllerAdvice(basePackages = {"com.hul.central"})
public class CentralAppExceptionTrapper {

}
