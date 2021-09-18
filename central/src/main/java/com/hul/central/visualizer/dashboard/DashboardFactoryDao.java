package com.hul.central.visualizer.dashboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hul.central.db.CentralAppDb;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Service("DashboardFactoryDao")
public class DashboardFactoryDao {
	
	@Autowired
	CentralAppDb cdb;

	public List<ArrayList<Object>> listDashboard() throws Exception {
		return this.cdb.dietList("SELECT ID, TITLE, HANDLE, OPTIONS, STATUS FROM CENTRAL_DASHBOARD;");
	}

	public Properties getDashboard(String _handle) throws Exception {
		return this.cdb.get("SELECT ID, TITLE, HANDLE, OPTIONS FROM CENTRAL_DASHBOARD WHERE HANDLE = ? AND STATUS = 1",new Object[]{_handle});
	}
	
	public Properties getDashboardUserOption(int _dashboardID, int _userID) throws Exception {
		return this.cdb.get("SELECT OPTIONS FROM CENTRAL_DASHBOARD_OPTIONS WHERE DID=? AND UID=?;", new Object[] {_dashboardID, _userID});
	}
	
	public List<Properties> listUser() throws Exception {
		return this.cdb.list("SELECT ID, EMAIL FROM CENTRAL_USERS;");
	}
	
	public List<ArrayList<Object>> listWidgetGroup() throws Exception {
		return this.cdb.dietList("SELECT ID, TITLE, HANDLE, DASHBOARD, OPTIONS, STATUS FROM CENTRAL_WIDGET_GROUP;");
	}

	public List<Properties> listWidgetGroup(int _dashboard) throws Exception {
		return this.cdb.list("SELECT ID, TITLE, HANDLE, OPTIONS, STATUS FROM CENTRAL_WIDGET_GROUP WHERE DASHBOARD="+ _dashboard +";");
	}

	public List<ArrayList<Object>> listWidget() throws Exception {
		return this.cdb.dietList("SELECT ID, TITLE, HANDLE, OPTIONS, IS_DEMO, TARGET, TYPE, STATUS FROM CENTRAL_WIDGET;");
	}

	public List<Properties> listWidgetFat() throws Exception {
		return this.cdb.list("SELECT ID, TITLE, HANDLE, OPTIONS, IS_DEMO, TARGET, TYPE, STATUS FROM CENTRAL_WIDGET WHERE STATUS = 1;");
	}

	public List<Properties> listWidget(int _grp) throws Exception {
		return this.cdb.list("SELECT ID, TITLE, HANDLE, OPTIONS, IS_DEMO, TARGET, TYPE, STATUS FROM CENTRAL_WIDGET WHERE WIDGET_GRP="+ _grp +";");
	}
	
	public Properties getWidget(int _id) throws Exception {
		return this.cdb.get("SELECT ID, TITLE, HANDLE, OPTIONS, IS_DEMO, TARGET, TYPE FROM CENTRAL_WIDGET WHERE ID = ? and STATUS = 1",new Object[]{_id});
	}
	
	public int addDashboard(String _title, String _handle, String _options) throws Exception {
		return this.cdb.insert("INSERT INTO CENTRAL_DASHBOARD(TITLE, HANDLE, OPTIONS, STATUS) VALUES(?,?,?,?);", new Object[]{_title, _handle, _options, true});
	}
	
	public int addDashboardOptions(int _uid, int _did, String _options) throws Exception {
		return this.cdb.insert("INSERT INTO CENTRAL_DASHBOARD_OPTIONS(UID, DID, OPTIONS) VALUES(?,?,?);", new Object[]{_uid, _did, _options});
	}

	public int addWidgetGroup(String _title, String _handle, int _dashboard, String _options) throws Exception {
		return this.cdb.insert("INSERT INTO CENTRAL_WIDGET_GROUP(TITLE, HANDLE, DASHBOARD, OPTIONS) VALUES(?,?,?,?);", new Object[]{_title, _handle, _dashboard, _options});
	}

	public int addWidget(String _title, String _handle, String _options, boolean _isdemo, String _target, String _type) throws Exception {
		return this.cdb.insert("INSERT INTO CENTRAL_WIDGET(TITLE, HANDLE, OPTIONS, IS_DEMO, TARGET, TYPE, STATUS) VALUES(?,?,?,?,?,?,?);", new Object[]{_title, _handle, _options, _isdemo, _target, _type, true});
	}
	
