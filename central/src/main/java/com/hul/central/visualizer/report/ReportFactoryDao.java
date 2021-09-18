package com.hul.central.visualizer.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hul.central.db.CentralAppDb;

/**
 * 
 * @Since 1.0.0
 * @author Saravana Kumar K
 * 
 */
@Service("ReportFactoryDao")
public class ReportFactoryDao {

	@Autowired
	CentralAppDb cdb;
	
	/**
	 * Entity Get Block
	 * @throws Exception 
	 **/
	
	public Properties getReport(int _report_id) throws Exception {
		return this.cdb.get("SELECT REPORT_ID, REPORT_NAME, IS_FROM_JASPER, BEAN, METHOD, OKEY, TARGET, CSV_EXPORT_AVAILABLE, XLS_EXPORT_AVAILABLE, TXT_EXPORT_AVAILABLE, PDF_EXPORT_AVAILABLE, STATUS FROM CENTRAL_RF_REPORTS WHERE REPORT_ID=?;", new Object[] {_report_id});
	}
	
	public Properties getTemplate(int _template_id) throws Exception {
		return this.cdb.get("SELECT TEMPLATE_ID, TEMPLATE_NAME, OPTIONS, IS_FACTORY_TEMPLATE, STATUS FROM CENTRAL_RF_REPORT_TEMPLATES WHERE TEMPLATE_ID=?;", new Object[] {_template_id});
	}
	
	public Properties getTemplateByReport(int _report_id) throws Exception {
		return this.cdb.get("SELECT TEMPLATE_ID, TEMPLATE_NAME, OPTIONS, IS_FACTORY_TEMPLATE, STATUS FROM CENTRAL_RF_REPORT_TEMPLATES WHERE REPORT_ID=?;", new Object[] {_report_id});
	}
	
	public Properties getField(int _field_id) throws Exception {
		return this.cdb.get("SELECT FIELD_ID, FIELD_KEY, FIELD_TYPE, IS_EDITABLE, IS_REMOVABLE, OPTIONS FROM CENTRAL_RF_REPORT_FIELDS WHERE FIELD_ID=?;", new Object[] {_field_id});
	}
	
	public List<Properties> listUser() throws Exception {
		return this.cdb.list("SELECT ID, EMAIL FROM CENTRAL_USERS;");
	}	
	
	/**
	 * Entity's List Block 
	 **/
	public List<ArrayList<Object>> listReports() throws Exception {
		return this.cdb.dietList("SELECT REPORT_ID, REPORT_NAME, IS_FROM_JASPER, BEAN, METHOD, OKEY, TARGET, CSV_EXPORT_AVAILABLE, XLS_EXPORT_AVAILABLE, TXT_EXPORT_AVAILABLE, PDF_EXPORT_AVAILABLE, STATUS FROM CENTRAL_RF_REPORTS;");
	} 
	
	public List<Properties> listReportsFat() throws Exception {
		return this.cdb.list("SELECT REPORT_ID, REPORT_NAME, IS_FROM_JASPER, BEAN, METHOD, OKEY, TARGET, CSV_EXPORT_AVAILABLE, XLS_EXPORT_AVAILABLE, TXT_EXPORT_AVAILABLE, PDF_EXPORT_AVAILABLE, STATUS FROM CENTRAL_RF_REPORTS;");
	} 
	
	public List<ArrayList<Object>> listReportTemplates(int _report_id) throws Exception {
		return this.cdb.dietList("SELECT TEMPLATE_ID, TEMPLATE_NAME, OPTIONS, IS_FACTORY_TEMPLATE, STATUS FROM CENTRAL_RF_REPORT_TEMPLATES WHERE REPORT_ID=?;", new Object[]{_report_id});
	}
	
	public List<Properties> listReportTemplatesFat(int _report_id) throws Exception {
		return this.cdb.list("SELECT TEMPLATE_ID, TEMPLATE_NAME, IS_FACTORY_TEMPLATE, STATUS FROM CENTRAL_RF_REPORT_TEMPLATES WHERE REPORT_ID=?;", new Object[]{_report_id});
	}
	
	public List<Properties> listReportLines(int _template_id, String _type) throws Exception {
		String table = "";
		if (_type.equals("header")) {
			table = "CENTRAL_RF_HEADER_LINES";
		} else if (_type.equals("footer")) {
			table = "CENTRAL_RF_FOOTER_LINES";
		} else {
			table = "CENTRAL_RF_REPORT_LINES";
		}
		return this.cdb.list("SELECT LINE_ID, OPTIONS FROM "+ table +" WHERE TEMPLATE_ID=?;", new Object[]{_template_id});
	} 
	
	public List<Properties> listReportFields(int _report_id) throws Exception {
		return this.cdb.list("SELECT FIELD_ID, FIELD_KEY, FIELD_TYPE, IS_EDITABLE, IS_REMOVABLE, OPTIONS FROM CENTRAL_RF_REPORT_FIELDS WHERE REPORT_ID=?;", new Object[]{_report_id});
	} 
	
	/**
	 * Entity's Insert Block 
	 **/
	public int addReport(String _name, String _bean, String _method, String _okey, String _target, boolean _csv, boolean _xls, boolean _txt, boolean _pdf) throws Exception {
		return this.cdb.insert("INSERT INTO CENTRAL_RF_REPORTS(REPORT_NAME, BEAN, METHOD, OKEY, TARGET, CSV_EXPORT_AVAILABLE, XLS_EXPORT_AVAILABLE, TXT_EXPORT_AVAILABLE, PDF_EXPORT_AVAILABLE) VALUES(?,?,?,?,?,?,?,?,?);", new Object[]{_name, _bean, _method, _okey, _target, _csv, _xls, _txt, _pdf});
	}
	
