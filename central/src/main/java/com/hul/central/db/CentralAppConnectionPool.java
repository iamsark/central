package com.hul.central.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.hul.central.base.CENTRAL;
import com.hul.central.security.CentralAppContext;
import com.hul.central.system.model.CentralAppUser;
import com.hul.central.utils.CentralAppLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Component
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class CentralAppConnectionPool {
	
	@Autowired
	private CentralAppContext ic;

	@Autowired
	private CentralAppDataSource ids;
	
	private Map<String,HikariDataSource> pooledConnections;
	
	private final int max_pool_size = 5;
	private final boolean auto_commit = true;
	private final int min_idle_connections = 2;
	
	public CentralAppConnectionPool(){
		this.pooledConnections = new HashMap<String,HikariDataSource>();
	}
	
	/**
	 * open new connection pool for ACM Database
	 * 
	 * @throws Exception
	 */
	public void initiateCp() throws Exception {
		System.out.println("Initiating Connection pool sequence.....");
		//this.logger.log("Initiating Connection pool sequence...", IKEA.LOG_INFO);
		HikariConfig config = new HikariConfig();
		config.setAutoCommit(this.auto_commit);
		config.setDataSource(this.ids.getSystemDataSource());
		config.setMaximumPoolSize(this.max_pool_size);
		config.setMinimumIdle(this.min_idle_connections);
		config.setPoolName("CentralAppCP");
		this.ic.setDs(new HikariDataSource(config));
		System.out.println("Connection pool - "+this.ic.getDs().getPoolName()+" is Initiated");
	}
	
	/**
	 * Close connection pool for ACM Database
	 * 
	 * @throws Exception
	 */
	public void closeCP() throws Exception {
		System.out.println("Closing Connection pool sequence.....");
		this.ic.getDs().close();
	}
	
	/**
	 * Supply connection object from connection pool
	 * 
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception{
		return this.getConnectionFromPool(this.ic.getDs());
	}
	
	/**
	 * supply connection Object from app connection pool
	 * 
	 * @param _ds
	 * @return
	 * @throws Exception
	 */
	private Connection getConnectionFromPool(HikariDataSource _ds) throws Exception{
		return _ds.getConnection();
	}
	
	
	/**
	 * 
	 * 
	 * @param _user
	 * @throws Exception
	 */
	public void closeAllAppConnectionPools() throws Exception {
		for (HikariDataSource ds : this.pooledConnections.values()) {
			if(!ds.isClosed()){
				ds.close();
			}
		}
		this.pooledConnections.clear();
	}
	
}
