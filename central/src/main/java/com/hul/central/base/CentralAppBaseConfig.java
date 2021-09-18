package com.hul.central.base;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan("com.hul.central")
public class CentralAppBaseConfig implements WebMvcConfigurer {

	/**
	 * 
	 * View mapping
	 * 
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		
		/**
		 * 
		 * Default to login page
		 *  
		 **/
        registry.addViewController("/").setViewName("index");
        
        /**
         * 
         * Map main application view
         * 
         **/
        registry.addViewController(CENTRAL.APP_CONTEXT_PATH).setViewName(CENTRAL.APP_VIEW);
        
        /**
         * 
         * Map system view (Administrator View)
         * 
         **/
        registry.addViewController(CENTRAL.SYSTEM_CONTEXT_PATH).setViewName(CENTRAL.SYSTEM_VIEW);
        
    }
	
	/**
	 * 
	 * Tell runtime where to look for views
	 * 
	 */
	
	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver bean = new InternalResourceViewResolver();

		bean.setViewClass(JstlView.class);
		bean.setPrefix("/views/");
		bean.setSuffix(".jsp");

		return bean;
   }

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	/**
	 * 
	 * Add argument to spring controller
	 * This helps standardize how the arguments passed to controller
	 * 
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		CentralAppRequestResolver reqPayload = new CentralAppRequestResolver();
		argumentResolvers.add( reqPayload );
	}
	
	/**
	 * 
	 * Resource location mapping
	 * 
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
    }
		
}
