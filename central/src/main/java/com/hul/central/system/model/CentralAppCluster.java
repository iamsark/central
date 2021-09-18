package com.hul.central.system.model;

public class CentralAppCluster {

	private String clusterID;
	private String publicURL;
	private String appHostName;
	private String appServerIp;
	private String dbHostName;
	private String dbServerIp;
	
	/**
	 * 
	 */
	public CentralAppCluster() {
		super();
	}

	/**
	 * @param clusterID
	 * @param publicURL
	 * @param appHostName
	 * @param appServerIp
	 * @param dbHostName
	 * @param dbServerIp
	 */
	public CentralAppCluster(String clusterID, String publicURL, String appHostName, String appServerIp,
			String dbHostName, String dbServerIp) {
		super();
		this.clusterID = clusterID;
		this.publicURL = publicURL;
		this.appHostName = appHostName;
		this.appServerIp = appServerIp;
		this.dbHostName = dbHostName;
		this.dbServerIp = dbServerIp;
	}

	/**
	 * @return the clusterID
	 */
	public String getClusterID() {
		return clusterID;
	}

	/**
	 * @param clusterID the clusterID to set
	 */
	public void setClusterID(String clusterID) {
		this.clusterID = clusterID;
	}

	/**
	 * @return the publicURL
	 */
	public String getPublicURL() {
		return publicURL;
	}

	/**
	 * @param publicURL the publicURL to set
	 */
	public void setPublicURL(String publicURL) {
		this.publicURL = publicURL;
	}

	/**
	 * @return the appHostName
	 */
	public String getAppHostName() {
		return appHostName;
	}

	/**
	 * @param appHostName the appHostName to set
	 */
	public void setAppHostName(String appHostName) {
		this.appHostName = appHostName;
	}

	/**
	 * @return the appServerIp
	 */
	public String getAppServerIp() {
		return appServerIp;
	}

	/**
	 * @param appServerIp the appServerIp to set
	 */
	public void setAppServerIp(String appServerIp) {
		this.appServerIp = appServerIp;
	}

	/**
	 * @return the dbHostName
	 */
	public String getDbHostName() {
		return dbHostName;
	}

	/**
	 * @param dbHostName the dbHostName to set
	 */
	public void setDbHostName(String dbHostName) {
		this.dbHostName = dbHostName;
	}

	/**
	 * @return the dbServerIp
	 */
	public String getDbServerIp() {
		return dbServerIp;
	}

	/**
	 * @param dbServerIp the dbServerIp to set
	 */
	public void setDbServerIp(String dbServerIp) {
		this.dbServerIp = dbServerIp;
	}
		
	
}
