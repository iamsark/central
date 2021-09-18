package com.hul.central.base;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

public class CentralAppSessionListener implements HttpSessionListener {
	
	public void sessionCreated(HttpSessionEvent se) {
		/* Set session timeout for 15 Minutes */
		se.getSession().setMaxInactiveInterval(30*60);
	}

	public void sessionDestroyed(HttpSessionEvent _se) {
		
	}

}