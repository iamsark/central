package com.hul.central.base;

import java.util.HashSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionTrackingMode;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * 
 * Bootstrap module for CentralApp<br>
 * This java based configuration approach effectively eliminates all the XML files ( including web.xml )<br>
 * The whole project will very minimal and easy to maintain. There are five end points<br><br>
 * 
 * <i>/central/app/*</i> - This will be used by the Application modules (both transactions as well as masters)<br>
 * <i>/central/system/*</i> - This will be used only by the Authentication Module<br>
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

public class CentralAppBootstrap extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { CentralAppBaseConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	@Override
    protected void registerDispatcherServlet(ServletContext _context) {  
		
		/* Set COOKIE as Session Tracking mode */
        HashSet<SessionTrackingMode> set = new HashSet<SessionTrackingMode>();
        set.add(SessionTrackingMode.COOKIE);
        _context.setSessionTrackingModes(set);
        
        /* Add session listener */
        _context.addListener(new CentralAppSessionListener());
        
        /* Add context listener */
        _context.addListener(new CentralAppContextListener());
        
        /* Must one */
        super.registerDispatcherServlet(_context);
		
	}

}
