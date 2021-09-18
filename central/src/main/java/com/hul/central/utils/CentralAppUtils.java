package com.hul.central.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.hul.central.base.CENTRAL;

/**
 * 
 * @author  Sark
 * @version 1.0
 *
 */

@Component
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
@PropertySource(value={CENTRAL.SYSTEM_PROPERTIES})
public class CentralAppUtils {

	@Value( "${central.system.recaptcha.url}" )
	private String url;
	
	@Value( "${central.system.recaptcha.secret}" )
	private String secret;
	
	@Value( "${central.system.recaptcha.agent}" )
	private String userAgent;

	public CentralAppUtils() {}
	
	public String generatePassword(int _length) {
		char[] allowedChars = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^*()-_=+[{]}|;,.?")).toCharArray();
		return RandomStringUtils.random( _length, 0, allowedChars.length-1, false, false, allowedChars, new SecureRandom() );
	}
	
	public String hashPassword(String _password) throws Exception {
		byte[] messageByte = new byte[0];		
		messageByte = _password.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		messageByte = md.digest(messageByte);		
		return new String(messageByte, "UTF-8");
	}
	
	public Properties normalizePayload( JSONObject payload ) throws JSONException {
		Properties res = new Properties();		
		for (int i = 0; i < payload.names().length(); i++){
			res.put(payload.names().getString(i),payload.get(payload.names().getString(i)));
		}		
		return res;
	}
	
	public String toJson(Object _payload) throws Exception {
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		ObjectWriter ow = om.writer();				
		return ow.writeValueAsString(_payload);
	}
	
	/**
	 * 
	 * Used to verify the google recaptcha response
	 * sent from the client
	 * @param grecaptcha
	 * @return Boolean
	 * @throws Exception 
	 * 
	 */
	public boolean verify(String grecaptcha) throws Exception {
		
		if (grecaptcha == null || "".equals(grecaptcha)) {
			return false;
		}
	
		URL host = new URL(this.url);
		HttpsURLConnection connection = (HttpsURLConnection) host.openConnection();

		/* Prepare request header */
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", this.userAgent);
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		/* Prepare query params */
		String postParams = "secret=" + this.secret + "&response=" + grecaptcha;

		/* Well initiate the request */
		connection.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes(postParams);
		out.flush();
		out.close();
		
		/* Prepare to read response */
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		/* Read all response */
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		/* Parse the JSON response and
		 * check if it has success value */
		JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		
		return jsonObject.getBoolean("success");
		
	}
	
