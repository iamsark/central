package com.hul.central.system.model;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.codehaus.jettison.json.JSONObject;

import com.zaxxer.hikari.HikariDataSource;

public class CentralAppUser {

	private int id;
	private String username;
	private List<CentralAppRole> roles;
	private JSONObject userPreference;
	private Properties transientOption;
	private boolean isTransactional;
	private Connection conn;
	private boolean isReuseCon;
	private List<String> liveModules;
	
	private boolean status;
	private boolean isAdmin;
	private boolean isSystem;	
	private boolean isAppUser;	
	private boolean isPreAccess;
	
	/**
	 * 
	 */
	public CentralAppUser() {
		super();
	}
	
	/**
	 * 
	 * @param _id
	 * @param _username
	 * @param _status
	 */
	public CentralAppUser(int _id, String _username, boolean _status) {
		super();
		this.id = _id;
		this.username = _username;
		this.status = _status;
		this.isTransactional = false;
		this.transientOption = new Properties();		
	}

	/**
	 * @param id
	 * @param username
	 * @param rs
	 * @param roles
	 * @param userPreference
	 * @param isTransactional
	 * @param conn
	 * @param isReuseCon
	 * @param liveModules
	 * @param ds
	 * @param status
	 * @param isAdmin
	 * @param isSystem
	 * @param isAppUser
	 * @param isPreAccess
	 */
	public CentralAppUser(int id, String username, List<CentralAppRole> roles, JSONObject userPreference,
			Properties transientOption, boolean isTransactional, Connection conn, boolean isReuseCon, List<String> liveModules, 
			boolean status, boolean isAdmin, boolean isSystem, boolean isAppUser, boolean isPreAccess) {
		super();
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.userPreference = userPreference;
		this.transientOption = transientOption;
		this.isTransactional = isTransactional;
		this.conn = conn;
		this.isReuseCon = isReuseCon;
		this.liveModules = liveModules;		
		this.status = status;
		this.isAdmin = isAdmin;
		this.isSystem = isSystem;
		this.isAppUser = isAppUser;
		this.isPreAccess = isPreAccess;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the roles
	 */
	public List<CentralAppRole> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<CentralAppRole> roles) {
		this.roles = roles;
	}

	/**
	 * @return the userPreference
	 */
	public JSONObject getUserPreference() {
		return userPreference;
	}

	/**
	 * @param userPreference the userPreference to set
	 */
	public void setUserPreference(JSONObject userPreference) {
		this.userPreference = userPreference;
	}

	/**
	 * @return the transientOption
	 */
	public Properties getTransientOption() {
		return transientOption;
	}

	/**
	 * @param transientOpttion the transientOption to set
	 */
	public void setTransientOption(Properties transientOption) {
		this.transientOption = transientOption;
	}
	
	/**
	 * @return the isTransactional
	 */
	public boolean isTransactional() {
		return isTransactional;
	}

	/**
	 * @param isTransactional the isTransactional to set
	 */
	public void setTransactional(boolean isTransactional) {
		this.isTransactional = isTransactional;
	}

	/**
	 * @return the conn
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * @param conn the conn to set
	 */
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	/**
	 * @return the isReuseCon
	 */
	public boolean isReuseConnection() {
		return isReuseCon;
	}

	/**
	 * @param isReuseCon the isReuseCon to set
	 */
	public void setReuseConnection(boolean isReuseCon) {
		this.isReuseCon = isReuseCon;
	}

	/**
	 * @return the liveModules
	 */
	public List<String> getLiveModules() {
		return liveModules;
	}

	/**
	 * @param liveModules the liveModules to set
	 */
	public void setLiveModules(List<String> liveModules) {
		this.liveModules = liveModules;
	}	
	
	public void beginTransaction() throws Exception {
		this.setTransactional(true);
	}
	
	public void endTransaction() throws Exception {
		this.setTransactional(false);
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
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin the isAdmin to set
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @return the isSystem
	 */
	public boolean isSystem() {
		return isSystem;
	}

	/**
	 * @param isSystem the isSystem to set
	 */
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	/**
	 * @return the isAppUser
	 */
	public boolean isAppUser() {
		return isAppUser;
	}

	/**
	 * @param isAppUser the isAppUser to set
	 */
	public void setAppUser(boolean isAppUser) {
		this.isAppUser = isAppUser;
	}

	/**
	 * @return the isPreAccess
	 */
	public boolean isPreAccess() {
		return isPreAccess;
	}
	
}
