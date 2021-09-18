package com.hul.central.visualizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hul.central.system.model.CentralAppRequest;
import com.hul.central.visualizer.dashboard.DashboardFactoryService;
import com.hul.central.visualizer.report.ReportFactoryService;

@Controller(value = "reportFactoryController")
@RequestMapping("/visualizer")
public class VisualizerController {

	@Autowired 
	ReportFactoryService rfs;
	
	@Autowired
	DashboardFactoryService dfs;
	
	@ResponseBody
	@RequestMapping(value="/rf/get", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String getRFEntity(CentralAppRequest _car) throws Exception {				
		return this.rfs.getEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/rf/list", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String listRFEntity(CentralAppRequest _car) throws Exception {
		return this.rfs.listEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/rf/create", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String createRFEntity(CentralAppRequest _car) throws Exception {
		return this.rfs.createEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/rf/update", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String updateRFEntity(CentralAppRequest _car) throws Exception {
		return this.rfs.updateEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/rf/delete", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String deleteRFEntity(CentralAppRequest _car) throws Exception {
		return this.rfs.removeEntity(_car);
	
	}
	
	@ResponseBody
	@RequestMapping(value="/rf/compose", method=RequestMethod.GET)	
	public void previewRFEntity(HttpServletRequest _rq, HttpServletResponse _res) throws Exception {
		this.rfs.handleComposeRequest(_rq, _res);
	}
	
	@ResponseBody
	@RequestMapping(value="/df/get", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String getDFEntity(CentralAppRequest _car) throws Exception {				
		return this.dfs.getEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/df/list", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String listDFEntity(CentralAppRequest _car) throws Exception {
		return this.dfs.listEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/df/create", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String createDFEntity(CentralAppRequest _car) throws Exception {
		return this.dfs.createEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/df/update", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String updateDFEntity(CentralAppRequest _car) throws Exception {
		return this.dfs.updateEntity(_car);
	}
	
	@ResponseBody
	@RequestMapping(value="/df/delete", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)	
	public String deleteDFEntity(CentralAppRequest _car) throws Exception {
		return this.dfs.removeEntity(_car);
	}
	
}
