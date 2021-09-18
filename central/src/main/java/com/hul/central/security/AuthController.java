package com.hul.central.security;

import com.hul.central.base.CENTRAL;
import com.hul.central.system.model.CentralAppRequest;
import com.hul.central.system.model.CentralAppResponse;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Controller
@RequestMapping(CENTRAL.USER_CONTEXT_PATH)
public class AuthController {

	@Autowired
	Authenticate as;
	
	@Autowired
	CentralAppResponse ir;
	
	/**
	 * This would bring the Login Page
	 * 
	 * @return ModelAndView
	 */
	@RequestMapping(value=CENTRAL.LOGIN_CONTEXT_PATH,  method=RequestMethod.GET)
	public ModelAndView view() {
		ModelAndView mav = new ModelAndView();	
		mav.setViewName("login");
		return mav;
	}
	
	/**
	 * This one does the Login Operation
	 *  
	 * @param _irq
	 * @param _request
	 * @param _model
	 * @return ModelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value=CENTRAL.SIGNIN_CONTEXT_PATH,  method=RequestMethod.POST)
	public String login(CentralAppRequest _irq, HttpServletRequest _request) throws Exception {
		return as.doLogin(_irq, _request);
	}
	
	@ResponseBody
	@RequestMapping(value=CENTRAL.LOGOUT_CONTEXT_PATH,  method=RequestMethod.GET)
	public String logout(CentralAppRequest _irq, HttpServletRequest _request) {
		return as.doLogout(_irq, _request);
	}
	
}
