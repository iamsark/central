package com.hul.central.visualizer.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hul.central.system.model.CentralAppRequest;
import com.hul.central.system.model.CentralAppResponse;
import com.hul.central.visualizer.VisualizerController;
import com.hul.central.visualizer.report.factory.ReportFactoryDemoData;
import com.hul.central.visualizer.report.factory.TestComposer;

/**
 * 
 * @Since 1.0.0
 * @author Saravana Kumar K
 * 
 */
@Service("ReportFactoryService")
public class ReportFactoryService {

	@Autowired
	CentralAppResponse ir;
	
	@Autowired
	ReportFactoryDao rfd;
	
	@Autowired
	ApplicationContext context;
	
	public String getEntity(CentralAppRequest _crq) throws Exception {
		JSONObject payload = _crq.getPayload();		
		if (_crq.getTask().equals("REPORT_TEMPLATE")) {
			
			Properties meta = new Properties();
			meta.put("TEMPLATE", this.rfd.getTemplate(payload.getInt("TEMPLATE_ID")));
			meta.put("LINES", this.rfd.listReportLines(payload.getInt("TEMPLATE_ID"), "line"));			
			meta.put("HEADER_LINES", this.rfd.listReportLines(payload.getInt("TEMPLATE_ID"), "header"));
			meta.put("FOOTER_LINES", this.rfd.listReportLines(payload.getInt("TEMPLATE_ID"), "footer"));
			meta.put("FIELDS", this.getFields(payload.getInt("REPORT_ID")));			
			
			return this.ir.prepareResponse(true, "", meta).toString();
			
		}
		
		return ir.prepareResponse(false, "Unknown Task", null).toString();		
	}
	
	public JSONArray arrayListToJsonArray(List<Properties> _lines) throws Exception {
		JSONArray lArray = new JSONArray();
		for (int i = 0; i < _lines.size(); i++) {				
			lArray.put(new JSONArray((String)(_lines.get(i).get("OPTIONS"))));				
		}
		return lArray;
	}
	
	public Properties getTemplateForReport(int _report_id) throws Exception {
		return this.rfd.getTemplateByReport(_report_id);
	}
	
	public List<Properties> getTemplateLines(int _template_id, String _line_type) throws Exception {
		return this.rfd.listReportLines(_template_id, _line_type);
	}
	
	public Properties getFields(int _report_id) throws Exception {
		List<Properties> fields = this.rfd.listReportFields(_report_id);			
		String key;
		Properties field;
		Properties fieldsObj = new Properties();
		for (int i = 0; i < fields.size(); i++) {
			field = fields.get(i);
			key = (String)field.get("FIELD_KEY");
			field.remove("FIELD_KEY");
			fieldsObj.put(key, field);
		}		
		return fieldsObj;
	}
	
	public String listEntity(CentralAppRequest _crq) throws Exception {		
		JSONObject payload = _crq.getPayload();		
		if (_crq.getTask().equals("BEAN")) {
			List<String> beanList = new ArrayList<String>();
			String[] beans = this.context.getBeanDefinitionNames();			
			for (String bean : beans) {
				if (!bean.contains("scopedTarget")) {					
					beanList.add(bean);
				}
			}			
			return ir.prepareResponse(true, "", beanList.toArray(new String[beanList.size()])).toString();
		} else if (_crq.getTask().equals("METHOD")) {
			List<String> methodList = new ArrayList<String>();
			if (!((String)payload.get("CLASS")).equals("")) {						
				Class<?> c = this.context.getBean(payload.getString("CLASS")).getClass();
	            Method[] m = c.getMethods();  
	            for (int i = 0; i < m.length; i++) {
	            	methodList.add(m[i].getName());	            	      	
				}
			}	
			return ir.prepareResponse(true, "", methodList.toArray(new String[methodList.size()])).toString(); 
		} else if (_crq.getTask().equals("USER_LIST")) {
			return this.ir.prepareResponse(true, "", this.rfd.listUser()).toString();
		} else if (_crq.getTask().equals("REPORTS")) {
			return this.ir.prepareResponse(true, "", this.rfd.listReports()).toString();
		} else if (_crq.getTask().equals("REPORT_LIST")) {
			return this.ir.prepareResponse(true, "", this.rfd.listReportsFat()).toString();
		} else if (_crq.getTask().equals("REPORT_TEMPLATES")) {
			return this.ir.prepareResponse(true, "", this.rfd.listReportTemplatesFat(payload.getInt("REPORT_ID"))).toString();
		} else if (_crq.getTask().equals("REPORT_LINES")) {
			return this.ir.prepareResponse(true, "", this.rfd.listReportLines(payload.getInt("TEMPLATE_ID"), "line")).toString();
		} else if (_crq.getTask().equals("REPORT_FIELDS")) {
			return this.ir.prepareResponse(true, "", this.rfd.listReportFields(payload.getInt("REPORT_TYPE_ID"))).toString();
		}
		return ir.prepareResponse(false, "Unknown Task", null).toString();
	}
	
