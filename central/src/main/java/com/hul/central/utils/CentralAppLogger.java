package com.hul.central.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.time.Instant;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.hul.central.base.CENTRAL;
import com.hul.central.security.CentralAppContext;
import com.hul.central.system.model.CentralAppUser;


/**
 * 
 * @author Sark
 * @since 1.0.0
 * 
 */

@Component
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
@PropertySource(value={CENTRAL.SYSTEM_PROPERTIES})
public class CentralAppLogger implements Runnable {

	@Autowired
	private CentralAppContext ic;
	
	@Autowired
	private Environment env;
	
	private long startTime;
	
	/* Holds the FS location type */
	private String bucketType;
	
	/* Holds the FS absolute path */
	private String bucketRoot;
	
	/* Logger demon instance */
	private Thread demon;
	
	/* Used to stop the demon thred */
	private boolean die;
	
	/* Logger demon frequency */
	@Value( "${central.system.logger.demon.delay}" )
	private int tickerDelay = 10;
	
	/* Maximum entry threshold for logger queue */
	@Value( "${central.system.logger.queue.threshold}" )	
	private int queueThreshold = 100;
	
	/* Logger entry queue */	
	private Queue<Properties> queue = null;
	
	public CentralAppLogger() { 
		super();
		this.startTime = 0;
		/* Instantiate the Log Queue */
		this.queue = new LinkedList<Properties>();
		/* Start the Logger thread */
		this.die = false;
		this.demon = new Thread(this);
		this.demon.start();
	}
	
	@PostConstruct
	public void init() throws Exception {
		/* Load FS location type from the Config */
		this.bucketType = this.env.getProperty("central.system.bucket.location.type");
		/* Load FS location abs path from the Config */
		this.bucketRoot = this.env.getProperty("central.system.bucket.location");
		/* If it is internal then init the 'bucketRoot' with class path */
		if (this.bucketType.equals("internal")) {
			this.bucketRoot = getClass().getClassLoader().getResource("").getPath();
		}	
		/* Make sure the FS root is set and suffixed with "/" */
		if (this.bucketRoot != null && !this.bucketRoot.equals("")) {
			if (!this.bucketRoot.endsWith( "/" )) {
				this.bucketRoot = this.bucketRoot +"/";
			}
		} else {
			/* Look like the FS root has been marked foe External Location
			 * But the location given is  */
		}		
		/* Inspect the root directories for logs */
		this.checkBaseDirectories();
	}
	
	public void startDemon() { 
		if (!this.demon.isAlive()) {
			this.demon = new Thread(this);
			this.demon.start();
		}
	}
	
	public void stopDemon() { System.out.println("Stopping Logger Demon");
		if (this.demon.isAlive()) {
			this.die = true;
			this.demon.interrupt();
		}
	}
	
	public void restartDemon() {
		this.stopDemon();
		this.startDemon();
	}
	
	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/** 
	 * 
	 * Logger method for application modules, primarily used by the Module Developers<br>
	 * _msg is the actual message that has to be logged<br>
	 * _type represent the message type, there are four types of messages<br>
	 * Use IKEA global constant for this value<br><br>
	 * 
	 * <b>IKEA.IKEA_LOG_INFO</b> for normal message<br>
	 * <b>IKEA.IKEA_LOG_ERROR</b> for error message<br>
	 * <b>IKEA.IKEA_LOG_WARNING</b> for warning message<br>
	 * <b>IKEA.IKEA_LOG_SUCCESS</b> for success message<br>	
	 * 
	 * @param _type
	 * @param _msg
	 *
	 */
	public void log(String _msg, String _type) {
		this.log("app", _type, _msg, null, null);
		/* Rush .. Rush ... don't put the caller waiting longer */
		return;
	}
	
	/**
	 * 
	 * Overloaded method for "log", useful when something has to be logged while session timeout 
	 * 
	 * @param _msg
	 * @param _type
	 * @param _user
	 * 
	 */
	public void log(String _msg, String _type, CentralAppUser _user) {
		this.log("app", _type, _msg, null, _user);
		/* Rush .. Rush ... don't put the caller waiting longer */
		return;
	}
	
