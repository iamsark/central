package com.hul.central.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.hul.central.base.CENTRAL;
import com.hul.central.exception.CentralAppException;
import com.hul.central.security.CentralAppContext;
import com.hul.central.utils.CentralAppUtils;
import com.hul.central.utils.CentralAppLogger;
import com.hul.central.annotation.Columnfield;
import com.hul.central.annotation.TableRef;

/**
 * 
 * JDBC wrapper for Central App related tasks<br>
 * We choose this basic approach because we have simple operations here with these modules<br>
 * The implementation itself simple and easy to follow, and it provide common interface for accessing database.
 * 
 * @author 	Sark
 * @since 	1.0.0
 *
 */

@Component
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class CentralAppDb implements CentralDb {

	@Autowired
	CentralAppUtils helper;
	
	@Autowired
	private CentralAppLogger logger;

	@Autowired
	private CentralAppContext context;
	
	
	@Autowired
	private CentralAppConnectionPool pool;
	
	/*max number of parameter for a single query*/
	private final int Max_Param_Size = 2100;
	
	@SuppressWarnings("unused")
	private final int Max_Col_Size = 300;
	
	/**
	 * Default constructor
	 */
	public CentralAppDb() {super();}
	
	/************************************************* Start of Count Block *******************************************/

	@Override
	public int count(String _query) throws Exception {
		return this.count(_query, new Object[]{});
	}

	@Override
	public int count(String _query, Object... _args) throws Exception {
		int res = -1;
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query);		
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}			
		ResultSet rs = stmt.executeQuery();	
		if (rs.isBeforeFirst()) {
			if (rs.next()) {
				res = rs.getInt(1);				
			}				
		}			
		this.disconnect(stmt, rs);	
		this.closeConnection(conn);
		return res;
	}
	
	public boolean exist(String _query) throws Exception {
		return this.exist(_query, new Object[]{});
	}

	@Override
	public boolean exist(String _query, Object... _args) throws Exception {
		boolean res = false;
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query);			
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}			
		ResultSet rs = stmt.executeQuery();
		if (rs.isBeforeFirst()) {
			res = true;
		}
		this.disconnect(stmt, rs);
		this.closeConnection(conn);
		return res;
	}
	
	/**************************************************** get block *****************************************************/
	
	/**
	 * 
	 * @param _query
	 * @param toObject
	 * @throws Exception
	 */
	public void get(String _query, Object _toObj) throws Exception {
		this.get(_query, _toObj, new Object[]{});
	}
	

	/**
	 * 
	 * @param _query
	 * @param toObject
	 * @param _args
	 * @throws Exception
	 */
	public void get(String _query, Object _toObj, Object... _args) throws Exception {
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query);
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}			
		ResultSet rs = stmt.executeQuery();		
		if (rs.next()) {
			this.objMapper(rs,_toObj);				
		}			
		this.disconnect(stmt, rs);
		this.closeConnection(conn);
	}

	@Override
	public Properties get(String _query) throws Exception {
		return this.get(_query, new Object[]{});
	}

	@Override
	public Properties get(String _query, Object... _args) throws Exception {
		Properties res = null;
		ResultSetMetaData rsmd;	
		Connection conn = this.getConnection();
		PreparedStatement stmt= conn.prepareStatement(_query);
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}	
		ResultSet rs = stmt.executeQuery();	

		if (rs.next()) {            
            res = new Properties();
			rsmd = rs.getMetaData();			
			for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {				
				res.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
			}					
							
		}		
		this.disconnect(stmt, rs);
		this.closeConnection(conn);
		return res;
	}
	
	/*********************************       end of get Block *********************************************************/
	
	
	/**************************************  start of list block ******************************************************/
	
	/**
	 * 
	 * @param _query
	 * @param _toObjList
	 * @param _toObj
	 * @throws Exception
	 */
	public <T> void list(String _query, List<T> _objList, Class<?> _toClass) throws Exception{
		this.list(_query, _objList, _toClass, new Object[]{});
	}


	/**
	 * 
	 * @param _query
	 * @param _toObjList
	 * @param _toObj
	 * @param _args
	 * @throws Exception
	 */
	public <T> void list(String _query, List<T> _toObjList, Class<?> _toClass, Object..._args) throws Exception{
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query);	
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}		
		ResultSet rs = stmt.executeQuery();
		if( rs != null  ){
			if (rs.isBeforeFirst()) {
				this.listObjMapper( rs,_toObjList, _toClass);
			}
		}
		this.disconnect(stmt, null);
		this.closeConnection(conn);
	}

	@Override
	public List<Properties> list(String _query) throws Exception {
		return this.list(_query, new Object[]{});
	}

	@Override
	public List<Properties> list(String _query, Object... _args) throws Exception {
		List<Properties> res = null;	
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query);	
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}			
		res = this.prepareResultSetKeyVal(stmt.executeQuery());				
		this.disconnect(stmt, null);
		this.closeConnection(conn);
		return res;
	}

	@Override
	public List<ArrayList<Object>> dietList(String _query) throws Exception {
		return this.dietList(_query, new Object[]{});
	}

	@Override
	public List<ArrayList<Object>> dietList(String _query, Object... _args) throws Exception {
		ArrayList<ArrayList<Object>> res = null;
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query);	
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}			
		res = this.prepareResultSet(stmt.executeQuery());
		this.disconnect(stmt, null);
		this.closeConnection(conn);
		return res;
	}
	
	/*********************************************************** End of List block ******************************************/

	/*********************************************************** Start of Insert block **************************************/

	@Override
	public int insert(String _query) throws Exception {
		return this.execute(_query, new Object[]{});
	}

	@Override
	public int insert(String _query, Object... _args) throws Exception {
		return this.execute(_query, _args);
	}
	
	@Override
	public int bulkInsert(String _query, List<Object[]> _args) throws Exception {
		int res = 0, colCount = 0;
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query, Statement.RETURN_GENERATED_KEYS);	
		if (_args.size() > 0) {
			colCount = _args.get(0).length;
			for(int i = 0; i < _args.size(); i++) {
				for (int j = 1; j <= colCount; j++) {			
					stmt.setObject(((i * colCount) + j), _args.get(i)[j-1]);
				}					
			}
		}
		res = stmt.executeUpdate();
		this.disconnect(stmt, null);
		this.closeConnection(conn);
		return res;
	}

	@Override
	public int delete(String _query) throws Exception {
		return this.execute(_query, new Object[]{});
	}

	@Override
	public int delete(String _query, Object... _args) throws Exception {
		return this.execute(_query, _args);
	}

	@Override
	public int update(String _query) throws Exception {
		return this.execute(_query, new Object[]{});
	}

	@Override
	public int update(String _query, Object... _args) throws Exception {
		return this.execute(_query, _args);
	}

	

	@Override
	public int bulkUpdate(String _query, List<Object[]> _args) throws Exception {
		int res = 0, colCount = 0;
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query, Statement.RETURN_GENERATED_KEYS);	
		if (_args.size() > 0) {
			colCount = _args.get(0).length;
			for(int i = 0; i < _args.size(); i++) {
				for (int j = 1; j <= colCount; j++) {			
					stmt.setObject(((i * colCount) + j), _args.get(i)[j-1]);
				}					
			}
		}
		res = stmt.executeUpdate();
		this.disconnect(stmt, null);
		this.closeConnection(conn);
		return res;
	}
	
	/**
	 * 
	 * Open a connection to DB Server ( which DB.?, of course it is System DB ) 
	 * We don't use Connection Pooling for SystemDB
	 * Since it will be accessed by System & Admin users only, so we don't need that high performance DB operation.
	 * They can wait for some time ( Of course they are administrators and they have enough time ).
	 * Throws <b>Exception</b> on when failed.
	 *  
	 * @return	Connection
	 * @throws 	Exception
	 * 
	 **/
	private Connection connect() throws Exception {
		return this.pool.getConnection();	
	}
	
	/**
	 * Connection maintenance in Db layer level.
	 * if transaction started it returns pre-stored connection
	 * otherwise it returns new connection.
	 * 
	 * @return Connection
	 * @throws Exception
	 */
	private Connection getConnection() throws Exception {		
		return this.connect();
	}
	
	/**
	 * Starting database transactions.
	 * upcoming all database and CRUD operations will use same connection.
	 * 
	 * 
	 * @throws Exception
	 */
	public void beginTransaction() throws Exception {
		if(!this.context.current().isTransactional()){
			this.context.current().beginTransaction();
			Connection conn = this.connect();
			conn.setAutoCommit(false);
			this.context.current().setConnection(conn);
			this.logger.log("Transaction Started", CENTRAL.LOG_INFO);
		}else{
			throw new CentralAppException("Transaction already started. Can't Start Again");
		}
	}
	
	
	/**
	 * 
	 * 
	 * @throws Exception
	 */
	private void startSingleConnection() throws Exception {
		if(!this.context.current().isTransactional()){
			this.context.current().setReuseConnection(true);
			Connection conn = this.connect();
			conn.setAutoCommit(false);
			this.context.current().setConnection(conn);
		}else{
			this.logger.log("already in single connection mode(transaction)", CENTRAL.LOG_INFO);
		}
	}
	
	private void closeConnection(Connection _conn) throws Exception {
		if(_conn != null && !_conn.isClosed()){
			if(this.context.current() != null && !this.context.current().isTransactional() && !this.context.current().isReuseConnection()){
				_conn.setAutoCommit(true);
				_conn.close();
				//System.out.println("Returning Connection to Pool");
				this.logger.log("Returning Connection to pool", CENTRAL.LOG_INFO);
				
			}
		}
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	private void stopSingleConnection() throws Exception {
		if(!this.context.current().isTransactional()){
			this.context.current().setReuseConnection(false);
			this.context.current().getConnection().commit();
			this.logger.log(this.context.current().getConnection().getWarnings().toString(),CENTRAL.LOG_WARNING);
			this.context.current().getConnection().clearWarnings();
			this.context.current().getConnection().close();
		}else{
			this.logger.log("already in single connection mode(transaction)", CENTRAL.LOG_INFO);
		}
	}
	
	/**
	 * Finishing started transaction.
	 * It commits all the db operations that happened in this transaction.
	 * 
	 * @throws Exception
	 */
	public void endTransaction() throws Exception {
		if(this.context.current().isTransactional()){
			this.context.current().getConnection().commit();
			this.context.current().endTransaction();
			this.context.current().getConnection().setAutoCommit(true);
			this.context.current().getConnection().close();
			this.logger.log("Transaction Completed", CENTRAL.LOG_INFO);
		}else{
			throw new CentralAppException("Can't Finish Transaction as it is not started.");
		}
	}
	
	/**
	 * Dropping started transaction.
	 * It rollbacks all the database operations that happened in this transaction. 
	 * 
	 * @throws Exception
	 */
	public void dropTransaction() throws Exception {
		if(this.context.current().isTransactional()){
			this.context.current().getConnection().rollback();
			this.context.current().endTransaction();
			this.context.current().getConnection().setAutoCommit(true);
			this.context.current().getConnection().close();
			this.logger.log("Transaction aborted", CENTRAL.LOG_ERROR);
		}else{
			throw new CentralAppException("Can't Drop Transaction as it is not started.");
		}
	}
	
	
	/**
	 * Access violation check
	 * Only Application users are allowed to access properties here.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void secure() throws Exception{
		if (!this.context.current().isAppUser()) {
			this.logger.log("Access Violation : User '"+ this.context.current().getUsername() +"' not authorized to access Application DB.!", CENTRAL.LOG_ERROR);
			throw new CentralAppException("Access violation : User '"+ this.context.current().getUsername() +"' not authorized to perform system wide task.!");
		}
	}
	
	/*************************************************** Start of Stored Procedures Block**************************************************/

	/**
	 * 
	 * @param _query
	 * @return
	 * @throws Exception
	 */
	public List<Properties> callSp(String _query) throws Exception {
		return this.callSp(_query, new Object[]{});
	}

	/**
	 * 
	 */
	@Override
	public List<Properties> callSp(String _query, Object... _args) throws Exception {
		List<Properties> res = null;
		Connection conn = this.getConnection();
		PreparedStatement cs = conn.prepareStatement("{"+_query+"}");
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				cs.setObject((i+1), _args[i]);
			}
		}
		boolean isTrue = cs.execute();
		while (!isTrue && (cs.getUpdateCount() != -1)) {
			isTrue = cs.getMoreResults();
		}
		if (isTrue) {
			ResultSet rs = cs.getResultSet();
			if(rs != null){
				if(rs.isBeforeFirst()){
					if (rs.next()) {
						res = this.prepareResultSetKeyVal(cs.getResultSet());
					}
				}
			}
		}
		this.disconnect(cs, null);
		this.closeConnection(conn);
		return res;
	}

	/**
	 * 
	 */
	public void callSp(String _query,Object _toObj, Object... _args) throws Exception {
		Connection conn = this.getConnection();
		CallableStatement cs = conn.prepareCall("{"+_query+"}");
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				cs.setObject((i+1), _args[i]);
			}
		}
		boolean isTrue = cs.execute();
		while (!isTrue && (cs.getUpdateCount() != -1)) {
			isTrue = cs.getMoreResults();
		}
		if (isTrue) {
			ResultSet rs = cs.getResultSet();
			if(rs != null){
				if(rs.isBeforeFirst()){
					if (rs.next()) {
						this.objMapper(rs, _toObj);
					}
				}
			}
		}
		this.disconnect(cs, null);
		this.closeConnection(conn);
	}
	
	public Properties getQueryValue(String _query) throws Exception {
		Properties res = null;
		ResultSetMetaData rsmd;		
		PreparedStatement stmt = this.getConnection().prepareStatement(_query);			
		ResultSet rs = stmt.executeQuery();	
		if (rs.isBeforeFirst()) {
			res = new Properties();
			rsmd = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
					res.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
				}					
			}				
		}			
		this.disconnect(stmt, rs);
		return res;
	}
	
	public Properties getSPValue(String _sp_name) throws Exception {
		List<Properties> res = null;
		CallableStatement cs = this.getConnection().prepareCall("{"+_sp_name+"}");
		boolean isTrue = cs.execute();
		while (!isTrue && (cs.getUpdateCount() != -1)) {
			isTrue = cs.getMoreResults();
		}
		if (isTrue) {
			ResultSet rs = cs.getResultSet();
			if(rs != null){
				if(rs.isBeforeFirst()){
					if (rs.next()) {
						res = this.prepareResultSetKeyVal(cs.getResultSet());
					}
				}
			}
			this.disconnect(cs, rs);
		}
		return res.isEmpty() ? null : res.get(0);
	}

	/********************************************** End Of Stored Procedure Block ***************************************************/
	
	
	/**
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	private int execute(String _query, Object... _args) throws Exception  {
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query, Statement.RETURN_GENERATED_KEYS);
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}
		int rst=  stmt.executeUpdate();
		
		if (rst == 1 && _query.toLowerCase().contains("insert")) {
			ResultSet generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				rst = (int) generatedKeys.getLong(1);
			}			
		}
		
		this.disconnect(stmt, null);
		this.closeConnection(conn);
		return rst;
	}
	
	/************************************************* Start of create drop table Block ****************************************************/

	/**
	 * 
	 * @param _query
	 * @return
	 * @throws Exception
	 */
	public int create(String _query) throws Exception {
		return this.run(_query,new Object[]{});
	}

	/**
	 * 
	 * @param _query
	 * @return
	 * @throws Exception
	 */
	public int create(String _query,Object..._args) throws Exception {
		return this.run(_query,_args);
	}




	/**
	 * 
	 * @param _query
	 * @return
	 * @throws Exception
	 */
	public int drop(String _query) throws Exception {
		return this.run(_query);
	}


	/**
	 * 
	 * @param _query
	 * @param _conn
	 * @return
	 * @throws Exception
	 */
	private int run(String _query,Object..._args) throws Exception {
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(_query, Statement.RETURN_GENERATED_KEYS);
		if (_args.length > 0) {
			for(int i = 0; i < _args.length; i++) {
				stmt.setObject((i+1), _args[i]);
			}
		}
		stmt.execute();
		this.disconnect(stmt, null);
		this.closeConnection(conn);
		return 0;
	}
	
	/******************************************************* Start of Utils block ***********************************************************/

	/**
	 * 
	 * This helper method is used to prepare the result set with "KEY: VAL" pairs<br>
	 * Used exclusively by the 'list' method. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_rs
	 * @return	ArrayList[Properties]
	 * @throws 	Exception
	 * 
	 **/
	private ArrayList<Properties> prepareResultSetKeyVal(ResultSet _rs) throws Exception {
		ResultSetMetaData rsmd;
		Properties row = null;
		ArrayList<Properties> res = new ArrayList<Properties>();
		if( _rs != null  ){
			if (_rs.isBeforeFirst()) {
				rsmd = _rs.getMetaData();		
				ArrayList<String> colList = new ArrayList<String>();			
				for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
					colList.add(rsmd.getColumnName(i));
				}	
				while (_rs.next()) {			
					row = new Properties();
					for (int i = 0; i < colList.size(); i++) {
						row.put(colList.get(i), _rs.getObject(colList.get(i)));
					}
					res.add(row);
				}
			}
		}
		return res;		
	}

	/**
	 * 
	 * This helper method is used to prepare the result set with stripped down list<br>
	 * Used exclusively by the 'dietList' method. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_rs
	 * @return	ArrayList[ArrayList[Object]]
	 * @throws 	Exception 
	 * 
	 **/
	private ArrayList<ArrayList<Object>> prepareResultSet(ResultSet _rs) throws SQLException {
		ResultSetMetaData rsmd;
		ArrayList<Object> row = null;
		ArrayList<ArrayList<Object>> res = new ArrayList<ArrayList<Object>>();
		rsmd = _rs.getMetaData();		
		ArrayList<Object> header = new ArrayList<Object>();
		/* To minimize casting load we use separate 'colList' object */
		ArrayList<String> colList = new ArrayList<String>();
		for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
			colList.add(rsmd.getColumnName(i));
			header.add(rsmd.getColumnName(i));
		}	
		res.add(header);
		if (_rs.isBeforeFirst()) {
			while (_rs.next()) {			
				row = new ArrayList<Object>();
				for (int i = 0; i < colList.size(); i++) {
					row.add(_rs.getObject(colList.get(i)));
				}
				res.add(row);
			}
		}
		return res;
	}

	/**
	 * 
	 * @param prList
	 * @param _toObjList
	 * @param _toObj
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> void listObjMapper(List<Properties> prList,List<T> _toObjList,Class<?> _toClass) throws Exception{
		for(Properties pr:prList){
			Object toObj = _toClass.getDeclaredConstructor().newInstance();
			this.objMapper(pr,toObj);
			_toObjList.add( (T) toObj);
		}
	}

	/**
	 * 
	 * @param _rs
	 * @param _toObjList
	 * @param _toObj
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private <T> void listObjMapper(ResultSet _rs,List<T> _toObjList,Class<?> _toClass) throws Exception{
		while(_rs.next()){
			Object toObj = _toClass.getDeclaredConstructor().newInstance();
			this.objMapper(_rs,toObj);
			_toObjList.add( (T) toObj);
		}
	}

	/**
	 * 
	 * @param _pr
	 * @param _toObj
	 * @throws Exception
	 */
	public void objMapper(Properties _pr,Object _toObj) throws Exception {
		for(Method method:_toObj.getClass().getDeclaredMethods()){
			if( method.getParameterTypes().length == 1 ){
				if (method.getAnnotations().length == 1 ){
					Columnfield field = (Columnfield) method.getAnnotations()[0];
					if( _pr.containsKey(field.name())){
						if( method.getParameterTypes()[0].getName().equals("java.lang.String") ){
							method.invoke(_toObj, (java.lang.String) _pr.get(field.name()));
						} else if( method.getParameterTypes()[0].getName().equals("int") ){
							if(field.type().equals("tinyint")){
								int tmpInt = (Short)_pr.get(field.name());
								method.invoke(_toObj,tmpInt);
							}else{
								method.invoke(_toObj, (int) _pr.get(field.name()));
							}
						} else if( method.getParameterTypes()[0].getName().equals("java.util.Date") ){
							method.invoke(_toObj, (java.util.Date) _pr.get(field.name()));
						} else if( method.getParameterTypes()[0].getName().equals("boolean") ){
							method.invoke(_toObj, (boolean) _pr.get(field.name()));
						} else if( method.getParameterTypes()[0].getName().equals("double") ){
							if(field.type().equals("numeric")){
								BigDecimal tmpVal = (BigDecimal)_pr.get(field.name());
								method.invoke(_toObj, tmpVal.doubleValue());
							}else{
								method.invoke(_toObj, (double) _pr.get(field.name()));
							}
						} else if( method.getParameterTypes()[0].getName().equals("long") ){
							method.invoke(_toObj, (long) _pr.get(field.name()));
						} else if( method.getParameterTypes()[0].getName().equals("float") ){
							method.invoke(_toObj, (float) _pr.get(field.name()));
						} else if( method.getParameterTypes()[0].getName().equals("short") ){
							method.invoke(_toObj, (Short)_pr.get(field.name()));
						}else{
							throw new CentralAppException("Ivalid Data type in "+_toObj.getClass().getName()+"in method "+method.getName());
						}
					}
				}
			}
		}
	}	
	
	@SuppressWarnings("unlikely-arg-type")
	private <T> boolean bulkInsert(List<T> _objlist) throws Exception {
		boolean flag = false;
		int listSize = _objlist.size();
		if(listSize > 0){
			this.startSingleConnection();
			StringBuilder query = new StringBuilder();
			StringBuilder values = new StringBuilder();
			StringBuilder finalQuery = new StringBuilder();
			query.append("INSERT INTO ");
			if(_objlist.get(0).getClass().getAnnotations().length == 1){
				TableRef tr  = (TableRef)_objlist.get(0).getClass().getAnnotations()[0];
				query.append(tr.name());
			} else {
				throw new Exception(" Table Name Not Declared in Model Class");
			}
			query.append("( ");
			values.append("( ");

			boolean isFirst = true;
			List<Method> methods = new ArrayList<>();
			List<Field> fields = new ArrayList<>();
			for(Field fld:_objlist.get(0).getClass().getDeclaredFields()){
				if( fld.getAnnotations().length == 1 ){
					if(!isFirst){
						query.append(",");
						values.append(",");
					}
					String prefix = "get";
					if(fld.getType().equals("boolean")){
						prefix = "is";
					}
					flag = true;
					Columnfield field = ((Columnfield) fld.getAnnotations()[0]);
					query.append(field.name());
					String methodName = prefix+fld.getName().substring(0, 1).toUpperCase() +  fld.getName().substring(1);
					methods.add(_objlist.get(0).getClass().getDeclaredMethod(methodName));
					fields.add(fld);
					values.append("?");
					isFirst = false;
				}
			}
			query.append(" ) VALUES ");
			values.append(" )");		
			List<Object> objParam = new ArrayList<>();
			int numberOfRows = 1;
			isFirst = true;
			boolean hasValues = false;
			for(int ind = 0; ind<listSize; ind++){
				if( methods.size() * (numberOfRows) >= this.Max_Param_Size  ){
					hasValues = false;
					isFirst = true;
					numberOfRows = 1;
					this.insert(finalQuery.toString(), objParam.toArray(new Object[objParam.size()]));
					finalQuery = new StringBuilder();
					objParam.clear();
				}
				hasValues = true;
				if(!isFirst){
					finalQuery.append(" , ");
				}else{
					finalQuery.append(query);
				}
				finalQuery.append(values);
				for(int col=0;col<methods.size();col++){
					if(fields.get(col).getType().getName().equals("double") || fields.get(col).getType().getName().equals("float")){
						objParam.add(CentralAppUtils.decimalAmt((double)methods.get(col).invoke(_objlist.get(ind)),((Columnfield) fields.get(col).getAnnotations()[0]).dpCount()));
					}else {
						objParam.add(methods.get(col).invoke(_objlist.get(ind)));
					}
				}
				isFirst = false;
				numberOfRows++;
			}
			if(hasValues){
				this.insert(finalQuery.toString(), objParam.toArray(new Object[objParam.size()]));
			}
			this.stopSingleConnection();
		}
		return flag;
	}
	
	public <T> boolean save(List<T> _objlist) throws Exception {
		return this.bulkInsert(_objlist);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean save(Object _obj) throws Exception {
		boolean flag = false;
		StringBuilder query = new StringBuilder();
		StringBuilder values = new StringBuilder();
		query.append("INSERT INTO ");
		if(_obj.getClass().getAnnotations().length == 1){
			TableRef tr  = (TableRef)_obj.getClass().getAnnotations()[0];
			query.append(tr.name());
		} else {
			throw new Exception("Table Name Not Declared in Model Class "+_obj.getClass().getName());
		}
		query.append("( ");
		values.append("( ");
		List<Object> objParam = new ArrayList<>();
		int i=0;
		Method m;
		for(Field fld:_obj.getClass().getDeclaredFields()){
			if( fld.getAnnotations().length == 1 ){
				if(i != 0){
					query.append(",");
					values.append(",");
				}
				flag = true;
				Columnfield field = (Columnfield) fld.getAnnotations()[0];
				String prefix = "get";
				if(fld.getType().equals("boolean")){
					prefix = "is";
				}
				String methodName = prefix + fld.getName().substring(0, 1).toUpperCase() + fld.getName().substring(1);
				m = _obj.getClass().getDeclaredMethod(methodName);
				if(fld.getType().getName().equals("float") ){
					objParam.add(CentralAppUtils.decimalAmt((float)m.invoke(_obj), field.dpCount()));
				}else if( fld.getType().getName().equals("double")){
					objParam.add(CentralAppUtils.decimalAmt((double)m.invoke(_obj), field.dpCount()));
				}else {
					objParam.add(m.invoke(_obj));
				}
				query.append(field.name());
				values.append("?");
				i++;
			}
		}
		query.append(" )");
		values.append(" )");
		if(flag){
			query.append(" VALUES ").append(values);
		}
		this.insert(query.toString(), objParam.toArray(new Object[objParam.size()]));
		return flag;
	}
	
	public void objMapper(ResultSet _rs,Object _toObj) throws Exception {
		for(Field fld:_toObj.getClass().getDeclaredFields()){
			if(fld.getAnnotations().length == 1){
				Columnfield field = (Columnfield) fld.getAnnotations()[0];
				String methodName = "set" + fld.getName().substring(0, 1).toUpperCase() + fld.getName().substring(1);
				Method method = _toObj.getClass().getDeclaredMethod(methodName,fld.getType());
				try{
					if(_rs.getObject(field.name()) != null ){
						if(fld.getType().getName().equals("java.lang.String")){
							method.invoke(_toObj, (java.lang.String) _rs.getObject(field.name()));
						}else if(fld.getType().getName().equals("int")){
							if(field.type().equals("tinyint")){
								int tmpInt = (Short)_rs.getObject(field.name());
								method.invoke(_toObj,tmpInt);
							}else{
								method.invoke(_toObj, (int) _rs.getObject(field.name()));
							}
						}else if(fld.getType().getName().equals("java.util.Date")){
							method.invoke(_toObj, (java.util.Date) _rs.getObject(field.name()));
						}else if(fld.getType().getName().equals("boolean")){
							method.invoke(_toObj, (boolean) _rs.getObject(field.name()));
						}else if(fld.getType().getName().equals("double")){
							if(field.type().equals("numeric")){
								BigDecimal tmpVal = (BigDecimal)_rs.getObject(field.name());
								method.invoke(_toObj, tmpVal.doubleValue());
							}else{
								method.invoke(_toObj, (double) _rs.getObject(field.name()));
							}
						}else if(fld.getType().getName().equals("long")){
							method.invoke(_toObj, (long) _rs.getObject(field.name()));
						}else if(fld.getType().getName().equals("float")){
							method.invoke(_toObj, (float) _rs.getObject(field.name()));
						}else if(fld.getType().getName().equals("short")){
							method.invoke(_toObj, (Short)_rs.getObject(field.name()));
						}else {
							throw new CentralAppException("Ivalid Data type in class  "+ _toObj.getClass().getName() +"  in method "+ method.getName());
						}
					}
				} catch(Exception e){
					System.out.println(e.getMessage());
					System.out.println("Column Name is not valid Exception");
				}
			}
		}
	}
	
	/**
	 * 
	 * Well no magic, any opened connection must be closed and thats what it doing<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_stream
	 * @param 	_stmt
	 * @param 	_rs
	 * @throws 	Exception
	 * 
	 **/
	private void disconnect(Statement _stmt, ResultSet _rs) throws Exception {
		if (_rs != null && !_rs.isClosed()) {
			_rs.close();
		}
		if (_stmt != null && !_stmt.isClosed()) {
			_stmt.close();
		}
	}



	/**
	 * 
	 * Well no magic, any opened connection must be closed and thats what it doing<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_stream
	 * @param 	_stmt
	 * @param 	_rs
	 * @throws 	Exception
	 * 
	 **/
	@SuppressWarnings("unused")
	private void disconnectAll(Statement _stmt, ResultSet _rs) throws Exception {
		Connection _stream = ( _stmt != null ) ?_stmt.getConnection() : null;
		if (_rs != null && !_rs.isClosed()) {
			_rs.close();
		}
		if (_stmt != null && !_stmt.isClosed()) {
			_stmt.close();
		}
		if (_stream != null && !_stream.isClosed()) {
			_stream.close();
		}
	}

}
