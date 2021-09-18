package com.hul.central.base;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.hul.central.exception.CentralAppException;
import com.hul.central.system.model.CentralAppRequest;


/**
 * 
 * As we have generalized all the Ajax request with standard parameters.<br>
 * Here we are intercepting all the request before dispatched to the appropriate controller.<br>
 * We will instantiate the IkeaRequest object using the incoming Request Payload<br>
 * and this IkeaRequest object will be injected in all the Controller object.<br>
 * So that all module developers can access the incoming request parameters in a standard format.<br><br>
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

public class CentralAppRequestResolver implements HandlerMethodArgumentResolver {

	/**
	 * Add support for CentralAppRequest type
	 */
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(CentralAppRequest.class);
	}

	/**
	 * We are overriding this method to inject additional parameters into each controller handler
	 * In this case we will be preparing 'CentralAppRequest' object and injecting 
	 */
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		
		CentralAppRequest crq = new CentralAppRequest();		
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		
		if (request != null) {
			if (!request.getRequestURI().equals(request.getContextPath() +"/user/logout")) {
				/* Try "getParameter" method */				
				String lecaRequestBody =request.getParameter("leca_request_body");
				
				if ( lecaRequestBody == null ) {			
					/* Looks like "getParameter" method failed
					 * So try "getParameterValues" method */				
					String params[] = request.getParameterValues("leca_request_body");			
					if ( params != null && params.length > 0) {
						/* Found it */
						lecaRequestBody = params[0];
					} else {				
						/* Looks like both methods were failed
						 * Well dig the Request Header by ourself and fetch the argument directly */
					    String body = IOUtils.toString(request.getReader());
					    
					    /* decode with utf8 format */
					    body = java.net.URLDecoder.decode(body, "UTF-8");				   
					    if ( body.contains( "leca_request_body" ) ) {
					    	lecaRequestBody = body.substring( body.indexOf("{"), ( body.lastIndexOf("}") + 1 ) );
					    }				    
					}
				}		
				if (lecaRequestBody != null) {
					JSONObject json = new JSONObject(lecaRequestBody);
					crq.setAction(json.getString("action"));
					crq.setEntity(json.getString("entity"));			
					crq.setTask(json.getString("task"));
					crq.setPage(json.getInt("page"));
					crq.setPayload(json.getJSONObject("payload"));
					crq.setData_type(json.getString("data_type"));
					crq.setContent_type(json.getString("content_type"));
				} else {
					throw new CentralAppException( "Parameter is missing in the Request Header" );
				}
			}			
		} else {
			throw new CentralAppException( "Not able to resolve the Arguments, as the Request itself it not available" );
		}
		
		return crq;
		
	}

}
