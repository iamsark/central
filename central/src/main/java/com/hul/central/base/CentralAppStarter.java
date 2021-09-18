package com.hul.central.base;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class CentralAppStarter { 

	
	
	/**
	 * 
	 */
	public CentralAppStarter() {
		super();
		System.out.println("Inside on CentralAppStarter constructor");
	}

	public void onStartup(ServletContext servletContext) throws ServletException { System.out.println("Inside on Startup of CentralAppStarter");
		
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		
		context.register(CentralAppBaseConfig.class);
		servletContext.addListener(new ContextLoaderListener(context));
		
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(context));

		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
				
	}

}
