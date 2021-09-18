package com.hul.central.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 * JDBC wrapper interface for CentralApp DB operations.<br>
 * It provides a common way to implement DB operation abstraction.<br>
 * Basically CentralApp has two level DB access
 * <ul>
 * <li><b>App</b> user's Access</li>
 * <li><b>System</b> & <b>Admin</b> user's Access</li>
 * </ul>
 * So we have two implementation of this interface for usage, 1. CentralSystemDb 2. CentralAppDb.
 * Both instance will be used based on the signed in user's role.
 * 
 * @author  Sark
 * @since	1.0.0
 *
 **/

public interface CentralDb {
	
	/**
	 * 
	 *Overloaded method for count, without WHERE clause. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @return 	integer
	 * @throws 	Exception
	 * 
	 **/
	int count(String _query) throws Exception;
	
	/**
	 * 
	 * Execute the given query ( of course it has to be a Count query ) and returns the total number of rows as result<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return 	integer
	 * @throws 	Exception
	 **/
	int count(String _query, Object... _args) throws Exception;
	
	/**
	 * 
	 * Execute the query and return TRUE or FALSE.<br> 
	 * If the query has any result set then return TRUE<br>
	 * Otherwise FALSE. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	boolean
	 * @throws 	Exception
	 * 
	 **/
	boolean exist(String _query, Object... _args) throws Exception;
	
	/**
	 * 
	 * Overloaded method of get, without WHERE clause<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param	_query
	 * @return 	Properties
	 * @throws 	Exception
	 * 
	 **/
	Properties get(String _query) throws Exception;
	
	/**
	 * 
	 * Execute the query and returns the single row as wrapped inside a Properties<br>
	 * If multiple rows found it still return the first row alone<br>
	 * Throws <b>Exception</b> on when failed.<br>
	 * The returns format would be like<br><br>
	 * { COLUMN_1: VALUE 1, COLUMN_2: VALUE 2, COLUMN_3: VALUE 3 .... }
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	Properties
	 * @throws 	Exception
	 * 
	 **/
	Properties get(String _query, Object... _args) throws Exception;
	
	/**
	 * 
	 * Execute the query and returns the result set as List of Properties<br>
	 * This is a overloaded method of 'list' without WHERE clause<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @return	List[Properties]
	 * @throws 	Exception
	 * 
	 **/
	List<Properties> list(String _query) throws Exception;
	
	/**
	 * 
	 * Execute the given query and returns the result set as List of Properties<br>
	 * It also accept WHERE clause as array of object<br> 
	 * Throws <b>Exception</b> on when failed.<br>
	 * The return format would be like<br><br>
	 * 
	 * [<br>
	 * &nbsp;&nbsp;{ COLUMN_1: VALUE 1, COLUMN_2: VALUE 2, COLUMN_3: VALUE 3 .... },<br>
	 * &nbsp;&nbsp;{ COLUMN_1: VALUE 1, COLUMN_2: VALUE 2, COLUMN_3: VALUE 3 .... },<br>
	 * &nbsp;&nbsp;{ COLUMN_1: VALUE 1, COLUMN_2: VALUE 2, COLUMN_3: VALUE 3 .... },<br>
	 * &nbsp;&nbsp;.....<br>
	 * ]
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	List[Properties]
	 * @throws 	Exception
	 * 
	 **/
	List<Properties> list(String _query, Object..._args) throws Exception;
	
	/**
	 * 
	 * Execute the query and returns the result set as List of Properties<br>
	 * Overloaded method of 'dietList' without WHERE clause<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param _query
	 * @return
	 * @throws Exception
	 * 
	 **/
	List<ArrayList<Object>> dietList(String _query) throws Exception;
	
	/**
	 * 
	 * Method for getting list of rows from a table<br>
	 * The difference between this method and 'list' is lies in the return type<br>
	 * Accept two parameters, one is query itself and another is where clause values ( if any )<br>
	 * The very first row would be the List of Columns and subsequent rows would be the actual records<br>
	 * Throws <b>Exception</b> on when failed.<br>
	 * This method will return the stripped down rows ( Hence diet list ) in the following format<br><br>
	 * [<br>
	 * &nbsp;&nbsp;[ Column Name 1, Column Name 2, Column Name 3, ... ],<br>
	 * &nbsp;&nbsp;[ Column Value 1, Column Value 2,Column Value 3, ... ],<br>
	 * &nbsp;&nbsp;[ Column Value 1, Column Value 2,Column Value 3, ... ],<br>
	 * &nbsp;&nbsp;.....<br>
	 * ]
	 * 
	 * @param stayPut
	 * @param query
	 * @param values
	 * @return 
	 * @throws Exception
	 *  
	 **/
	List<ArrayList<Object>> dietList(String _query, Object... _args) throws Exception;
	
	/**
	 * 
	 * Execute the given query for inserting single or multiple rows ( query should be delimited by ';' for multiple rows )<br>
	 * This method doesn't accept insert values separately instead you have to include it in the query itself<br/>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	int insert(String _query) throws Exception;
	
	/**
	 * 
	 * Execute the given query for inserting single or multiple rows ( query should be delimited by ';' for multiple rows )<br>
	 * It accept insert values separately as Object Array. Throws <b>Exception</b> on when failed.<br>
	 * The query format should be like this<br><br>
	 * 
	 *  INSERT INTO TABLE_NAME ( COLUMN_1, COLUMN_2, COLUMN_3 ... ) VALUES (?,?,? ...), (?,?,? ...), (?,?,? ...) ...<br>
	 *  Object[ col 1 value, col 2 value, col 3 value ..., col 1 value, col 2 value, col 3 value ... ]
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	int insert(String _query, Object... _args) throws Exception;
	
	/**
	 * 
	 * Execute the given query for deleting single or multiple rows<br>
	 * Doesn't accept WHERE clause though. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	int delete(String _query) throws Exception;
	
	/**
	 * 
	 * Execute the given query for deleting single or multiple rows<br>
	 * It does accept WHERE clause as Object Array. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	int delete(String _query, Object... _args) throws Exception;	
	
	/**
	 * 
	 * Execute the given query for Updating single or multiple rows<br>
	 * Doesn't accept WHERE clause though. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	int update(String _query) throws Exception;
	
	/**
	 * 
	 * Execute the given query for Updating single or multiple rows<br>
	 * It does accept WHERE clause as Object Array. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	int update(String _query, Object... _args) throws Exception;
	
	/**
	 * 
	 * Use this method if you have hundreds of insert operation instead of using 'insert'<br>
	 * It accept insert values as List of Object Array ( each Object array represent single row value )<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	public int bulkInsert(String _query, List<Object[]> _args) throws Exception;
	
	/**
	 * 
	 * Use this method if you have hundreds of update operation instead of using 'update'<br>
	 * It accept updates values as List of Object Array ( each Object array represent single row value )<br>
	 * Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	integer
	 * @throws 	Exception
	 * 
	 **/
	public int bulkUpdate(String _query, List<Object[]> _args) throws Exception;
	
	/**
	 * 
	 * Use this method to call any stored procedure
	 * Accept WHERE clause as Object Array. Throws <b>Exception</b> on when failed.
	 * 
	 * @param 	_query
	 * @param 	_args
	 * @return	
	 * @throws 	Exception
	 * 
	 **/
	List<Properties> callSp(String _query, Object..._args) throws Exception;

}
