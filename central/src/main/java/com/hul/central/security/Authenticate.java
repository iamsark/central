package com.hul.central.security;

import com.hul.central.base.CENTRAL;
import com.hul.central.system.model.CentralAppRequest;
import com.hul.central.system.model.CentralAppUser;
import com.hul.central.utils.CentralAppUtils;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
@PropertySource(value={CENTRAL.SYSTEM_PROPERTIES})
public class Authenticate {

	@Autowired
	private AuthDao adao;
	
	@Autowired
	private CentralAppUtils utils;
	
	@Autowired
	private Environment env;

	public String doLogin(CentralAppRequest _irq, HttpServletRequest _request) throws Exception {
		
		Properties user;
		Boolean isValidRequest = true;
		String username = "", 
			   password = "";		
		/* Get the payload */
		JSONObject payload = _irq.getPayload();		
		/* Fetch the session */
		HttpSession session = _request.getSession();		
		/* Fetch the form parameters */
		username = payload.getString("username");
		password = payload.getString("password");
	
		if (this.env.getProperty("central.system.recaptcha.enable").equals("yes")) {
			/* Verify the captcha */
			isValidRequest = this.utils.verify(payload.getString("rcaptcha"));
		}
				
		if (isValidRequest) {
			if (!username.equals("") && !password.equals("")) {				
				user = this.adao.checkCredential(username, password);									
				if (user != null) {
					/* Successfully Authenticated
					 * Time to prepare the session and environment for this User */
					return this.prepareSystemSession( (int) user.get("ID"), (boolean) user.get("STATUS"), (String) user.get("EMAIL"), session );
				} else {
					/* Invalid credential */
					session.setAttribute(CENTRAL.AUTH_MSG_KEY, CENTRAL.AUTH_MSG_INVALID_CREDENTIAL);
				}
			} else {
				/* User name or Password missing */
				session.setAttribute(CENTRAL.AUTH_MSG_KEY, CENTRAL.AUTH_MSG_PARAM_MISSING);
			}
		} else {
			/* Invalid captcha */
			session.setAttribute(CENTRAL.AUTH_MSG_KEY, CENTRAL.AUTH_MSG_INVALID_CAPTCHA);
		}	
		return CENTRAL.LOGIN_CONTEXT_PATH;
	}
	
	public String doLogout(CentralAppRequest _irq, HttpServletRequest _request) {
		HttpSession session = _request.getSession();	
		/* Well clear the session */
		session.invalidate();
		return CENTRAL.SEND_TO_SIGNIN_VIEW;
	}
	
	private String prepareSystemSession(int _uid, boolean _status, String _uname, HttpSession _session) throws Exception {		
		
		CentralAppUser user = new CentralAppUser(_uid, _uname, _status);
		user.setSystem(true);
		user.setAdmin(false);
		user.setAppUser(false);
		
		_session.setAttribute(CENTRAL.SYSTEM_SESSION, user);		
		return CENTRAL.SYSTEM_CONTEXT_PATH;
	}
	
}
