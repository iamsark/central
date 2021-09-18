package com.hul.central.system.model;

import org.springframework.stereotype.Component;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

/**
 * 
 * Generic response container for CentralApp Modules<br>
 * All the request will be served this instance as a response<br>
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Component
public class CentralAppResponse {

	/* Requested task ( resource ) status */
	private boolean status;
	/* Any success or error message */
	private String message;
	/* Data ( if any otherwise null ) that is about to sent to client */
	private Object payload;
		
	/**
	 * 
	 */
	public CentralAppResponse() {
		super();		
	}

	/**
	 * @param status
	 * @param message
	 * @param payload
	 */
	public CentralAppResponse(boolean _status, String _message, Object _payload) {
		super();
		this.status = _status;
		this.message = _message;
		this.payload = _payload;
	}

	/**
	 * 
	 * Sets the response properties and returns the IkeaResponse instance
	 * @param _status
	 * @param _message
	 * @param _payload
	 * @return IkeaResponse object
	 * 
	 */
	public CentralAppResponse prepareResponse(boolean _status, String _message, Object _payload) {
		this.status = _status;
		this.message = _message;
		this.payload = _payload;
		return this;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean _status) {
		this.status = _status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String _message) {
		this.message = _message;
	}

	/**
	 * @return the payload
	 */
	public Object getPayload() {
		return payload;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayload(Object _payload) {
		this.payload = _payload;
	}
	
	/**
	 * 
	 * Map this instance as JSON string 
	 * @return String
	 * 
	 */
	public String toString() {
		String eM = "";
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		ObjectWriter ow = om.writer().withDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			eM = e.getMessage();
		} catch (JsonMappingException e) {
			e.printStackTrace();
			eM = e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			eM = e.getMessage();
		}
		/* This means IkeaResponse JSON mapping failed
		 * So return the error response by manually constructing */
		return "{\"status\":false,\"message\":\""+ eM +"\",\"payload\":null}";
	}
	
}