	public int updateDashboard(String _title, String _handle, String _options, int _id) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_DASHBOARD SET TITLE=?, HANDLE=?, OPTIONS=? WHERE ID=?;", new Object[]{_title, _handle, _options, _id});
	}

	public int updateDashboardOption(String _handle, String _options) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_DASHBOARD SET OPTIONS=? WHERE HANDLE=?;", new Object[]{_options, _handle});
	}
	
	public int updateDashboardOptions(int _did, String _options) throws Exception {
		return this.cdb.insert("UPDATE CENTRAL_DASHBOARD_OPTIONS SET OPTIONS=? WHERE DID=?;", new Object[]{_options, _did});
	}
	
	public int updateDashboardUserOptions(int _uid, int _did, String _options) throws Exception {
		return this.cdb.insert("UPDATE CENTRAL_DASHBOARD_OPTIONS SET OPTIONS=? WHERE UID=? AND DID=?;", new Object[]{_options, _uid, _did});
	}

	public int toggleDashboardStatus(int _id, boolean _status) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_DASHBOARD STATUS=? WHERE ID=?;", new Object[]{_status, _id});
	}

	public int updateWidgetGroup(String _title, String _handle, int _dashboard, String _options, boolean _status, int _id) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_WIDGET_GROUP SET TITLE=?, HANDLE=?, DASHBOARD=?, OPTIONS=?, STATUS=? WHERE ID=?;", new Object[]{_title, _handle, _dashboard, _options, _status, _id});
	}

	public int updateWidget(String _title, String _handle, String _options, boolean _isdemo, String _target, String _type, int _id) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_WIDGET SET TITLE=?, HANDLE=?, OPTIONS=?, IS_DEMO=?, TARGET=?, TYPE=? WHERE ID=?;", new Object[]{_title, _handle, _options, _isdemo, _target, _type, _id});
	}

	public int updateWidgetOption(String _options, int _id) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_WIDGET SET OPTIONS=? WHERE ID=?;", new Object[]{_options, _id});
	}

	public int toggleWidgetStatus(int _id, String _status) throws Exception {
		int value = _status.equals("true")?1:0;
		return this.cdb.update("UPDATE CENTRAL_WIDGET SET STATUS=? WHERE ID=?;", new Object[]{value, _id});
	}
	
	public int removeDashboard(int _id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_DASHBOARDS WHERE ID=?", new Object[]{_id});
	}
	
	public int removeDashboardOptions(int _did) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_DASHBOARD_OPTIONS WHERE DID=?", new Object[]{_did});
	}

	public int removeWidgetGroup(int _id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_WIDGET_GROUP WHERE ID=?", new Object[]{_id});
	}

	public int removeWidget(int _id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_WIDGET WHERE ID=?", new Object[]{_id});
	}

	public String getWidgetDataFromQuery(String _query) throws Exception {
		Properties pr = this.cdb.getQueryValue(_query);
		if(pr != null && !pr.isEmpty() && pr.containsKey("OPTIONS")){
			return pr.get("OPTIONS").toString();
		}
		return "";
	}

	public String getWidgetDataFromSP(String _sp_name) throws Exception {
		Properties pr = this.cdb.getSPValue(_sp_name);
		if(pr != null && !pr.isEmpty() && pr.containsKey("OPTIONS")){
			return pr.get("OPTIONS").toString();
		}
		return "";
	}
	
	public HashMap<String,String> get(String _query, Connection _conn) throws Exception {
		return this.get(_query,_conn, new Object[]{});
	}

	public HashMap<String,String> get(String _query, Connection _conn, Object... _args) throws Exception {

		HashMap<String,String> res = null;
		ResultSetMetaData rsmd;		
		PreparedStatement stmt = _conn.prepareStatement(_query);			
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}			
		ResultSet rs = stmt.executeQuery();	
		if (rs.isBeforeFirst()) {
			res = new HashMap<String,String>();
			rsmd = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
					res.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)).toString());
				}					
			}				
		}			
		if (stmt != null && stmt.isClosed() == false) {
			stmt.close();
		}
		return res;
	}


	public List<HashMap<String,String>> list(String _query, Connection _conn) throws Exception {
		return this.list(_query,_conn,new Object[]{});
	}

	public List<HashMap<String,String>> list(String _query, Connection _conn, Object... _args) throws Exception {
		List<HashMap<String,String>> res = null;		
		PreparedStatement stmt = _conn.prepareStatement(_query);			
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}			
		res =  this.prepareResultSetKeyVal(stmt.executeQuery());				
		if (stmt != null && stmt.isClosed() == false) {
			stmt.close();
		}
		return res;
	}


	public int insert(String _query, Connection _conn) throws Exception {
		return this.execute( _query,_conn, new Object[]{});
	}

	public int insert(String _query, Connection _conn, Object... _args) throws Exception {
		return this.execute( _query,_conn, _args);
	}

	public int delete(String _query, Connection _conn) throws Exception {
		return this.execute( _query,_conn, new Object[]{});
	}

	public int delete(String _query, Connection _conn, Object... _args) throws Exception {
		return this.execute(_query,_conn, _args);
	}

	public int update(String _query, Connection _conn) throws Exception {
		return this.execute( _query,_conn, new Object[]{});
	}

	public int update(String _query, Connection _conn, Object... _args) throws Exception {
		return this.execute( _query,_conn, _args);
	}

	/**
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	private int execute(String _query, Connection _conn, Object... _args) throws Exception {
		int id = 0;
		PreparedStatement stmt = _conn.prepareStatement(_query, Statement.RETURN_GENERATED_KEYS);			
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		if (rs.next()) {
			id = rs.getInt(1);
		}
		if (stmt != null && stmt.isClosed() == false) {
			stmt.close();
		}
		return id;
	}


	private List<HashMap<String,String>> prepareResultSetKeyVal(ResultSet _rs) throws Exception {
		ResultSetMetaData rsmd;
		HashMap<String,String> row = null;
		List<HashMap<String,String>> res = new ArrayList<HashMap<String,String>>();
		rsmd = _rs.getMetaData();		
		ArrayList<String> colList = new ArrayList<String>();			
		for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
			colList.add(rsmd.getColumnName(i));
		}	
		while (_rs.next()) {			
			row = new HashMap<String,String>();
			for (int i = 0; i < colList.size(); i++) {
				row.put(colList.get(i), _rs.getObject(colList.get(i)).toString());
			}
			res.add(row);
		}	
		return res;		
	}
	
}