	/**
	 * 
	 * Logger method for admin modules, primarily used by the Admin Module Developer<br>
	 * _msg is the actual message that has to be logged<br>
	 * _type represent the message type, there are four types of messages<br>
	 * Use IKEA global constant for this value<br><br>  
	 * 
	 * <b>IKEA.IKEA_LOG_INFO</b> for normal message<br>
	 * <b>IKEA.IKEA_LOG_ERROR</b> for error message<br>
	 * <b>IKEA.IKEA_LOG_WARNING</b> for warning message<br>
	 * <b>IKEA.IKEA_LOG_SUCCESS</b> for success message<br>
	 * 
	 * @param _msg
	 * @param _type
	 * 
	 */
	public void alog(String _msg, String _type) {
		this.log("admin", _type, _msg, null, null);
		/* Rush .. Rush ... don't put the caller waiting longer */
		return;
	}
	
	/**
	 * 
	 * Logger method for system modules, primarily used by the System Module Developer<br>
	 * _msg is the actual message that has to be logged<br>
	 * _type represent the message type, there are four types of messages<br>
	 * Use IKEA global constant for this value<br><br>  
	 * 
	 * <b>IKEA.IKEA_LOG_INFO</b> for normal message<br>
	 * <b>IKEA.IKEA_LOG_ERROR</b> for error message<br>
	 * <b>IKEA.IKEA_LOG_WARNING</b> for warning message<br>
	 * <b>IKEA.IKEA_LOG_SUCCESS</b> for success message<br>
	 * 
	 * @param _msg
	 * @param _type
	 * 
	 */
	public void slog(String _msg, String _type) {
		this.log("system", _type, _msg, null, null);
		/* Rush .. Rush ... don't put the caller waiting longer */
		return;
	}
	
	/**
	 * 
	 * Logger method for exception trapper, not to be used by any module developers<br>
	 * 
	 * @param _msg
	 * @param _type
	 * 
	 */
	public void elog(Exception _e) {_e.printStackTrace();
		//this.log("exception", IKEA.LOG_ERROR, "", _e, null);
		/* Rush .. Rush ... don't put the caller waiting longer */
		return;
	}
	
	/**
	 * 
	 * @param _target
	 * @param _class
	 * @param _type
	 * @param _msg
	 * 
	 */
	private void log(String _target, String _type, String _msg, Exception _e, CentralAppUser _user) {
		/* Get the current user */
		CentralAppUser user = this.ic.current();
		/* Make sure the user object is there on the session
		 * Some time the session got expired when the user tries log something */
		user = (user != null) ? user : (_user != null) ? _user : null; 
		
		/* Determine the target folders */
		String file = "";
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		
		if (user != null && (_target.equals("app") || _target.equals("admin"))) {
			file = this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/" + _target + "/" + year + "/system/" + month + "_log.json";
		} else {
			file = this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/" + _target + "/" + year + "/" + month + "_log.json";
		}		
		
		/* Prepare the log entry */
		Properties entry = new Properties();
		Properties message = new Properties();
		
		/* Helper property entry for the logger
		 * to determine the  */		
		entry.put("file", file);
		entry.put("target", _target);
		entry.put("year", year);
		entry.put("month", month);
		
		/* Message property entry
		 * which actually used to make the log entry */
		if (!_target.equals("exception")) {
			/* Put RS name - which will be used to check for the RS directory exist or not */
			
			if (user != null) {
				entry.put("rs", "system");			
				/* User email */
				message.put("e", user.getUsername());
			} else {
				entry.put("rs", "Pre Session");			
				/* User email */
				message.put("e", "Pre Session");
			}			
			
			/* The type of the LOG message */
			message.put("t", _type);
			/* Actual log message */
			message.put("m", StringEscapeUtils.escapeHtml(_msg));			
		} else {
			if (user == null) {
				message.put("e", "Happened before authentication");
			} else {
				/* User email */
				message.put("e", user.getUsername());
				/* RS name */
				message.put("r", "system");				
				/* User type */
				if (user.isAdmin()) {
					message.put("u", "ADMIN");
				} else if (user.isAppUser()) {
					message.put("u", "APP");
				} else if (user.isSystem()) {
					message.put("u", "SYSTEM");
				}
			}
			/* Exception type class name */
			message.put("c", ExceptionUtils.getRootCauseMessage(_e));
			/* Exception message */
			message.put("m", _e.getMessage());
			/* Here 't' represent Stack Trace */
			message.put("t", ExceptionUtils.getStackTrace(_e));		
		}
		
		/* Time stamp */
		message.put("s", Instant.now().getEpochSecond());
		
		/* Message */
		entry.put("message", message);
				
		/* Put it in the Log Queue */
		this.queue.add(entry);
		
		if (this.queue.size() > this.queueThreshold && this.demon.isAlive()) {
			this.demon.interrupt();
		}
		
		/* Hurry ... Hurry ... */
		return;
	}
	
