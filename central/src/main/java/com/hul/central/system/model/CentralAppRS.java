package com.hul.central.system.model;

import com.hul.central.db.CentralAppDbDetails;

/**
 * 
 * @author  Sark
 * @version 1.0.0
 *
 */

public class CentralAppRS {

	/* RSP ID */
	private int id;
	/* Branch ID of which the RSP is belongs to */
	private int cluster;
	/* RSP Name - Used in various situation */
	private String name;
	/* RSP Code - legacy property ( Used in RSUnify ) */
	private String rsp_code;
	/* Status of the RSP - 'true' for Enabled, 'false' for Disabled */
	private boolean status;
	/* Maintenance Flag - 'true' if it is in Maintenance mode, 'false' otherwise */
	private boolean maintenance;
	/* Data Source pointer for this RS */
	private CentralAppDbDetails ds;
	
	/**
	 * 
	 */
	public CentralAppRS() {
		super();
	}

	/**
	 * @param id
	 * @param cluster
	 * @param name
	 * @param rsp_code
	 * @param status
	 * @param maintenance
	 * @param ds
	 */
	public CentralAppRS(int id, int cluster, String name, String rsp_code, boolean status, boolean maintenance,
			CentralAppDbDetails ds) {
		super();
		this.id = id;
		this.cluster = cluster;
		this.name = name;
		this.rsp_code = rsp_code;
		this.status = status;
		this.maintenance = maintenance;
		this.ds = ds;
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
	 * @return the cluster
	 */
	public int getCluster() {
		return cluster;
	}

	/**
	 * @param cluster the cluster to set
	 */
	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the rsp_code
	 */
	public String getRsp_code() {
		return rsp_code;
	}

	/**
	 * @param rsp_code the rsp_code to set
	 */
	public void setRsp_code(String rsp_code) {
		this.rsp_code = rsp_code;
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
	 * @return the maintenance
	 */
	public boolean isInMaintenanceMode() {
		return maintenance;
	}

	/**
	 * @param maintenance the maintenance to set
	 */
	public void setMaintenanceMode(boolean maintenance) {
		this.maintenance = maintenance;
	}

	/**
	 * @return the ds
	 */
	public CentralAppDbDetails getDS() {
		return ds;
	}

	/**
	 * @param ds the ds to set
	 */
	public void setDS(CentralAppDbDetails ds) {
		this.ds = ds;
	}
		
	
}
