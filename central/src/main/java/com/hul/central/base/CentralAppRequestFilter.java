package com.hul.central.base;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hul.central.system.model.CentralAppResponse;

/**
 * Request filter implementation for basic security and sanitization.<br>
 * We check for three types of Users here, <br>
 * <ul>
 * <li><i>Actual end user</i> - <b>LECA_USER_APP</b> attribute should be there</li>
 * <li><i>Territory level access</i> - <b>LECA_USER_TERRITORY</b> attribute should be there</li>
 * <li><i>Area level access</i> - <b>LECA_USER_AREA</b> attribute should be there</li>
 * <li><i>Zone level access</i> - <b>LECA_USER_ZONE</b> attribute should be there</li>
 * <li><i>Cluster level access</i> - <b>LECA_USER_CLUSTER</b> attribute should be there</li>
 * <li><i>Branch level access</i> - <b>LECA_USER_BRANCH</b> attribute should be there</li>
 * <li><i>Country level access</i> - <b>LECA_USER_COUNTRY</b> attribute should be there</li>
 * <li><i>System user</i> - <b>LECA_USER_SYSTEM</b> attribute should be there</li>
 * </ul>
 * If any one of the above exists then we let the request go further<br>
 * otherwise we redirect them to login page.<br><br>
 * 
 * @author Sark
 * @since  1.0.0
 *
 */

@WebFilter(urlPatterns = { "/*" })
public class CentralAppRequestFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest _request, ServletResponse _response, FilterChain _chain) throws IOException, ServletException {
		
		boolean allow = true;
		HttpServletRequest req = (HttpServletRequest) _request;
		HttpServletResponse res = (HttpServletResponse) _response;
		HttpSession session = req.getSession();
	
		String path = req.getRequestURI();
		String contextPath = req.getContextPath();
		boolean isAjax = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
		
		/**
		 * 
		 * Exclude /central, /central/, /central/assets/.* 
		 * 
		 **/
		if (!path.equals(contextPath)
				&& !path.equals(contextPath +"/")
				&& !path.equals(contextPath + "/user/signin")
				&& !path.startsWith(contextPath + CENTRAL.ASSET_CONTEXT_PATH)
				&& !path.startsWith(contextPath + CENTRAL.SYSTEM_CONTEXT_PATH)) {
			
			if (session.getAttribute(CENTRAL.APP_SESSION) == null 
				&& session.getAttribute(CENTRAL.SYSTEM_SESSION) == null) {
				
				/* But no valid session so sent them to Login Page */
				if (session != null || req.isRequestedSessionIdValid()) {					
					/* Clear the session, just in case */
					session.invalidate();
				}
				
				if (isAjax) {
					res.reset();
					res.setContentType("application/json");
					res.setCharacterEncoding("UTF-8");	
					/* Notify the client that login is required */
					res.getWriter().write(new CentralAppResponse().prepareResponse(false, CENTRAL.SIGNIN_REQUIRED, null).toString());	
				} else {					
					res.sendRedirect(contextPath);
				}				
				return;				
			}		
		}
		
		if (!allow) {
			/* Well this is a sign of Access Violation */
			if (session != null || req.isRequestedSessionIdValid()) {
				/* Clear the session, just in case */
				session.invalidate();
			}
			if (isAjax) {
				res.reset();
				res.setContentType("application/json");
				res.setCharacterEncoding("UTF-8");				
				/* Notify the client that access violation exception happened */
				res.getWriter().write(new CentralAppResponse().prepareResponse(false, CENTRAL.ACCESS_VIOLATION, null).toString());
			} else {
				res.sendRedirect(contextPath);
			}
			return;
		}		
		
		/* Looks like a genuine Request - Proceed to Further */
		if (allow) {
			_chain.doFilter(_request, _response);
		}
		
	}
	
	
}
