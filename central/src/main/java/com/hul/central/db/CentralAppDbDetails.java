package com.hul.central.db;

/**
 * 
 * @author  Sark
 * @version 1.0.0
 *
 */

public class CentralAppDbDetails {

	private int id;
	private String name="";
	private String driver="";
	private String conn_str="";
	private String user="";
	private String pass="";
	private String options="";
		
	/**
	 * 
	 */
	public CentralAppDbDetails() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param driver
	 * @param conn_str
	 * @param user
	 * @param pass
	 * @param options
	 */
	public CentralAppDbDetails(int id, String name, String driver, String conn_str, String user, String pass,
			String options) {
		super();
		this.id = id;
		this.name = name;
		this.driver = driver;
		this.conn_str = conn_str;
		this.user = user;
		this.pass = pass;
		this.options = options;
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
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the conn_str
	 */
	public String getConn_str() {
		return conn_str;
	}

	/**
	 * @param conn_str the conn_str to set
	 */
	public void setConn_str(String conn_str) {
		this.conn_str = conn_str;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the options
	 */
	public String getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(String options) {
		this.options = options;
	}
	
	
}
