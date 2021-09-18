package com.hul.central.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.hul.central.base.CENTRAL;
import com.hul.central.security.CentralAppContext;
import com.hul.central.system.model.CentralAppUser;
import com.hul.central.utils.CentralAppLogger;


/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Component
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
@PropertySource(value={CENTRAL.SYSTEM_PROPERTIES})
public class CentralAppDataSource {

	@Autowired
	private CentralAppContext ic;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private CentralAppLogger logger;
	
	/**
	 * 
	 * Used by the System, Support and Admin Users.
	 * 
	 * @return DataSource
	 */
	public DataSource getSystemDataSource() {
		boolean res = true;
		String dbInstance = "mssql";
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		
		if (env.getProperty("central.system.db.instance") != null) {
			dbInstance = env.getProperty("central.system.db.instance");
		}
		
		if (dbInstance.equals("mssql")) {
			if (env.getProperty("central.system.db.mssql.driver.class") != null) {
				datasource.setDriverClassName(env.getProperty("central.system.db.mssql.driver.class"));
			} else {
				res = false;
				this.logger.slog("Driver Class Name is not set for System DB [MSSQL]", CENTRAL.LOG_ERROR);
			}
			if (env.getProperty("central.system.db.mssql.url") != null) {
				datasource.setUrl(env.getProperty("central.system.db.mssql.url"));		
			} else {
				res = false;
				this.logger.slog("Connection url is not set for System DB [MSSQL]", CENTRAL.LOG_ERROR);
			}
			if (env.getProperty("central.system.db.mssql.username") != null) {
				datasource.setUsername(env.getProperty("central.system.db.mssql.username"));
			} else {
				res = false;
				this.logger.slog("DB user name is not set for System DB [MSSQL]", CENTRAL.LOG_ERROR);
			}
			if (env.getProperty("central.system.db.mssql.password") != null) {
				datasource.setPassword(env.getProperty("central.system.db.mssql.password"));
			} else {
				res = false;
				this.logger.slog("DB user password is not set for System DB [MSSQL]", CENTRAL.LOG_ERROR);
			}
		} else {
			if (env.getProperty("central.system.db.mysql.driver.class") != null) {
				datasource.setDriverClassName(env.getProperty("central.system.db.mysql.driver.class"));
			} else {
				res = false;
				this.logger.slog("Driver Class Name is not set for System DB [MySQL]", CENTRAL.LOG_ERROR);
			}
			if (env.getProperty("central.system.db.mysql.url") != null) {
				datasource.setUrl(env.getProperty("central.system.db.mysql.url"));		
			} else {
				res = false;
				this.logger.slog("Connection url is not set for System DB [MySQL]", CENTRAL.LOG_ERROR);
			}
			if (env.getProperty("central.system.db.mysql.username") != null) {
				datasource.setUsername(env.getProperty("central.system.db.mysql.username"));
			} else {
				res = false;
				this.logger.slog("DB user name is not set for System DB [MySQL]", CENTRAL.LOG_ERROR);
			}
			if (env.getProperty("central.system.db.mysql.password") != null) {
				datasource.setPassword(env.getProperty("central.system.db.mysql.password"));
			} else {
				res = false;
				this.logger.slog("DB user password is not set for System DB [MySQL]", CENTRAL.LOG_ERROR);
			}
		}
				
		if (res) {
			return datasource;
		}
		return null;
	}
		
	/**
	 * 
	 * Used by the DB Explorer module
	 * 
	 * @param _ids
	 * @return DataSource
	 * 
	 */
	public DataSource getDataSource(CentralAppDbDetails _ids) {
		boolean res = true;
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		if (_ids.getDriver() != null && !_ids.getDriver().isEmpty()) {
			datasource.setDriverClassName(_ids.getDriver());
		} else {
			res = false;
			this.logger.log("Driver Class Name is not set for DS : "+_ids.getName(), CENTRAL.LOG_ERROR);
		}
		if (_ids.getConn_str() != null && !_ids.getConn_str().isEmpty()) {
			datasource.setUrl(_ids.getConn_str());
		} else {
			res = false;
			this.logger.slog("Connection url is not set for DS : "+ _ids.getName(), CENTRAL.LOG_ERROR);
		}
		if (_ids.getUser() != null && !_ids.getUser().isEmpty()) {
			datasource.setUsername(_ids.getUser());
		} else {
			res = false;
			this.logger.slog("DB user name is not set for DS : "+ _ids.getName(), CENTRAL.LOG_ERROR);
		}
		if (_ids.getPass() != null && !_ids.getPass().isEmpty()) {
			datasource.setPassword(_ids.getPass());
		} else {
			res = false;
			this.logger.slog("DB puser password is not set for DS : "+ _ids.getName(), CENTRAL.LOG_ERROR);
		}
		if (res) {
			return datasource;
		}
		return null;
	}
	
	/**
	 * 
	 * Used for Testing DB connection Properties
	 * 
	 * @param _driver
	 * @param _url
	 * @param _user
	 * @param _pass
	 * @return DataSource
	 * 
	 */
	public DataSource getDataSource(String _driver, String _url, String _user, String _pass) {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		if ((_driver != null && !_driver.equals(""))
				&& (_url != null && !_url.equals(""))
				&& (_user != null && !_user.equals(""))
				&& (_pass != null && !_pass.equals(""))) {
			datasource.setDriverClassName(_driver);
			datasource.setUrl(_url);
			datasource.setUsername(_user);
			datasource.setPassword(_pass);
			return datasource;
		}
		return null;
	}
	
}