	public String createEntity(CentralAppRequest _crq) throws Exception {
		JSONObject payload = _crq.getPayload();
		if (_crq.getTask().equals("REPORTS")) {	
			int did = this.rfd.addReport(payload.getString("REPORT_NAME"), payload.getString("BEAN"), payload.getString("METHOD"), payload.getString("OKEY"), payload.getString("TARGET"), payload.getBoolean("CSV_EXPORT_AVAILABLE"), payload.getBoolean("XLS_EXPORT_AVAILABLE"), payload.getBoolean("TXT_EXPORT_AVAILABLE"), payload.getBoolean("PDF_EXPORT_AVAILABLE"));		
			/* Create report's default template */
			did = this.rfd.addReportTemplate("Factory Template", did, this.getDefaultTemplateOption(), true);
			return this.ir.prepareResponse(true, payload.getString("REPORT_NAME") + " created successfully.!", null).toString();
		} else if (_crq.getTask().equals("REPORT_TEMPLATES")) {
			return this.ir.prepareResponse(true, payload.getString("TEMPLATE_NAME") + " created successfully.!", this.rfd.addReportTemplate(payload.getString("TEMPLATE_NAME"), payload.getInt("REPORT_TYPE_ID"), payload.getJSONObject("OPTIONS").toString(), payload.getBoolean("IS_FACTORY_TEMPLATE"))).toString();
		} else if (_crq.getTask().equals("REPORT_LINES")) {
			return this.ir.prepareResponse(true, "Report lines stored successfully.!", this.rfd.addReportLine(payload.getInt("TEMPLATE_ID"), payload.getJSONObject("OPTIONS").toString(), "line")).toString();
		} else if (_crq.getTask().equals("REPORT_FIELD")) {
			return this.ir.prepareResponse(true, payload.getString("FIELD_KEY") + " created successfully.!", this.rfd.addReportField(payload.getString("FIELD_KEY"), payload.getString("TYPE"), payload.getInt("REPORT_TYPE_ID"), payload.getBoolean("IS_EDITABLE"), payload.getBoolean("IS_REMOVABLE"), payload.getJSONObject("OPTIONS").toString())).toString();
		}  else if (_crq.getTask().equals("REPORT_META")) {
			
		}
		return ir.prepareResponse(false, "Unknown Task", null).toString();
	}
	
