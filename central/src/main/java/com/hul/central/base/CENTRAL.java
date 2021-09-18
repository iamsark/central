package com.hul.central.base;

/**
 * 
 * Storage container for Global Constants 
 * 
 * @author 	Sark
 * @since	1.0.0
 *
 */

public class CENTRAL {

	public static final String SYSTEM_PROPERTIES = "classpath:system.properties";

	public static final String USER_CONTEXT_PATH = "/user";
	public static final String LOGIN_CONTEXT_PATH = "/login";
	public static final String SIGNIN_CONTEXT_PATH = "/signin";
	public static final String LOGOUT_CONTEXT_PATH = "/logout";
	
	public static final String APP_CONTEXT_PATH = "/app";
	public static final String ADMIN_CONTEXT_PATH = "/admin";
	public static final String SYSTEM_CONTEXT_PATH = "/system";
	public static final String MESSENGER_CONTEXT_PATH = "/messenger";
	public static final String WEBSOCKET_CONTEXT_PATH = "/socket";
	public static final String ASSET_CONTEXT_PATH = "/assets";
	public static final String MAINTENANCE_CONTEXT_PATH = "/maintenance";
	
	public static final String CRUD_GET_CONTEXT_PATH = "/get";
	public static final String CRUD_LIST_CONTEXT_PATH = "/list";
	public static final String CRUD_CREATE_CONTEXT_PATH = "/create";
	public static final String CRUD_UPDATE_CONTEXT_PATH = "/update";
	public static final String CRUD_DELETE_CONTEXT_PATH = "/delete";
	
	public static final String MESSENGER_SEND_CONTEXT_PATH = "/send";
	public static final String MESSENGER_RECEIVE_CONTEXT_PATH = "/receive";
	
	public static final String APP_SESSION = "LECA_USER_APP";
	public static final String ADMIN_SESSION = "LECA_USER_ADMIN";
	public static final String TERRITORY_SESSION = "LECA_USER_TERRITORY";
	public static final String AREA_SESSION = "LECA_USER_AREA";
	public static final String ZONE_SESSION = "LECA_USER_ZONE";
	public static final String CLUSTER_SESSION = "LECA_USER_CLUSTER";
	public static final String BRANCH_SESSION = "LECA_USER_BRANCH";
	public static final String COUNTRY_SESSION = "LECA_USER_COUNTRY";
	public static final String SYSTEM_SESSION = "LECA_USER_SYSTEM";	
	public static final String PRE_ACCESS_SESSION = "PRE_ACCESS";
	
	public static final String SEND_TO_SIGNIN_VIEW = "redirect:/";
	public static final String SEND_TO_APP_VIEW = "redirect:/app";
	public static final String SEND_TO_ADMIN_VIEW = "redirect:/admin";
	public static final String SEND_TO_SYSTEM_VIEW = "redirect:/system";
	
	public static final String APP_VIEW = "app";
	public static final String ADMIN_VIEW = "admin";	
	public static final String SYSTEM_VIEW = "system";
	public static final String MAINTENANCE_VIEW = "maintenance";	
	
	public static final String LOG_INFO = "info";
	public static final String LOG_ERROR = "error";
	public static final String LOG_WARNING = "warning";
	public static final String LOG_SUCCESS = "success";
	
	public static final String FS_LOGGER_BUCKET = "LoggerBucket";
	public static final String FS_QUERY_BUCKET = "QueryBucket";
	public static final String FS_USER_BUCKET = "UserBucket";
	
	public static final String SIGNIN_REQUIRED = "signin";
	public static final String SESSION_TIMED_OUT = "timeout";
	public static final String ACCESS_VIOLATION = "access";
	public static final String RS_IS_IN_MAINTENANCE = "maintenance";
	public static final String NO_SERVICE_FOUND = "No service found for this request";
	
	public static final String AUTH_MSG_KEY = "authentication_message";
	
	public static final String AUTH_MSG_INVALID_CREDENTIAL = "Invalid credential";
	public static final String AUTH_MSG_PARAM_MISSING = "Required parameter missing";
	public static final String AUTH_MSG_INVALID_CAPTCHA = "Invalid Captcha";
	
}
