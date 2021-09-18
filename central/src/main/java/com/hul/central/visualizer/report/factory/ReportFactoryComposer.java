package com.hul.central.visualizer.report.factory;

import java.util.ArrayList;
import java.util.Properties;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * 
 * @Since 
 * @author Saravana Kumar K
 * @datetime 15-Dec-2019 -- 12:48:40 pm
 */

public abstract class ReportFactoryComposer {
	
	/**
	 * 
	 * Width of the selected page (points)
	 * 
	 */
	protected float pageWidth = 0;
	
	/**
	 * 
	 * Height of the selected page (points)
	 * 
	 */
	protected float pageHeight = 0;
	
	/**
	 * 
	 * Total rows in the selected page (Lines)
	 * 
	 */
	protected float totalRows = 66;
	
	/**
	 * 
	 * Total columns in the selected page (Chars)
	 * 
	 */
	protected float totalColumns = 96;
	
	/**
	 * 
	 * Line height of the selected template
	 * 
	 */
	protected float lineHeight = 0;
	
	/**
	 * 
	 * Char width of the selected template
	 * 
	 */
	protected float charWidth = 0;
	
	/**
	 * 
	 * Holds the configuration of template
	 * 
	 */
	protected JSONObject templateConfig = null;
	
	/**
	 * 
	 * Holds the current record of collection that is being rendered
	 * 
	 */
	protected JSONArray collection = null;
	
	/**
	 * 
	 * Holds the current record from the collection
	 * 
	 */
	protected JSONObject collect = null;	
	
	/**
	 * 
	 * Holds the current records array from current collect
	 * 
	 */
	protected JSONArray records = null;
	
	/**
	 * 
	 * Holds the current data grid record that is being rendered
	 * 
	 */
	protected JSONObject currentRecord = null;
	
	/**
	 * 
	 * Page Top Margin
	 * 
	 */
	protected float marginTop = 0;
	
	/**
	 * 
	 * Page Right Margin
	 * 
	 */
	protected float marginRight = 1;
	
	/**
	 * 
	 * Page Bottom Margin
	 * 
	 */
	protected float marginBottom = 0;
	
	/**
	 * 
	 * Page Left Margin
	 * 
	 */
	protected float marginLeft = 1;
	
	/**
	 * 
	 * Holds the keys of already rendered fields
	 * 
	 */
	protected ArrayList<String> usedFields = null;
	
	/**
	 * 
	 * Holds the current fields width that is being rendered
	 * 
	 */
	protected int fieldWidth = 0;
	
	/**
	 * 
	 * Holds the current field's start column property
	 *  
	 */
	protected int startColumn = 0;
	
	/**
	 * 
	 * Holds the calculated value of a single line width
	 * 
	 */
	protected int lineLength = 0;  

	/**
	 * 
	 * Holds the current line index that is being rendered
	 * 
	 */
	protected int lineIndex = 0;
	
	/**
	 * Represent the number of page that is being created at any moment
	 */
	protected int pageIndex = 0;
	
	/**
	 * Represent the collection index
	 */
	protected int collectionIndex = 0;
	
	/**
	 * 
	 * Holds the page offset top
	 * 
	 */
	protected int offsetTop = 0;
	
	/**
	 * 
	 * Holds the current template meta
	 * 
	 */
	protected Properties template = null;
	
	/**
	 * 
	 * Holds the report line list
	 * 
	 */
	protected JSONArray lines = null;	
	
	/**
	 * 
	 * Holds the list of fields meta object
	 * 
	 */
	protected Properties fields = null;
	
	/**
	 * 
	 * Holds the report data object
	 * 
	 */
	protected JSONObject data = null;

	/**
	 * 
	 * @Since
	 * @datetime 16-Dec-2019 -- 11:21:46 am
	 * @param _template
	 * @param _lines
	 * @param _fields
	 * @throws Exception 
	 * 
	 */
	public ReportFactoryComposer(Properties _template, JSONArray _lines, Properties _fields) throws Exception {
		
		this.template = _template;
		this.lines = _lines;
		this.fields = _fields;
		
		this.usedFields = new ArrayList<String>();		
		this.templateConfig = new JSONObject((String) this.template.get("OPTIONS"));
		
	}
	
	public void reset() {
		
		this.data = null;
		this.lines = null;
		this.fields = null;
		this.template = null;
		
		this.usedFields = null;		
		this.pageWidth = 0;
		this.pageHeight = 0;		
		this.lineHeight = 0;
		this.charWidth = 0;		
		this.lineLength = 0;
		this.marginTop = 0;
		this.marginRight = 1;
		this.marginBottom = 0;
		this.marginLeft = 1;		
		this.fieldWidth = 0;
		this.startColumn = 0;
		this.lineLength = 0;
		this.lineIndex = 0;
		this.offsetTop = 0;
		
	}
	
	/**
	 * @return the template
	 */
	public Properties getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(Properties template) {
		this.template = template;
	}

	/**
	 * @return the lines
	 */
	public JSONArray getLines() {
		return lines;
	}

	/**
	 * @param lines the lines to set
	 */
	public void setLines(JSONArray lines) {
		this.lines = lines;
	}

	/**
	 * @return the fields
	 */
	public Properties getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Properties fields) {
		this.fields = fields;
	}

	/**
	 * @return the data
	 */
	public JSONObject getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(JSONObject data) {
		this.data = data;
	}
	
}