	/**
	 * convert date to string
	 * 
	 * @param date
	 * @return String
	 */
	public static String getDateString(Date date) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.format(date);
	}
	public static String getDateStringYYYYMMDD(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		return df.format(date);
	}
	public static String getCurDateString(Date date) {
		DateFormat df = new SimpleDateFormat("ddMMyyyy");
		return df.format(date);
	}

	public static String getDateStr(Date date) {
		DateFormat df = new SimpleDateFormat("ddMMyy");
		return df.format(date);
	}

	public static String getDateStringTime(Date date) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:000");
		return df.format(date);
	}
	
	public static String getDateStrTime(Date date) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return df.format(date);
	}

	/**
	 * difference between two dates
	 * 
	 * @param d1,
	 *            d2
	 * @return int
	 */
	public static int daysBetween(Date d1, Date d2) {
		int days = (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
		return days + 1;
	}

	public static Date getStringDate(String date) throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return format.parse(date);
		} catch (ParseException e) {
			throw new Exception("Invalid date format");
		}
	}
	
	public static java.sql.Date getStringSqlDate(String date) throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			  Date parsed = format.parse(date);
	        java.sql.Date sql = new java.sql.Date(parsed.getTime());
			return sql;
		} catch (ParseException e) {
			throw new Exception("Invalid date format");
		}
	}

	public static Date getStringYearMonthDateFormat(String date) throws Exception {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(date);
		} catch (ParseException e) {
			throw new Exception("Invalid date format");
		}
	}

	public static String getDateStringFormat(Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	public static String getStringDt(Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("ddMMyyyy");
		return df.format(date);
	}

	public static String getDateStrinHrsgFormat(Date date) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00.000");
		return df.format(date);
	}

	public static Date getStringDateFormat(String date) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00.000");
		String pckPkdStr = df.format(date);
		Date dt = null;
		try {
			dt = df.parse(pckPkdStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}

	public static Date convertSqlDateFormat(Date sqlDt) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd 00:00:00.000");
		String pckPkdStr = sdf.format(sqlDt);
		Date dt = null;
		try {
			dt = sdf.parse(pckPkdStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}

	public static String convertDateFormat(Date dt) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd 00:00:00.000");
		return sdf.format(dt);
	}

	public static String convertStringDateFormat(Date dt) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(dt);
	}

	public static Date getPkdDtMonth(int month, int year) throws Exception, ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String date = 01 + "/" + month + "/" + year;
		Date convertedDate = dateFormat.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(convertedDate);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		/*
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);*/
		Date dt = cal.getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, -1);
		calendar.add(Calendar.DATE, 1);
		Date pkdDateOfMonth = calendar.getTime();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00.000");
		String pckPkdStr = sdf.format(pkdDateOfMonth);
		Date pckPkdOn = null;
		try {
			pckPkdOn = sdf.parse(pckPkdStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pckPkdOn;
	}

	public static Date getShelfInvoiceLifeDate(Date pckPkdOn, int shelfInvlife) throws Exception {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(pckPkdOn);
		c.add(Calendar.DATE, shelfInvlife);
		Date shelfLifeDt = c.getTime();
		String shelfLifeDtStr = sdf.format(shelfLifeDt);
		Date pckShelfInvTill = null;
		try {
			pckShelfInvTill = sdf.parse(shelfLifeDtStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pckShelfInvTill;
	}

	public static double decimalAmt1(double amt, int decimalCount) {
		double decimalAmt = 0.0f;
		if (decimalCount == 2) {
			decimalAmt = (double) (Math.round(amt * 100.0) / 100.0);
		} else if (decimalCount == 3) {
			decimalAmt = (double) (Math.round(amt * 1000.0) / 1000.0);
		} else if (decimalCount == 4) {
			decimalAmt = (double) (Math.round(amt * 10000.0) / 10000.0);
		} else if (decimalCount == 5) {
			decimalAmt = (double) (Math.round(amt * 100000.0) / 100000.0);
		} else if (decimalCount == 6) {
			decimalAmt = (double) (Math.round(amt * 1000000.0) / 1000000.0);
		} else if (decimalCount == 7) {
			decimalAmt = (double) (Math.round(amt * 10000000.0) / 10000000.0);
		}
		return decimalAmt;
	}
	public static double decimalAmt(double amt, int decimalCount) {
		return (double) ( Math.round( amt * Math.pow(10.0,decimalCount))/Math.pow(10.0,decimalCount) );
	}
	
	public static double decimalAmtSplit(double amt, int decimalCount) {
		String dAmount = String.valueOf(amt);
		String[] str = dAmount.split("\\.");
		if( str.length > 1 && str[1].length() > decimalCount ) {
			str[1] = str[1].substring( 0, decimalCount );
			dAmount = str[0] +"."+str[1];
		}
		double dcmAmt = Double.parseDouble(dAmount);
		return dcmAmt;
	}

	/*public static float decimalAmtFloat(float amt, int decimalCount) {
		float decimalAmt = 0.0f;
		
		if(decimalCount == 2) {
			decimalAmt = (float) (Math.round(amt * 100.0) / 100.0);
		} else if(decimalCount == 3) {
			decimalAmt = (float) (Math.round(amt * 1000.0) / 1000.0);
		} else if(decimalCount == 4) {
			decimalAmt = (float) (Math.round(amt * 10000.0) / 10000.0);
		} else if(decimalCount == 5) {
			decimalAmt = (float) (Math.round(amt * 100000.0) / 100000.0);
		} else if(decimalCount == 6) {
			decimalAmt = (float) (Math.round(amt * 1000000.0) / 1000000.0);
		} else if(decimalCount == 7) {
			decimalAmt = (float) (Math.round(amt * 10000000.0) / 10000000.0);
		}
		return decimalAmt;
	}*/
	public static Date getCurrentDate() {
		Date date = new Date();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00.000");
		String curDtStr = sdf.format(date);
		Date curDt = null;
		try {
			curDt = sdf.parse(curDtStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return curDt;
	}

	public static String getCurrentTime() {
		GregorianCalendar gcalendar = new GregorianCalendar();
		String currentTime = gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":" + gcalendar.get(Calendar.SECOND);
		return currentTime;
	}
	
}