	public int addReportTemplate(String _template_name, int _report_id, String _options, boolean _is_factory_template) throws Exception {
		System.out.println("Report id : "+ _report_id);
		return this.cdb.insert("INSERT INTO CENTRAL_RF_REPORT_TEMPLATES(TEMPLATE_NAME, REPORT_ID, OPTIONS, IS_FACTORY_TEMPLATE) VALUES(?,?,?,?);", new Object[]{_template_name, _report_id, _options, _is_factory_template});
	}
	
	public int addReportLine(int _template_id, String _options, String _type) throws Exception {
		String table = "";
		if (_type.equals("header")) {
			table = "CENTRAL_RF_HEADER_LINES";
		} else if (_type.equals("footer")) {
			table = "CENTRAL_RF_FOOTER_LINES";
		} else {
			table = "CENTRAL_RF_REPORT_LINES";
		}
		return this.cdb.insert("INSERT INTO "+ table +"(TEMPLATE_ID, OPTIONS) VALUES(?,?);", new Object[]{_template_id, _options});
	}
	
	public int addReportField(String _field_key, String _type, int _report_id, boolean _is_editable, boolean _is_removable, String _options) throws Exception {
		return this.cdb.insert("INSERT INTO CENTRAL_RF_REPORT_FIELDS(FIELD_KEY, FIELD_TYPE, REPORT_ID, IS_EDITABLE, IS_REMOVABLE, OPTIONS) VALUES(?,?,?,?,?,?);", new Object[]{_field_key, _type, _report_id, _is_editable, _is_removable, _options});
	}
	
	/** 
	 * Entity's Update Block 
	 **/
	public int updateReport(String _name, String _bean, String _method, String _okey, String _target, boolean _csv, boolean _xls, boolean _txt, boolean _pdf, int _report_id) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_RF_REPORTS SET REPORT_NAME=?, BEAN=?, METHOD=?, OKEY=?, TARGET=?, CSV_EXPORT_AVAILABLE=?, XLS_EXPORT_AVAILABLE=?, TXT_EXPORT_AVAILABLE=?, PDF_EXPORT_AVAILABLE=? WHERE REPORT_ID=?;", new Object[]{_name, _bean, _method, _okey, _target, _csv, _xls, _txt, _pdf, _report_id});
	}
	
	public int toggleReportStatus(int _report_id, boolean _status) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_RF_REPORTS SET STATUS=? WHERE REPORT_ID=?;", new Object[]{_status, _report_id});
	}
	
	public int updateReportTemplate(String _template_name, String _options, boolean _is_factory_template, int _template_id) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_RF_REPORT_TEMPLATES SET TEMPLATE_NAME=?, OPTIONS=?, IS_FACTORY_TEMPLATE=? WHERE TEMPLATE_ID=?;", new Object[]{_template_name, _options, _is_factory_template, _template_id});
	}
	
	public int toggleReportTemplateStatus(int _template_id, boolean _status) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_RF_REPORT_TEMPLATES SET STATUS=? WHERE TEMPLATE_ID=?;", new Object[]{_status, _template_id});
	}
	
	public int updateReportLines(String _options, int _line_id) throws Exception {
		return this.cdb.update("UPDATE INTO CENTRAL_RF_REPORT_LINES SET OPTIONS=? WHERE LINE_ID=?;", new Object[]{_options, _line_id});
	}
	
	public int updateReportField(String _field_key, String _type, boolean _is_editable, boolean _is_removable, String _options, int _field_id) throws Exception {
		return this.cdb.update("UPDATE CENTRAL_RF_REPORT_FIELDS SET FIELD_KEY=?, FIELD_TYPE=?, IS_EDITABLE=?, IS_REMOVABLE=?, OPTIONS=? WHERE FIELD_ID=?;", new Object[]{_field_key, _type, _is_editable, _is_removable, _options, _field_id});
	}
	
	/**
	 * Entity's Remove Block 
	 **/
	public int removeReport(int _report_id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_RF_REPORTS WHERE REPORT_ID=?", new Object[]{_report_id});
	}
	
	public int removeReportTemplate(int _template_id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_RF_REPORT_TEMPLATES WHERE TEMPLATE_ID=?", new Object[]{_template_id});
	}
	
	public int removeReportTemplates(int _report_id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_RF_REPORT_TEMPLATES WHERE REPORT_ID=?", new Object[]{_report_id});
	}
	
	public int removeReportLine(int _line_id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_RF_REPORT_LINES WHERE LINE_ID=?", new Object[]{_line_id});
	}
	
	public int removeReportLines(int _template_id, String _type) throws Exception {
		String table = "";
		if (_type.equals("header")) {
			table = "CENTRAL_RF_HEADER_LINES";
		} else if (_type.equals("footer")) {
			table = "CENTRAL_RF_FOOTER_LINES";
		} else {
			table = "CENTRAL_RF_REPORT_LINES";
		}
		return this.cdb.delete("DELETE FROM "+ table +" WHERE TEMPLATE_ID=?", new Object[]{_template_id});
	}
	
	public int removeReportField(int _field_id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_RF_REPORT_FIELDS WHERE FIELD_ID=?", new Object[]{_field_id});
	}
	
	public int removeReportField(String _field_key) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_RF_REPORT_FIELDS WHERE FIELD_KEY=?", new Object[]{_field_key});
	}
	
	public int removeReportFields(int _report_id) throws Exception {
		return this.cdb.delete("DELETE FROM CENTRAL_RF_REPORT_FIELDS WHERE REPORT_ID=?", new Object[]{_report_id});
	}
	
}
