package com.hul.central.system.model;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author  Sark
 * @version 1.0
 *
 */

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class CentralAppRequest {

	private String action = "";
	private String entity = null;
	private String task = null;
	private int page = -1;
	private String data_type = "";
	private String content_type = "";
	private JSONObject payload = null;
	
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
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
	 * @return the page
	 */
	public int getPage() {
		return page;
	}
	
	/**
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}
	
	/**
	 * @return the data_type
	 */
	public String getData_type() {
		return data_type;
	}
	
	/**
	 * @param data_type the data_type to set
	 */
	public void setData_type(String data_type) {
		this.data_type = data_type;
	}
	
	/**
	 * @return the content_type
	 */
	public String getContent_type() {
		return content_type;
	}
	
	/**
	 * @param content_type the content_type to set
	 */
	public void setContent_type(String content_type) {
		this.content_type = content_type;
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