	public String updateEntity(CentralAppRequest _crq) throws Exception { 
		JSONObject payload = _crq.getPayload();
		if (_crq.getTask().equals("REPORTS")) {			
			return this.ir.prepareResponse(true, payload.getString("REPORT_NAME") + " updated successfully.!", this.rfd.updateReport(payload.getString("REPORT_NAME"), payload.getString("BEAN"), payload.getString("METHOD"), payload.getString("OKEY"), payload.getString("TARGET"), payload.getBoolean("CSV_EXPORT_AVAILABLE"), payload.getBoolean("XLS_EXPORT_AVAILABLE"), payload.getBoolean("TXT_EXPORT_AVAILABLE"), payload.getBoolean("PDF_EXPORT_AVAILABLE"), payload.getInt("REPORT_ID"))).toString();
		} else if(_crq.getTask().equals("REPORT_STATUS")) {
			return this.ir.prepareResponse(true,  "", this.rfd.toggleReportStatus(payload.getInt("REPORT_ID"), payload.getBoolean("STATUS"))).toString();
		} else if (_crq.getTask().equals("REPORT_TEMPLATES")) {			
			JSONObject fields = payload.getJSONObject("FIELDS");			
			/* Insert or Update report fields */
			for(Iterator<?> iterator = fields.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				JSONObject field = fields.getJSONObject(key);
				if (field.has("FIELD_ID")) {
					this.rfd.updateReportField(key, field.getString("FIELD_TYPE"), field.getBoolean("IS_EDITABLE"), field.getBoolean("IS_REMOVABLE"), field.getJSONObject("OPTIONS").toString(), field.getInt("FIELD_ID"));
				} else {
					this.rfd.addReportField(key, field.getString("FIELD_TYPE"), payload.getInt("REPORT_ID"), field.getBoolean("IS_EDITABLE"), field.getBoolean("IS_REMOVABLE"), field.getJSONObject("OPTIONS").toString());
				}
			}	
			
			this.updateReportLines(payload.getJSONArray("LINES"), payload.getInt("TEMPLATE_ID"), "line");
			this.updateReportLines(payload.getJSONArray("HEADER_LINES"), payload.getInt("TEMPLATE_ID"), "header");
			this.updateReportLines(payload.getJSONArray("HEADER_LINES"), payload.getInt("TEMPLATE_ID"), "footer");			
			
			return this.ir.prepareResponse(true, payload.getString("TEMPLATE_NAME") + " updated successfully.!", this.rfd.updateReportTemplate(payload.getString("TEMPLATE_NAME"), payload.getJSONObject("OPTIONS").toString(), payload.getBoolean("IS_FACTORY_TEMPLATE"), payload.getInt("TEMPLATE_ID"))).toString();
		} else if (_crq.getTask().equals("REPORT_LINES")) {
			return this.ir.prepareResponse(true, "Report lines updated successfully.!", this.rfd.updateReportLines(payload.getJSONObject("OPTIONS").toString(), payload.getInt("LINE_ID"))).toString();
		} else if (_crq.getTask().equals("REPORT_FIELD")) {
			return this.ir.prepareResponse(true, payload.getString("REPORT_NAME") + " created successfully.!", this.rfd.updateReportField(payload.getString("FIELD_KEY"), payload.getString("FIELD_TYPE"), payload.getBoolean("IS_EDITABLE"), payload.getBoolean("IS_REMOVABLE"), payload.getJSONObject("OPTIONS").toString(), payload.getInt("FIELD_ID"))).toString();
		}
		return ir.prepareResponse(false, "Unknown Task", null).toString();
	}
	
