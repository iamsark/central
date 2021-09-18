package com.hul.central.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.hul.central.base.CENTRAL;
import com.hul.central.system.model.CentralAppSocketRequest;
import com.hul.central.system.model.CentralAppSocketResponse;
import com.hul.central.system.model.CentralAppUser;

/**
 * 
 * @author  Sark
 * @version 1.0
 *
 */

public class CentralAppWebSocket extends Endpoint {

	/* Holds the rs_key of the System RS */
	private String systemRSKey;

	/* Holds all the LIVE users web socket sessions */
	private Map<String, Map<String, Properties>> live;
	
	public CentralAppWebSocket() {
		this.systemRSKey = "";
		this.live = new HashMap<String, Map<String, Properties>>();
	}
	
	@Override
	public void onOpen(Session _wSsession, EndpointConfig _ec) {
		CentralAppUser user = null;
		_wSsession.addMessageHandler(new IkeaSocketMessageHandler(_wSsession));
		/* Get the HTTP session from Web Socket session */
		HttpSession httpSession = (HttpSession) _ec.getUserProperties().get(HttpSession.class.getName());
		if (httpSession != null) {
			user = this.getIkeaUser(httpSession); 
			
			if (user != null) {
				String rs_key = this.generateUniqueKey(123, "aserwrw");
				Properties entry = new Properties();
				entry.put("session", _wSsession);
				entry.put("email", user.getUsername());
				
				/* Set RS & User key on http session as well 
				 * which will be used later to remove the socket entry, when the corresponding users session got expired */
				httpSession.setAttribute("CENTRAL_WS_RS_KEY", rs_key);
				httpSession.setAttribute("CENTRAL_WS_USER_KEY", user.getUsername());
				
				if (user.isAppUser()) {
					entry.put("utype", "APP");
				} else if (user.isAdmin()) {
					entry.put("utype", "ADMIN");
				} else {
					entry.put("utype", "SYSTEM");
					this.systemRSKey = rs_key;
				}
				
				try {
					//if (this.io != null) {
						//entry.put("first_name", this.io.getUserOption(user.getId(), "first_name"));
						//entry.put("last_name", this.io.getUserOption(user.getId(), "last_name"));
					//}					
				} catch (Exception e) {/* Ignore it */}	
				
				if (!this.live.containsKey(rs_key)) {
					this.live.put(rs_key, new HashMap<String, Properties>());
				}
				
				/* Add it to the LIVE tracker list */
				this.live.get(rs_key).put(user.getUsername(), entry);
				
				/* Well time time to tell all live users about the new session */
				try {
					this.broadCastSessionList();
				} catch (Exception _e) {
					/* Ignore it */
				}
			} 
		}
	}
	
	@Override
	public void onClose(Session _session, CloseReason closeReason) {
		super.onClose(_session, closeReason);
	}
	
	@Override
	public void onError(Session _session, Throwable _thr) {
		System.out.println("websocket connection exception: " + _session.getId() + " threw " + _thr.getClass().getSimpleName() +", "+ _thr.getMessage());
	}
	
	public void removeUserSocketEntry(String _rs_key, String _user_key) {
		if (this.live.containsKey(_rs_key)) {
			if (this.live.get(_rs_key).containsKey(_user_key)) {
				this.live.get(_rs_key).remove(_user_key);
				try {
					this.broadCastSessionList();
				} catch (Exception e) {
					/* Ignore it */
				}
			}		
		}
	}
	
	/**
	 * 
	 * List all users except @_userkey from all RS(s)
	 * 
	 * @param _userkey
	 * @return
	 * 
	 */
	private List<Properties> listAllSessions(String _userkey) {
		Properties rsProp = null;
		Properties userProp = null;
		List<Properties> users = null;
		List<Properties> sessionList = new ArrayList<Properties>();
		for (Map.Entry<String, Map<String, Properties>> rEntry : this.live.entrySet()) {
			rsProp = new Properties();
			users = new ArrayList<Properties>();						
			rsProp.put("key", rEntry.getKey());	
			for (Map.Entry<String, Properties> uEntry : rEntry.getValue().entrySet()) {
				if (!uEntry.getKey().equals(_userkey)) {
					userProp = new Properties();
					userProp.put("email", uEntry.getKey());
					userProp.put("utype", uEntry.getValue().get("utype"));
					users.add(userProp);
				}
				rsProp.put("rsname", uEntry.getValue().get("rsname"));
			}
			rsProp.put("users", users);
			sessionList.add(rsProp);	
		}
		return sessionList;
	}
	
	/**
	 * 
	 * List all users except @_userkey from the given RS @_rskey<br>
	 * It also includes user from "System" RS since System and Support users are also has to be included. 
	 * 
	 * @param _rskey
	 * @param _userkey
	 * @return
	 * 
	 */
	private List<Properties> listRSSessions(String _rskey, String _userkey) {
		Properties rsProp = null;
		Properties userProp = null;
		List<Properties> users = null;
		List<Properties> sessionList = new ArrayList<Properties>();
		for (Map.Entry<String, Map<String, Properties>> rEntry : this.live.entrySet()) {			
			/* List sessions only if the RS is matched with the argument or "System" */
			if (rEntry.getKey().equals(_rskey) || rEntry.getKey().equals(this.systemRSKey)) {
				rsProp = new Properties();
				users = new ArrayList<Properties>();						
				rsProp.put("key", rEntry.getKey());
				for (Map.Entry<String, Properties> uEntry : rEntry.getValue().entrySet()) {
					if (!uEntry.getKey().equals(_userkey)) {
						userProp = new Properties();
						userProp.put("email", uEntry.getKey());
						userProp.put("utype", uEntry.getValue().get("utype"));
						users.add(userProp);						
					}
					rsProp.put("rsname", uEntry.getValue().get("rsname"));
				}
				rsProp.put("users", users);
				sessionList.add(rsProp);
			}
		}
		return sessionList;
	}
	
