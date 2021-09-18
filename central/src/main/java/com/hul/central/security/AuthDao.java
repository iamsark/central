package com.hul.central.security;

import com.hul.central.base.CENTRAL;
import com.hul.central.db.CentralAppDb;
import com.hul.central.exception.CentralAppException;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Service
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
@PropertySource(value={CENTRAL.SYSTEM_PROPERTIES})
public class AuthDao {

	@Autowired
	CentralAppDb idb;
	
	@Autowired
	private Environment env;
	
	public Properties checkCredential(String _email, String _password) throws Exception {	
		String query = "", 
			   dbInstance = "mssql";
		if (this.env.getProperty("central.system.db.instance") != null && !this.env.getProperty("central.system.db.instance").trim().equals("")) {
			dbInstance = env.getProperty("central.system.db.instance");
		} else {
			throw new CentralAppException("System.properties : DB instance type property not found");
		}
		
		if (dbInstance.equals("mssql")) {
			query = "SELECT ID, RS_ID, EMAIL, STATUS FROM CENTRAL_USERS WHERE EMAIL=? AND PASSWORD=?;";
		} else {
			query = "SELECT ID, RS_ID, EMAIL, STATUS FROM CENTRAL_USERS WHERE EMAIL=? AND PASSWORD=MD5(?);";
		}
		
		return idb.get(query, new Object[]{_email, _password});
	}
	
	public Properties getRS(int _rs_id) throws Exception {
		String query = "SELECT ID, COUNTRY, NAME, RS_CODE, STATUS, MAINTENANCE FROM IKEA_RS WHERE ID=?;";
		return idb.get(query, new Object[]{_rs_id});
	}
	
	public List<Properties> getDSPointer(int _rs_id) throws Exception {
		String query = "SELECT ID, NAME, DRIVER, CONN_STR, DB_USER, DB_PASS, CONN_OPT, STATUS FROM IKEA_DATASOURCE WHERE RS_ID=?;";
		return idb.list(query, new Object[]{_rs_id});
	}
	
	public List<Properties> getRoles(int _uid) throws Exception {		
		String query = "SELECT IKEA_USER_ROLE_MAP.ROLE_ID, IKEA_ROLE.NAME, IKEA_ROLE.STATUS, IKEA_ROLE.IS_PRIVATE, IKEA_ROLE.CREATED_FOR FROM IKEA_USER_ROLE_MAP INNER JOIN IKEA_ROLE ON IKEA_USER_ROLE_MAP.ROLE_ID = IKEA_ROLE.ID WHERE IKEA_USER_ROLE_MAP.USER_ID=?;";		
		//String query = "SELECT ID, NAME, STATUS FROM IKEA_ROLE  IKEA_USER_ROLE_MAP WHERE USER_ID=?;";
		return idb.list(query, new Object[]{_uid});
	}
	
	public List<Properties> getMenuList() throws Exception {
		String query = "SELECT ID, TITLE, HANDLE, PARENT, ICON, MENU_TYPE, STATUS, RESOURCES FROM IKEA_MENU;";
		return idb.list(query);
	}
	
	public List<Properties> getMenuMapping(int _rid) throws Exception {
		String query = "SELECT ID, MENU_ID, STATUS, CAN_READ, CAN_WRITE, CAN_UPDATE, CAN_DELETE FROM IKEA_MENU_ROLE_MAP WHERE ROLE_ID=?;";
		return idb.list(query, new Object[]{_rid});
	}
	
}
