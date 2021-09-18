package com.hul.central.system.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class CentralAppSocketResponse {

	private boolean status;
	
	private String entity;
	
	private String task;
	
	private Object payload;	

	/**
	 * 
	 */
	public CentralAppSocketResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param entity
	 * @param task
	 * @param payload
	 */
	public CentralAppSocketResponse(boolean _status, String _entity, String _task, Object _payload) {
		super();
		this.status = _status;
		this.entity = _entity;
		this.task = _task;
		this.payload = _payload;
	}

	public CentralAppSocketResponse prepareResponse(boolean _status, String _entity, String _task, Object _payload) {
		this.status = _status;
		this.entity = _entity;
		this.task = _task;
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
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(String task) {
		this.task = task;
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
	public void setPayload(Object payload) {
		this.payload = payload;
	}

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
