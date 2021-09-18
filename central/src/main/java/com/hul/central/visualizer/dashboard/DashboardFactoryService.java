package com.hul.central.visualizer.dashboard;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hul.central.system.model.CentralAppRequest;
import com.hul.central.system.model.CentralAppResponse;

/**
 * 
 * @author Sark
 * @since 1.0.0
 *
 */

@Service("DashboardFactoryService")
public class DashboardFactoryService {

	@Autowired
	CentralAppResponse ir;
	
	@Autowired
	DashboardFactoryDao dfd;
	
	@Autowired
	ApplicationContext context;
	
	public String getEntity(CentralAppRequest _crq) {
		return null;		
	}
	
	public String listEntity(CentralAppRequest _crq) throws Exception {	
		
		if(!_crq.getPayload().has("OPTIONS")){
			JSONObject options = new JSONObject();
			options.put("DURATION", 4);
			options.put("ORDERTYPE", "total");
			options.put("TRTYPE", 0);
			_crq.getPayload().put("OPTIONS",options);
		}
		
		JSONObject payload = _crq.getPayload();
		
		if (_crq.getTask().equals("BEAN")) {
			List<String> beanList = new ArrayList<String>();
			String[] beans = this.context.getBeanDefinitionNames();			
			for (String bean : beans) {
				if (!bean.contains("scopedTarget")) {
					//beanList.add(this.context.getBean(bean).getClass().toString());
					beanList.add(bean);
				}
			}			
			return ir.prepareResponse(true, "", beanList.toArray(new String[beanList.size()])).toString(); 
		} else if (_crq.getTask().equals("METHOD")) {
			List<String> methodList = new ArrayList<String>();
			if (!((String)payload.get("CLASS")).equals("")) {						
				Class<?> c = this.context.getBean((String)payload.get("CLASS")).getClass();
	            Method[] m = c.getMethods();  
	            for (int i = 0; i < m.length; i++) {
	            	methodList.add(m[i].getName());	            	      	
				}
			}	
			return ir.prepareResponse(true, "", methodList.toArray(new String[methodList.size()])).toString(); 
		} else if(_crq.getTask().equals("DASHBOARD")) {
			return this.ir.prepareResponse(true, "", this.prepareDashboardList(_crq)).toString();
		} else if(_crq.getTask().equals("WIDGET_GRP")) {
			return this.ir.prepareResponse(true, "", this.dfd.listWidgetGroup()).toString();
		} else if(_crq.getTask().equals("WIDGET_GRP_FOR_DASHBOARD")) {			
			return this.ir.prepareResponse(true, "", this.dfd.listWidgetGroup(((Long)payload.get("ID")).intValue())).toString();
		} else if(_crq.getTask().equals("WIDGET")) {
			return this.ir.prepareResponse(true, "", this.loadRawWidget(_crq, (String)payload.get("TYPE"))).toString();
		} else if(_crq.getTask().equals("WIDGET_FOR_GRP")) {
			return this.ir.prepareResponse(true, "", this.dfd.listWidget(((Long)payload.get("ID")).intValue())).toString();
		} 
		
		return ir.prepareResponse(false, "Unknown Task", null).toString();
		
	}
	
	private List<ArrayList<Object>> prepareDashboardList(CentralAppRequest _irq) throws Exception {
		Properties indexs = new Properties();
		List<ArrayList<Object>> dList = this.dfd.listDashboard();
		if (dList.size() > 1) {
			for (int i = 0; i < dList.get(0).size(); i++) {
				indexs.put(dList.get(0).get(i), i);
			}
			for (int i = 1; i < dList.size(); i++) {
				String Options = (String) dList.get(i).get((int) indexs.get("OPTIONS"));
				dList.get(i).set((int) indexs.get("OPTIONS"), Options);
			}
		}		
		return dList;
	}
	
	private Object loadRawWidget(CentralAppRequest _irq, String _lType) throws Exception {
		if (_lType.equals("FAT")) {
			String target = "";
			List<Properties> res = new ArrayList<Properties>();
			List<Properties> widgets = this.dfd.listWidgetFat();
			for (int i = 0; i < widgets.size(); i++) {
				target = (String) widgets.get(i).get("TARGET");
				//removing User level filter
				//if (target.contains(_irq.getUserBean().getUserId().toString())) {
					res.add(widgets.get(i));
				//}
			}
			return res;
		} else {			
			return this.dfd.listWidget();
		}		
	}
	
}
