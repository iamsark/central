package com.hul.central.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hul.central.base.CENTRAL;
import com.hul.central.system.model.CentralAppUser;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Component
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class CentralAppContext {
	
public CentralAppContext() {super();}
	
	private HikariDataSource ds = null;	
	
	public CentralAppUser current() {    		
		HttpServletRequest request = null;		
		if (RequestContextHolder.getRequestAttributes() != null) {
			request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();		   
		}
	    if (request instanceof HttpServletRequest) {
	    	HttpSession session = (HttpSession) request.getSession();
	    	if (session.getAttribute(CENTRAL.PRE_ACCESS_SESSION) != null) {
				/* Pre Access session */
				return (CentralAppUser) session.getAttribute(CENTRAL.PRE_ACCESS_SESSION);
			} else if (session.getAttribute(CENTRAL.APP_SESSION) != null) {
				/* Has App user session */
				return (CentralAppUser) session.getAttribute(CENTRAL.APP_SESSION);
			} else if (session.getAttribute(CENTRAL.ADMIN_SESSION) != null) {
				/* Has Admin user session */
				return (CentralAppUser) session.getAttribute(CENTRAL.ADMIN_SESSION);
			} else if (session.getAttribute(CENTRAL.SYSTEM_SESSION) != null) {
				/* Has System user session */
				return (CentralAppUser) session.getAttribute(CENTRAL.SYSTEM_SESSION);
			}
	    }	
	    return null;		
	}
	
	/**
	 * @return the ds
	 */
	public HikariDataSource getDs() {
		return ds;
	}

	/**
	 * @param ds the ds to set
	 */
	public void setDs(HikariDataSource ds) {
		this.ds = ds;
	}

}
