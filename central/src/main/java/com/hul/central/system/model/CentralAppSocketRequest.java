package com.hul.central.system.model;

import org.codehaus.jettison.json.JSONObject;

public class CentralAppSocketRequest {

private String rskey;
	
	private String user_key;
	
	private String entity;
	
	private String task;
	
	private JSONObject payload;

	/**
	 * 
	 */
	public CentralAppSocketRequest() {
		super();
	}

	/**
	 * @param entity
	 * @param task
	 * @param payload
	 */
	public CentralAppSocketRequest(String _rskey, String _ukey, String entity, String task, JSONObject payload) {
		super();
		this.rskey = _rskey;
		this.user_key = _ukey;
		this.entity = entity;
		this.task = task;
		this.payload = payload;
	}
	
	public CentralAppSocketRequest parse(String _header) throws Exception {
		JSONObject json = new JSONObject(_header);	
		this.setRskey(json.getString("rkey"));
		this.setUserkey(json.getString("ukey"));
		this.setEntity(json.getString("entity"));			
		this.setTask(json.getString("task"));
		this.setPayload(json.getJSONObject("payload"));
		return this;
	}

	/**
	 * @return the rskey
	 */
	public String getRskey() {
		return rskey;
	}

	/**
	 * @param rskey the rskey to set
	 */
	public void setRskey(String rskey) {
		this.rskey = rskey;
	}
	
	/**
	 * @return the user_key
	 */
	public String getUserkey() {
		return user_key;
	}

	/**
	 * @param user_key the user_key to set
	 */
	public void setUserkey(String user_key) {
		this.user_key = user_key;
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
	public JSONObject getPayload() {
		return payload;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}
	
}
