package com.hul.central.messenger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 
 * @author  Sark
 * @version 1.0
 *
 */

public class CentralAppWebSocketConfig implements ServerEndpointConfig {

	private final String path;
	private final Class<?> epClass;	
	private CentralAppWebSocketConfigurator csc = null;
	
	public CentralAppWebSocketConfig(Class<?> _epClass, String _path) {
		this.epClass = _epClass;
		this.path = _path;
	}
	
	@Override
	public List<Class<? extends Decoder>> getDecoders() {
		return Collections.emptyList();
	}

	@Override
	public List<Class<? extends Encoder>> getEncoders() {
		return Collections.emptyList();
	}

	@Override
	public Map<String, Object> getUserProperties() {
		return Collections.emptyMap();
	}

	@Override
	public Configurator getConfigurator() {
		if (this.csc == null) {
			this.csc = new CentralAppWebSocketConfigurator();
		}
		return this.csc;
	}

	@Override
	public Class<?> getEndpointClass() {
		return epClass;
	}

	@Override
	public List<Extension> getExtensions() {
		return Collections.emptyList();
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public List<String> getSubprotocols() {
		return Collections.emptyList();
	}

}
