package com.hul.central.system.model;

/**
 * 
 * @author  Sark
 * @version 1.0.0
 *
 */

public class CentralAppRole {

	private int id;
	private String name;
	private boolean status;
	private boolean is_private;
	private int created_for;
	
	/**
	 * 
	 */
	public CentralAppRole() {
		super();
	}
	
	/**
	 * @param _id
	 * @param _name
	 * @param _status
	 */
	public CentralAppRole(int _id, String _name, boolean _status, boolean _is_private, int _created_for) {
		super();
		this.id = _id;
		this.name = _name;
		this.status = _status;
		this.is_private = _is_private;
		this.created_for = _created_for;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param _id the id to set
	 */
	public void setId(int _id) {
		this.id = _id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param _name the name to set
	 */
	public void setName(String _name) {
		this.name = _name;
	}

	/**
	 * @return the status
	 */
	public boolean getStatus() {
		return this.status;
	}

	/**
	 * @param _status the status to set
	 */
	public void setStatus(boolean _status) {
		this.status = _status;
	}

	/**
	 * @return the is_private
	 */
	public boolean isIs_private() {
		return is_private;
	}

	/**
	 * @param is_private the is_private to set
	 */
	public void setIs_private(boolean is_private) {
		this.is_private = is_private;
	}

	/**
	 * @return the created_for
	 */
	public int getCreated_for() {
		return created_for;
	}

	/**
	 * @param created_for the created_for to set
	 */
	public void setCreated_for(int created_for) {
		this.created_for = created_for;
	}
	
}