	/**
	 * 
	 * Broadcast the current live users list to all the users 
	 * 
	 * @throws Exception
	 * 
	 */
	private void broadCastSessionList() throws Exception {
		String res = null;
		Properties prop = null;
		for (Map.Entry<String, Map<String, Properties>> rEntry : this.live.entrySet()) {			
			for (Map.Entry<String, Properties> uEntry : rEntry.getValue().entrySet()) {
				prop = new Properties();
				if (uEntry.getValue().get("utype").equals("SYSTEM") || uEntry.getValue().get("utype").equals("SUPPORT")) {
					prop.put("rs_key", rEntry.getKey());
					prop.put("user_key", uEntry.getValue().get("email"));
					prop.put("sessions", this.listAllSessions((String)uEntry.getValue().get("email")));				
					res = new CentralAppSocketResponse().prepareResponse(true, "messenger", "SESSION_LIST", prop).toString();
					((Session) uEntry.getValue().get("session")).getAsyncRemote().sendText(res);
				} else {
					prop.put("rs_key", rEntry.getKey());
					prop.put("user_key", uEntry.getValue().get("email"));
					prop.put("sessions", this.listRSSessions(rEntry.getKey(), (String)uEntry.getValue().get("email")));				
					res = new CentralAppSocketResponse().prepareResponse(true, "messenger", "SESSION_LIST", prop).toString();
					((Session) uEntry.getValue().get("session")).getAsyncRemote().sendText(res);
				}				
			}
		}
	}
	
	/**
	 * Socket Message handler for each session<br>
	 * This instance will be used to receive all the incoming messages for the given session.
	 * 
	 * @author Sark
	 *
	 */
	class IkeaSocketMessageHandler implements MessageHandler.Whole<String> {
		
		/* Web socket session */
		final Session session;
		
		/**
		 * 
		 * @param session
		 */
		public IkeaSocketMessageHandler(Session session) {
			this.session = session;		
		}
		
		/**
		 * Call back handler for incoming messages
		 */
		@Override
		public void onMessage(String _header) {	
			try {	
				String res = null;
				/* Have a dedicated request and response model for each message */
				CentralAppSocketRequest irq = new CentralAppSocketRequest();				 
				/* Parse the header */
				irq.parse(_header);
				if (irq.getEntity().equals("messenger")) {
					/* Alert Module */
					if (irq.getTask().equals("SESSION_LIST")) {
						res = new CentralAppSocketResponse().prepareResponse(true, irq.getEntity(), irq.getTask(), CentralAppWebSocket.this.listAllSessions(irq.getRskey())).toString();
						this.session.getAsyncRemote().sendText(res);
					} else if (irq.getTask().equals("CLOSE_SESSION")) {
						if (CentralAppWebSocket.this.live.containsKey(irq.getRskey())) {
							CentralAppWebSocket.this.live.remove(irq.getRskey());
							/* Broadcast the session list to all users */
							CentralAppWebSocket.this.broadCastSessionList();
						}
					} else {
						this.session.getAsyncRemote().sendText("got your message (" + _header + "). Thanks!");			
					} 
				} else if (irq.getEntity().equals("push")) {
					if (irq.getTask().equals("SEND")) {
						Map<String, Properties> rs = CentralAppWebSocket.this.live.get(irq.getPayload().get("RSKEY"));
						if (rs != null) {
							Session session = (Session) rs.get(irq.getPayload().get("USER")).get("session");
							if (session != null) {
								Properties prop = new Properties();
								prop.put("from", irq.getUserkey());
								prop.put("message", irq.getPayload().getString("MSG"));
								res = new CentralAppSocketResponse().prepareResponse(true, "push", "NOTIFICATION", prop).toString();
								session.getAsyncRemote().sendText(res);
							}
						}						
					}
				} else if (irq.getEntity().equals("chat")) {
					/* Chat Module */
					if (irq.getTask().equals("SEND")) {
						Properties prop = new Properties();
						Map<String, Properties> rs = CentralAppWebSocket.this.live.get(irq.getPayload().get("RSKEY"));
						if (rs != null) {
							Session session = (Session) rs.get(irq.getPayload().get("USER")).get("session");
							if (session != null) {
								prop.put("from_rs", irq.getRskey());
								prop.put("from_user", irq.getUserkey());
								prop.put("message", irq.getPayload().getString("MSG"));
								res = new CentralAppSocketResponse().prepareResponse(true, "chat", "MESSAGE", prop).toString();
								session.getAsyncRemote().sendText(res);
							}
						}
					}
				}				
			} catch (Exception e) {
				/* Ignore it */
			}		
		}		
	}
	
	private CentralAppUser getIkeaUser(HttpSession _session) {
		if (_session.getAttribute(CENTRAL.APP_SESSION) != null) {
			/* Has App user session */
			return (CentralAppUser) _session.getAttribute(CENTRAL.APP_SESSION);
		} else if (_session.getAttribute(CENTRAL.ADMIN_SESSION) != null) {
			/* Has Admin user session */
			return (CentralAppUser) _session.getAttribute(CENTRAL.ADMIN_SESSION);
		} else if (_session.getAttribute(CENTRAL.SYSTEM_SESSION) != null) {
			/* Has System user session */
			return (CentralAppUser) _session.getAttribute(CENTRAL.SYSTEM_SESSION);
		}
		return null;
	}
	
	private String generateUniqueKey(Integer _rsid, String _rsname) {
		return (_rsname.replaceAll("[^a-zA-Z]+","") +"_"+ _rsid);
	}
	
}
