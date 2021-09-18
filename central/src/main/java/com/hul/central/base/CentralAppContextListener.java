package com.hul.central.base;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import java.time.Instant;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hul.central.db.CentralAppConnectionPool;
import com.hul.central.messenger.CentralAppWebSocket;
import com.hul.central.messenger.CentralAppWebSocketConfig;
import com.hul.central.utils.CentralAppLogger;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

public class CentralAppContextListener implements ServletContextListener {

	private CentralAppLogger logger = null;
	private CentralAppConnectionPool ccp = null;
	
	@Override
	public void contextInitialized(ServletContextEvent _sce) { 
		
		/* Get the IkeaLogger instance from Bean Factory
		 * Since Auto wiring won't work here */
		this.logger = WebApplicationContextUtils.getRequiredWebApplicationContext(_sce.getServletContext()).getBean(CentralAppLogger.class);
		/* Set the server start time */
		if (this.logger != null) {
			this.logger.setStartTime(Instant.now().getEpochSecond());
		}	
		
		this.ccp =  WebApplicationContextUtils.getRequiredWebApplicationContext(_sce.getServletContext()).getBean(CentralAppConnectionPool.class);
		/* initiating connection pool for IkeaACM db */
		if (this.ccp != null) {
			try{
				this.ccp.initiateCp();
			}catch(Exception e){
				if (this.logger != null) {
					this.logger.elog(e);
				} else {
					/* Looks like IkeaLogger not ready */
					e.printStackTrace();
				}
			}
		}
		
		
		/* Now deploy the Web Socket end point */
		final ServerContainer sc = (ServerContainer) _sce.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
		try {
			sc.addEndpoint(new CentralAppWebSocketConfig(CentralAppWebSocket.class, CENTRAL.WEBSOCKET_CONTEXT_PATH));
		} catch (DeploymentException e) {
			if (this.logger != null) {
				this.logger.elog(e);
			} else {
				/* Looks like IkeaLogger not ready */
				e.printStackTrace();
			}
		}		
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent _sce) {
		
		if(this.ccp != null ){
			try{
				this.ccp.closeCP();
			}catch(Exception e){
				if (this.logger != null) {
					this.logger.elog(e);
				} else {
					/* Looks like IkeaLogger not ready */
					e.printStackTrace();
				}
			}
		}
		System.out.println("All System connections are closed");
				
		/* Notify the logger demon that context is about to be destroyed
		 * Thus flushing any pending log entries to the file */
		if (this.logger != null) {
			this.logger.stopDemon();
		}
		System.out.println("Logger is stopped");
		
	}
	
}