	private void write() {
		String hash = "";		
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		Properties entry = null;
		Map<String, ArrayList<Properties>> job = new HashMap<String, ArrayList<Properties>>();
		
		/* Group the log entries based on target file */
		while(!this.queue.isEmpty()) {
			entry = this.queue.poll();
			hash = DigestUtils.md5Hex((String) entry.get("file")).toUpperCase();
			if (!job.containsKey(hash)) {
				job.put(hash, new ArrayList<Properties>());
			}	
			job.get(hash).add(entry);
		}

		/* Make sure the Year & RS directories are exist */
		for (ArrayList<Properties> entries : job.values()) {
		    for (int i = 0; i < entries.size(); i++) {
		    	if (i == 0) {
		    		String base = (this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/" + entries.get(i).get("target") + "/");
		    		/* Check for the Year directory */
		    		this.checkDirectoryExist(base, String.valueOf(entries.get(i).get("year")));
		    		/* Check for the RS directory */
		    		if (entries.get(i).get("target").equals("app") || entries.get(i).get("target").equals("admin")) {
		    			this.checkRsDirectory((base + entries.get(i).get("year") + "/" ), (String) entries.get(i).get("rs"));
		    		}		    		
		    	}
			}		
		}
		
		/* Prepare json serializer */
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		ObjectWriter ow = om.writer();
		
		/* Well we are good to go, lets write it down all the logs */
		for (ArrayList<Properties> entries : job.values()) {
			try {
				if (entries.size() > 0) {
					/* Initiate the writer Objects */
					fw = new FileWriter((String)entries.get(0).get("file"), true);
					bw = new BufferedWriter(fw);
					out = new PrintWriter(bw);				
					for (int i = 0; i < entries.size(); i++) {			
						/* Write it down */
						out.write("," + ow.writeValueAsString(entries.get(i).get("message")));
					}					
					/* Close the writers */
					out.close();
					bw.close();
					fw.close();					
				}					
			} catch(Exception e) {
				/* Well what we can say, Logger itself failed,
				 * No other way, Let the Tomcat handle it */
				e.printStackTrace();
			}
		}		
	}
	
	public List<String> listYear(String _target) {
		ArrayList<String> directories = new ArrayList<String>();
        File[] yDirectories = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/"+ _target).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return dir.isDirectory();
            }
        }); 
        for (int i = 0; i < yDirectories.length; i++) {
        	directories.add(yDirectories[i].getName());
        }
        return directories;       
	}
	
	public Properties listMonth(String _target) {
		String mname;
		Properties months = new Properties(); 
		File base = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/"+ _target);
		if (base.isDirectory()) {			
			File[] files = base.listFiles();
			for (int i = 0; i < files.length; i++) {
				mname = files[i].getName();
				/* Trim the [_log.json] suffix */
				mname = mname.replace("_log.json", "");				
				months.put(mname, new DateFormatSymbols().getMonths()[Integer.parseInt(mname)]);
			}
		}
		return months;
	}
	
	public ArrayList<String> listRS(String _year) {
		ArrayList<String> directories = new ArrayList<String>();
		File[] yDirectories = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/app/"+ _year).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return dir.isDirectory();
            }
        }); 
		for (int i = 0; i < yDirectories.length; i++) {
        	directories.add(yDirectories[i].getName());
        }
        return directories;  
	}
	
	public Integer[] listYearAsArray(String _type) {
		List<Integer> years = new ArrayList<Integer>();
        File[] yDirectories = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/"+ _type).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return dir.isDirectory();
            }
        }); 
        if (yDirectories.length > 0) {
        	years.add(Integer.parseInt(yDirectories[0].getName()));
        }        
        return years.toArray(new Integer[]{years.size()});
	}
	
	public Integer[] listMonthAsArray(String _type, int _year) {
		List<Integer> months = new ArrayList<Integer>();
		File base = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/"+ _type +"/"+ _year);
		if (base.isDirectory()) {			
			File[] files = base.listFiles();
			for (int i = 0; i < files.length; i++) {
				months.add(Integer.parseInt(files[i].getName().replace("_log.json", "")));
			}
		}
		return months.toArray(new Integer[]{months.size()});
	}
	
	public String readLogs(String _file) throws IOException {
		String content = "[]";
		File file = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/"+_file);
		if (file.exists()) {
			content = FileUtils.readFileToString(file, "UTF-8");
			if (!content.equals("")) {
				content = content.substring(1);
				content = "[" + content + "]";
			} else {
				content = "[]";
			}
		}		
		return content;
	}
	
	/**
	 * Check the Logs directory and create if it doesn't exist<br>.
	 * It ensure the following directories are exist<br>
	 * 		Ikea Logs/
	 * 		Ikea Logs/app/
	 * 		Ikea Logs/support/
	 * 		Ikea Logs/admin/
	 * 		Ikea Logs/system/
	 * 		Ikea Logs/exceptions/
	 * 		
	 * @return boolean
	 */
	private void checkBaseDirectories() {
        File dir = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/");
        if (!dir.exists()) {
        	/* Create root Log Folder */
        	dir.mkdir();
        }
        
        dir = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/app/");
        if (!dir.exists()) {
        	/* Create root log folder for App users */
        	dir.mkdir();
        }
        this.checkCurrentYearDirectory(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/app/");
        
        dir = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/support/");
        if (!dir.exists()) {
        	/* Create root log folder for Support users */
        	dir.mkdir();
        }
        this.checkCurrentYearDirectory(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/support/");
        
        dir = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/admin/");
        if (!dir.exists()) {
        	/* Create folder for Admin user */
        	dir.mkdir();
        }
        this.checkCurrentYearDirectory(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/admin/");
        
        dir = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/system/");
        if (!dir.exists()) {
        	/* Create folder for System user */
        	dir.mkdir();
        } 
        this.checkCurrentYearDirectory(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/system/");
        
        dir = new File(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/exception/");
        if (!dir.exists()) {
        	/* Create folder for Exception logs
        	 * Which will be logged by the Ikea Global Exception Trapper */
        	dir.mkdir();
        } 
        this.checkCurrentYearDirectory(this.bucketRoot + CENTRAL.FS_LOGGER_BUCKET + "/exception/");
	}
	
	private boolean checkCurrentYearDirectory(String _base) {
		return this.checkDirectoryExist(_base, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
	}
	
	private boolean checkDirectoryExist(String _base, String _dir) {
		/* Make sure the base path ends width "/" */
		if(!_base.trim().substring(_base.length() - 1).equals("/")) {
			_base = _base.trim() + "/";
		}
		
		File dir = new File(_base + _dir +"/");
		if (!dir.exists()) {
			/* Create folder for current year */
        	dir.mkdir();
		}
		return true;
	}
	
	private boolean checkRsDirectory(String _base, String _rs) {
		/* Make sure the base path ends width "/" */
		if(!_base.trim().substring(_base.length() - 1).equals("/")) {
			_base = _base.trim() + "/";
		}
		
		File dir = new File(_base + _rs +"/");
		if (!dir.exists()) {
			/* Create folder for current year */
        	dir.mkdir();
		}
		return true;
	}

	public void run() {		
		/* Infinite loop to make this thread as background */
		do {			
			//System.out.println("Checking the Logger Queue : " + Thread.currentThread().getId());			
			/* Check for Log queue */
			if (this.queue.size() > 0) { System.out.println("We have something to log");
				/* Well start to write the logs */
				this.write();
			}
			/* Time for rest */
			try {
				/* Sleep for 30 sec */
				Thread.sleep (this.tickerDelay * 1000);
	        } catch (InterruptedException interruptedException) {/* Just a catch, no need to handle it */}
		} while(!this.die);	
	}

}