	public String removeEntity(CentralAppRequest _crq) throws Exception {
		JSONObject payload = _crq.getPayload();
		List<Properties> templates = null;
		if (_crq.getTask().equals("REPORTS")) {			
			templates = this.rfd.listReportTemplatesFat(payload.getInt("REPORT_ID"));
			for (Properties template : templates) {
				/* Remove report lines */
				this.rfd.removeReportLines((int)template.get("TEMPLATE_ID"), "line");
				this.rfd.removeReportLines((int)template.get("TEMPLATE_ID"), "header");
				this.rfd.removeReportLines((int)template.get("TEMPLATE_ID"), "footer");
			}
			/* Remove report template */
			this.rfd.removeReportTemplates(payload.getInt("REPORT_ID"));
			/* Remove report fields */
			this.rfd.removeReportFields(payload.getInt("REPORT_ID"));
			/* Finally remove the report itself */
			return ir.prepareResponse(true, payload.getString("REPORT_NAME") +" removed successfully.!", this.rfd.removeReport(payload.getInt("REPORT_ID"))).toString();
		} else if (_crq.getTask().equals("REPORT_TEMPLATES")) {
			/* Remove the report lines belongs to this template */
			this.rfd.removeReportLines(payload.getInt("TEMPLATE_ID"), "line");
			/* Remove the template itself */			
			return ir.prepareResponse(true, payload.getString("TEMPLATE_NAME") +" removed successfully.!", this.rfd.removeReportTemplate(payload.getInt("TEMPLATE_ID"))).toString();
		} else if (_crq.getTask().equals("REPORT_LINES")) {
			return ir.prepareResponse(true, "Report lines removed successfully", this.rfd.removeReportLine(payload.getInt("LINE_ID"))).toString(); 
		} else if (_crq.getTask().equals("REPORT_FIELD")) {
			/* Before removing, if the type is Page Header or Footer then remove the child fields as well */
			Properties prop = this.rfd.getField(payload.getInt("FIELD_ID"));
			JSONObject field = new JSONObject((String)prop.get("OPTIONS"));
			if (prop.get("FIELD_TYPE").equals("page_header") || prop.get("FIELD_TYPE").equals("page_footer")) {
				JSONArray fields = field.getJSONArray("fields");				
				for (int i = 0; i < fields.length(); i++) {					
					this.rfd.removeReportField(fields.getString(i));
				}			
			}			
			return ir.prepareResponse(true, "Report field removed successfully", this.rfd.removeReportField(payload.getInt("FIELD_ID"))).toString();
		}
		return ir.prepareResponse(false, "Unknown Task", null).toString();
	}

	
	public void handleComposeRequest(HttpServletRequest _rq, HttpServletResponse _res) throws Exception {
		
		final File f = new File(VisualizerController.class.getProtectionDomain().getCodeSource().getLocation().getPath());		
		CentralAppRequest _irq = new CentralAppRequest();
		_irq.setAction("GET");
		_irq.setEntity("report");
		_irq.setTask("PREVIEW");		
		
	   JSONObject payload = new JSONObject();	   
	   payload.put("TEMPLATE_ID", ""+_rq.getParameter("TEMPLATE_ID"));
	   payload.put("REPORT_ID", ""+_rq.getParameter("REPORT_ID"));   
	   _irq.setPayload(payload);
	   
	   Properties report = this.rfd.getReport(Integer.parseInt((String) payload.get("REPORT_ID")));
	   Properties fields = this.getFields(Integer.parseInt((String) payload.get("REPORT_ID")));					
		
		List<Properties> lines = this.rfd.listReportLines(Integer.parseInt((String)payload.get("TEMPLATE_ID")), "line");
		List<Properties> hLines = this.rfd.listReportLines(Integer.parseInt((String)payload.get("TEMPLATE_ID")), "header");
		List<Properties> fLines = this.rfd.listReportLines(Integer.parseInt((String)payload.get("TEMPLATE_ID")), "footer");
		
		Properties template = this.rfd.getTemplate(Integer.parseInt((String)payload.get("TEMPLATE_ID")));			
		
		JSONArray lArray = this.arrayListToJsonArray(lines);
		JSONArray hlArray = this.arrayListToJsonArray(hLines);
		JSONArray flArray = this.arrayListToJsonArray(fLines);
		
		TestComposer rfPdf = new TestComposer(template, lArray, hlArray, flArray, fields);	
		
		String className = (String) report.get("BEAN");
		String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
		
		String methodName = (String) report.get("METHOD");
		Method method = this.context.getBean(className).getClass().getDeclaredMethod(methodName);
		
		rfPdf.setData((JSONObject) method.invoke(this.context.getBean(className)));		
		
		ByteArrayOutputStream baos = rfPdf.compose(this);
	   	   
	   _res.setHeader("Expires", "0");
	   _res.setHeader("Pragma", "public");
	   _res.setHeader("Access-Control-Allow-Origin", "*");
	   _res.setHeader("Access-Control-Allow-Headers", "Content-Type");	   
	   _res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT");
	   _res.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");	   
	   
	   _res.setContentType("application/pdf");
	   if (_rq.getParameter("TYPE").equals("ATTACHMENT")) {
	   	_res.setHeader("Content-disposition", "attachment; filename=\"" + ((String) report.get("REPORT_NAME")) +".pdf\"" );
	   } else {
	   	_res.setHeader("Content-disposition", "filename=\"" + ((String) report.get("REPORT_NAME")) +".pdf\"" );
	   }   
	   	   
	   _res.setContentLength(baos.size());
	      
	   OutputStream out = _res.getOutputStream();	   
	   baos.writeTo(out);
	   
	   out.flush();
	   out.close();
	   
	}
	
	private void updateReportLines(JSONArray lines, int _template_id, String _type) throws Exception {
		/* Flush out report lines */
		this.rfd.removeReportLines(_template_id, _type);
		for (int i = 0; i < lines.length(); i++) {
			this.rfd.addReportLine(_template_id, lines.getJSONArray(i).toString(), _type);
		}
	}
	
	private String getDefaultTemplateOption() throws Exception {
		
		JSONObject options = new JSONObject();
		
		options.put("paper", "A4");
		options.put("format", "text");
		options.put("printer_type", "dmp");		
		options.put("printer_cpi", 12);
		options.put("printer_lpi", 6);
		options.put("orientation", "portrait");		
		options.put("font", "roman");
		options.put("font_size", 9);
		options.put("char_space", 1);			
		options.put("margin_top", 1);
		options.put("margin_right", 1);
		options.put("margin_bottom", 1);
		options.put("margin_right", 1);		
		options.put("padding_top", 0);
		options.put("padding_right", 0);
		options.put("padding_bottom", 0);
		options.put("padding_left", 0);		
		options.put("title_page", false);
		options.put("title", false);
		options.put("subtitle", false);
		options.put("version", "");
		
		return options.toString();
		
	}
	
}
