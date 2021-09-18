package com.hul.central.messenger;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

/**
 * 
 * @author  Sark
 * @version 1.0
 *
 */

public class CentralAppWebSocketConfigurator extends  Configurator {

public static CentralAppWebSocket cws = null;
	
	public CentralAppWebSocketConfigurator() {
		CentralAppWebSocketConfigurator.cws = new CentralAppWebSocket();
	}
	
	@Override
    public void modifyHandshake(ServerEndpointConfig _config, HandshakeRequest _request, HandshakeResponse _response) {
        HttpSession httpSession = (HttpSession) _request.getHttpSession();
        _config.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getEndpointInstance(Class<T> _epClass) throws InstantiationException {
		return (T) CentralAppWebSocketConfigurator.cws;		
	}
	
}
