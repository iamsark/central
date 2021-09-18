package com.hul.central.visualizer.report.factory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.hul.central.visualizer.report.ReportFactoryDao;
import com.hul.central.visualizer.report.ReportFactoryService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

@SuppressWarnings("unused")
public class TestComposer extends ReportFactoryComposer {

	private String type = null;
	private String fkey = null;
	private JSONArray line = null;
	private Properties field = null;
	private JSONObject fmeta = null;

	private PdfWriter writer = null;
	private PdfDocument pdf = null;
	private Document document = null;

	private JSONArray headerLines = null;	
	private JSONArray footerLines = null;
	
	private PdfFont globalFont = null;
	private int globalFontSize = 9;
	
	private JSONObject pageHeaderField = null;
	private List<float[]> colWidths = null;
	private List<List<String>> headerRowsColumn = null;
	
	/* Sub report related properties */
	private Properties subReportLines = null;
	private Properties subreportFields = null;
	private Properties subReportTemplates = null;
		
	private ReportFactoryService rfs;
	
	public TestComposer(Properties _template, JSONArray _lines, JSONArray _hLines, JSONArray _fLines, Properties _fields) throws Exception {
		
		super(_template, _lines, _fields);
		this.headerLines = _hLines;
		this.footerLines = _fLines;
		
		this.subReportLines = new Properties();
		this.subreportFields = new Properties();
		this.subReportTemplates = new Properties();
		
	}

	public ByteArrayOutputStream compose(ReportFactoryService _rfs) throws Exception {
		
		this.rfs = _rfs;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.writer = new PdfWriter(baos);
		
		this.pdf = new PdfDocument(this.writer);
		this.setupPageSize();
		
		PdfViewerPreferences preferences = new PdfViewerPreferences();
		
	    preferences.setFitWindow(true);
	    preferences.setHideMenubar(true);
	    preferences.setHideToolbar(true);
	    preferences.setHideWindowUI(true);
	    preferences.setCenterWindow(true);
	    preferences.setDisplayDocTitle(true);
	    this.pdf.getCatalog().setViewerPreferences(preferences);	

		this.document = new Document(this.pdf);
		this.setupDocument();		

		this.collection = this.getData().getJSONArray("collection");
		
		if (this.collection == null) {
			this.document.add(new AreaBreak());
			
			PdfPage page = this.pdf.addNewPage();
			PdfCanvas pdfCanvas = new PdfCanvas(page);
			PageSize pageSize = TestComposer.this.pdf.getDefaultPageSize(); 
			Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);
			PdfFont font = PdfFontFactory.createFont(StandardFonts.COURIER);
			
			Text title = new Text("Looks like the Data Source not configured");			
			Paragraph p = new Paragraph().add(title);
			p.setTextAlignment(TextAlignment.CENTER);
			canvas.add(p);
			canvas.close();
						
			this.document.close();			
			return baos;
		}
		
		/* Get the header field */
		Properties header = this.getField("header");
		if (header != null) {
			this.usedFields.add((String) header.get("FIELD_KEY"));
			this.pageHeaderField = new JSONObject((String) header.get("OPTIONS"));			
		}
		
		this.pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderHandler());
		  
		Table rTable = null;
		Boolean isEmptyCell = false;
		int previousEndColumn = 0;
		ArrayList<Float> cols = null;
		ArrayList<Cell> cells = new ArrayList<Cell>();
		
		for (int i = 0; i < this.collection.length(); i++) {		
			
			/**
		     * Increment the collection index
		     */
			this.collectionIndex++;					  
			this.collect = this.collection.getJSONObject(i);
			
			/* Reset the used fields list */
			this.usedFields = new ArrayList<String>();
					
			/* Reset the page index */
			this.pageIndex = 0;
			
			/* Add new Page */
			if (i > 0) {
				this.document.add(new AreaBreak());
			}			
						
			/* Begin the line scanning */
			for (int j = 0; j < this.lines.length(); j++) {
								
				this.line = this.lines.getJSONArray(j);
				
				if (this.line.length() > 0) {
					
					isEmptyCell = false;
					previousEndColumn = 0;					
					cols = new ArrayList<Float>();
					cells = new ArrayList<Cell>();
				
					for (int x = 0; x < this.line.length(); x++) {
						if (!this.usedFields.contains(this.line.getString(x))) {
							
							this.field = (Properties) this.fields.get(this.line.getString(x));
							this.fkey = (String) this.field.get("FIELD_KEY");
							this.type = (String) this.field.get("FIELD_TYPE");						
							this.fmeta = new JSONObject((String) this.field.get("OPTIONS"));
							
							this.usedFields.add(this.line.getString(x));
							
							if (this.type.equals("record_table")) {
								/* This is a full width widget, 
								 * delegate the task to renderRecordTable handler */								
								if (this.collect.has(this.fmeta.getString("handle"))) {
									if (this.collect.get(this.fmeta.getString("handle")) instanceof JSONArray) {
										this.renderRecordTable(this.collect.getJSONArray(this.fmeta.getString("handle")));
									}									
								}								
								continue;
							} else if(this.type.equals("separator")) {
								/* This is a full width widget, 
								 * delegate the task to renderPageSeparator handler */
								this.renderPageSeparator();
								continue;
							} else if(this.type.equals("sub_report")) {
								/* This is a full width widget, 
								 * delegate the task to renderSubReport handler */
								this.renderSubReport();
								continue;
							} 
							
							/* Determine if placeholder cell needed */
							startColumn = this.fmeta.getInt("column_start");
								
							isEmptyCell = false;					
							/* Determine any empty cell on start of the line */
							if (previousEndColumn == 0) {
								
								if (startColumn > this.document.getLeftMargin()) {
									isEmptyCell = true;
									cols.add((float) Math.round(this.charWidth * (startColumn - this.document.getLeftMargin())));
								}							
														
							} else if (startColumn > previousEndColumn) {
								isEmptyCell = true;
								cols.add((float) Math.round(this.charWidth * (startColumn - previousEndColumn)));				
							}
							
							/* Create cell for placeholder */
							if (isEmptyCell) {							
								cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
							}
							
							if (this.type.equals("static_text")) {
								cells.add(this.renderStaticText());
								cols.add((float) Math.round(this.charWidth * this.fmeta.getInt("width")));
							} else if (this.type.equals("dynamic_text")) {
								cells.add(this.renderDynamicText(this.collect));
								cols.add((float) Math.round(this.charWidth * this.fmeta.getInt("width")));
							} else if (this.type.equals("page_number")) {
								cells.add(this.renderPageNumber());
								cols.add((float) Math.round(this.charWidth * this.fmeta.getInt("width")));
							} else {
								/* Ignore it */							
							}					
							
							previousEndColumn = this.fmeta.getInt("column_end");
							
						}
					}
					
					/* Check for header field, need to skip */
					if (this.line.length() == 1 && this.type == null) {
						continue;
					}
					if (this.line.length() == 1 && (this.type.equals("page_header") || this.type.equals("record_table") || this.type.equals("separator"))) {
						continue;
					}
					
					/* Check whether we need to add last empty placeholder cell */
					if (previousEndColumn < this.totalColumns) {					
						cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
						cols.add((float) Math.round(this.charWidth * (this.totalColumns - previousEndColumn)));					
					}				
					
					/* Create line table */
					int x = 0;
					float[] nCols = new float[cols.size()];					
					for (x = 0; x < cols.size(); x++) {
						nCols[x] = cols.get(x);
					}			
					
					rTable = new Table(UnitValue.createPercentArray(nCols));
					rTable.setWidth(UnitValue.createPercentValue(100));
					
					LinkedHashSet<Cell> cellSet = new LinkedHashSet<>(cells);
					for( Iterator<Cell> iter = cellSet.iterator(); iter.hasNext();) {					
						rTable.addCell(iter.next());
					}
					this.document.add(rTable);
					
				} else {
					/* Add an empty line */
					rTable = new Table(1);
					rTable.setWidth(UnitValue.createPercentValue(100));
					rTable.setFixedLayout();
					
					Cell cell = new Cell().add(new Paragraph(" ")); 
					cell.setBorder(Border.NO_BORDER);
					rTable.addCell(cell);
					this.document.add(rTable);
				}
				
			}			
			
		}		 

		this.document.close();
		
		return baos;
	}
	
	private void renderPageSeparator() {
		Table rTable =  new Table(1);
		rTable.setWidth(UnitValue.createPercentValue(100));			
		Cell cell = new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER); 
		cell.setBorderBottom(new DashedBorder(1));
		rTable.addCell(cell);
		this.document.add(rTable);
	}
	
	private Cell renderStaticText() throws Exception {		
		return this.renderTextElement(this.fmeta.getString("label"));	
	}
	
	private Cell renderDynamicText(JSONObject _collect) throws Exception {	
		
		Object cellData = _collect.get(this.fmeta.getString("handle"));
		
		if (cellData == null) {
			return new Cell();
		}
		
		if (!(cellData instanceof String)) {
			cellData = String.valueOf(cellData);
		}
		
		return this.renderTextElement((String) cellData);
		
	}
	
	private Cell renderPageNumber() throws Exception {		
		return this.renderTextElement("Page : "+ String.valueOf(this.pageIndex + 1));
	}
	
	private Cell renderTextElement(String _content) throws Exception {
		
		Cell cell = new Cell().add(new Paragraph(_content)).setBorder(Border.NO_BORDER);
		
		if (this.fmeta.getString("alignment").equals("left")) {
			cell.setTextAlignment(TextAlignment.LEFT);
		} else if (this.fmeta.getString("alignment").equals("right")) {
			cell.setTextAlignment(TextAlignment.RIGHT);
		} else if (this.fmeta.getString("alignment").equals("center")) {
			cell.setTextAlignment(TextAlignment.CENTER);
		} else {
			cell.setTextAlignment(TextAlignment.JUSTIFIED);
		}
		
		if (this.fmeta.getString("font_weight").equals("bold")) {
			cell.setBold();
		}		
		if (this.fmeta.getString("font_style").equals("italic")) {
			cell.setItalic();
		}		
		if (this.fmeta.getString("text_decoration").equals("underline")) {
			cell.setUnderline();
		}
		
		int padding_top = 0,
			padding_right = 0,
			padding_bottom = 0,
			padding_left = 0;
		
		if (this.fmeta.getBoolean("use_global_padding")) {
			padding_top = this.templateConfig.getInt("padding_top");
			padding_right = this.templateConfig.getInt("padding_right");
			padding_bottom = this.templateConfig.getInt("padding_bottom");
			padding_left = this.templateConfig.getInt("padding_left");
		} else {
			padding_top = this.fmeta.getInt("padding_top");
			padding_right = this.fmeta.getInt("padding_right");
			padding_bottom = this.fmeta.getInt("padding_bottom");
			padding_left = this.fmeta.getInt("padding_left");
		}
		
		cell.setPaddingTop(padding_top);
		cell.setPaddingRight(padding_right);
		cell.setPaddingBottom(padding_bottom);
		cell.setPaddingLeft(padding_left);
		
		return cell;
		
	}
	
	private void renderRecordTable(JSONArray _records) throws Exception {	
		
		this.document.add(this.renderRecordTableHeader());
		
		/* Now render table records */		
		Table rTable = null;
		JSONObject col = null;
		JSONObject cols = this.fmeta.getJSONObject("columns");		
		
		this.records = _records; 
		
		if (this.records == null) {
			return;
		}
		
		for (int i = 0; i < this.records.length(); i++) {
			
			this.currentRecord = this.records.getJSONObject(i);
			
			for (int j = 0; j < this.headerRowsColumn.size(); j++) {
				
				rTable = new Table(this.colWidths.get(j));
				rTable.setWidth(UnitValue.createPercentValue(100));
				rTable.setFixedLayout();
				for (int x = 0; x < this.headerRowsColumn.get(j).size(); x++) {
			
					col = cols.getJSONObject(this.headerRowsColumn.get(j).get(x));
					Cell cell = new Cell();
					
					if (col.getString("record_align").equals("left")) {
						cell.setTextAlignment(TextAlignment.LEFT);
					} else if (col.getString("record_align").equals("right")) {
						cell.setTextAlignment(TextAlignment.RIGHT);
					} else if (col.getString("record_align").equals("center")) {
						cell.setTextAlignment(TextAlignment.CENTER);
					} else {
						cell.setTextAlignment(TextAlignment.JUSTIFIED);
					}
					
					cell.add(new Paragraph((String) this.currentRecord.get(col.getString("handle"))));
					cell.setBorder(Border.NO_BORDER);
					rTable.addCell(cell);
					
				}
				
				this.document.add(rTable);
				
			}
			
		}
		
	}
	
	private IBlockElement renderRecordTableHeader() throws Exception {
		
		if (this.type == null || !this.type.equals("record_table")) {
			return null;
		}
	
		JSONObject col = null;
		JSONArray hLine = null;
		Div headerWrapper = new Div();		
		
		this.colWidths = new ArrayList<float[]>();
		
		JSONObject cols = this.fmeta.getJSONObject("columns");				
		this.headerRowsColumn = new ArrayList<List<String>>(); 
		
		/* Render table header */
		JSONArray headerLines = this.fmeta.getJSONArray("header");		
		
		/* Render table header rows */		
		for (int i = 0; i < headerLines.length(); i++) {
			
			/* Prepare row meta */			
			hLine = headerLines.getJSONArray(i);
			this.headerRowsColumn.add(new ArrayList<String>());
			this.colWidths.add(new float[hLine.length()]);		
						
			for (int j = 0; j < hLine.length(); j++) {
				col = cols.getJSONObject(hLine.getString(j));
				this.colWidths.get(i)[j] = ((float) Math.round(this.charWidth * col.getInt("width")));
				this.headerRowsColumn.get(i).add(hLine.getString(j));
			}
			
			/* Render header row */
			Table hrTable = new Table(UnitValue.createPercentArray(this.colWidths.get(i)));
			hrTable.setWidth(UnitValue.createPercentValue(100));
			//hrTable.setFixedLayout();
			for (int j = 0; j < hLine.length(); j++) {
				
				col = cols.getJSONObject(hLine.getString(j));
				Cell cell = new Cell();
				
				/* Format cell */
				if (col.getString("column_align").equals("left")) {
					cell.setTextAlignment(TextAlignment.LEFT);
				} else if (col.getString("column_align").equals("right")) {
					cell.setTextAlignment(TextAlignment.RIGHT);
				} else if (col.getString("column_align").equals("center")) {
					cell.setTextAlignment(TextAlignment.CENTER);
				} else {
					cell.setTextAlignment(TextAlignment.JUSTIFIED);
				}
				
				cell.add(new Paragraph(col.getString("label")));
				cell.setBorder(Border.NO_BORDER);
				hrTable.addCell(cell);
				
			}
			
			headerWrapper.add(hrTable);
			
		}
		
		/* Apply border to table header */
		headerWrapper.setBorderTop(new DashedBorder(1));
		headerWrapper.setBorderBottom(new DashedBorder(1));
		
		return headerWrapper;
		
	}
	
	private void renderSubReport() throws Exception {
				
		int startColumn = 0,
			previousEndColumn = 0;
		Table rTable = null;
		List<Float> cols = null;
		List<Cell> cells = null;
		Boolean isEmptyCell = false;
		
		Object report_id = this.fmeta.get("report");
		if (!(report_id instanceof Integer)) {
			report_id = Integer.valueOf((String) report_id);
		}
		
		/* Check whether we have the meta before hand */
		this.prepareSubReportBucket((int) report_id);
		
		/* Sub report collect */
		JSONObject subCollect = this.collect.getJSONObject(this.fmeta.getString("handle"));
			
		JSONArray sLine = null,
				sLines = (JSONArray) this.subReportLines.get(report_id);
		
		Properties sFields = (Properties) this.subreportFields.get(report_id); 
		
		/* Begin the line scanning */
		for (int j = 0; j < sLines.length(); j++) { 
							
			sLine = sLines.getJSONArray(j);
			
			if (sLine.length() > 0) {
				
				isEmptyCell = false;
				previousEndColumn = 0;					
				cols = new ArrayList<Float>();
				cells = new ArrayList<Cell>();
			
				for (int x = 0; x < sLine.length(); x++) {					
					if (!this.usedFields.contains(sLine.getString(x))) {
						
						this.field = (Properties) sFields.get(sLine.getString(x));
						this.fkey = (String) this.field.get("FIELD_KEY");
						this.type = (String) this.field.get("FIELD_TYPE");						
						this.fmeta = new JSONObject((String) this.field.get("OPTIONS"));
						
						this.usedFields.add(sLine.getString(x));
						
						if (this.type.equals("record_table")) {
							/* This is a full width widget, 
							 * delegate the task to renderRecordTable handler */
							this.renderRecordTable(subCollect.getJSONArray(this.fmeta.getString("handle")));
							continue;
						} else if(this.type.equals("separator")) {
							/* This is a full width widget, 
							 * delegate the task to renderPageSeparator handler */
							this.renderPageSeparator();
							continue;
						} 
						
						/* Determine if placeholder cell needed */
						startColumn = this.fmeta.getInt("column_start");
						
						isEmptyCell = false;					
						/* Determine any empty cell on start of the line */
						if (previousEndColumn == 0) {
							
							if (startColumn > this.document.getLeftMargin()) {
								isEmptyCell = true;
								cols.add((float) Math.round(this.charWidth * (startColumn - this.document.getLeftMargin())));
							}							
													
						} else if (startColumn > previousEndColumn) {
							isEmptyCell = true;
							cols.add((float) Math.round(this.charWidth * (startColumn - previousEndColumn)));				
						}
						
						/* Create cell for placeholder */
						if (isEmptyCell) {							
							cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
						}
						
						if(this.type.equals("static_text")) {
							cells.add(this.renderStaticText());
							cols.add((float) Math.round(this.charWidth * this.fmeta.getInt("width")));
						} else if(this.type.equals("dynamic_text")) {
							cells.add(this.renderDynamicText(subCollect));
							cols.add((float) Math.round(this.charWidth * this.fmeta.getInt("width")));
						} else {
							/* Ignore it */							
						}					
						
						previousEndColumn = this.fmeta.getInt("column_end");
						
					}
				}
				
				/* Check for header field, need to skip */
				if (sLine.length() == 1 && this.type == null) {
					continue;
				}
				if (sLine.length() == 1 && (this.type.equals("page_header") || this.type.equals("record_table") || this.type.equals("separator"))) {
					continue;
				}
				
				/* Check whether we need to add last empty placeholder cell */
				if (previousEndColumn < this.totalColumns) {					
					cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
					cols.add((float) Math.round(this.charWidth * (this.totalColumns - previousEndColumn)));					
				}				
				
				/* Create line table */
				int x = 0;
				float[] nCols = new float[cols.size()];					
				for (x = 0; x < cols.size(); x++) {
					nCols[x] = cols.get(x);
				}			
				
				rTable = new Table(UnitValue.createPercentArray(nCols));
				rTable.setWidth(UnitValue.createPercentValue(100));
				rTable.setFixedLayout();
				LinkedHashSet<Cell> cellSet = new LinkedHashSet<>(cells);
				for( Iterator<Cell> iter = cellSet.iterator(); iter.hasNext();) {					
					rTable.addCell(iter.next());
				}
				this.document.add(rTable);
				
			} else {
				/* Add an empty line */
				rTable = new Table(1);
				rTable.setWidth(UnitValue.createPercentValue(100));
				//rTable.setFixedLayout();
				Cell cell = new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER); 
				rTable.addCell(cell);
				this.document.add(rTable);
			}
		}
		
		/* Reset the type property so that the new page header don't get affected */
		this.type = null;
	}
	
	private void prepareSubReportBucket(int _report_id) throws Exception { 
		
		if (this.rfs == null) {
			this.rfs = new ReportFactoryService();
		}
		
		if (!this.subReportTemplates.containsKey(_report_id)) {			
			Properties template = this.rfs.getTemplateForReport((int) _report_id);
			this.subReportTemplates.put(_report_id, template);
		}
		if (!this.subreportFields.containsKey(_report_id)) {
			this.subreportFields.put(_report_id, this.rfs.getFields((int) _report_id));
		}
		if (!this.subReportLines.containsKey(_report_id)) {
			int template_id = (int) ((Properties) this.subReportTemplates.get(_report_id)).get("TEMPLATE_ID");
			JSONArray lArray = this.rfs.arrayListToJsonArray(this.rfs.getTemplateLines(template_id, "line"));
			this.subReportLines.put(_report_id, lArray);
		}
		
	}

	protected class HeaderHandler implements IEventHandler {

		PdfDocumentEvent docEvent = null;
		
		public HeaderHandler() {}		

		@Override
		public void handleEvent(Event arg) {

			this.docEvent = (PdfDocumentEvent) arg;		
			
			try {
				
				this.renderPageHeader();
				
				/* Increase page index */
				TestComposer.this.pageIndex++;
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} 
		
		@SuppressWarnings("resource")
		private void renderPageHeader() throws Exception {
			
			Table hTable = null;
			JSONArray line = null;
			String fieldType = null;		
			
			Properties field = null;		
			JSONObject fieldOption = null;		
			
			int height = 0;
			int startColumn = 0;
			int previousEndColumn = 0;
			Boolean isEmptyCell = false;
			
			Div header = new Div();
			ArrayList<Float> cols = null;			
			ArrayList<Cell> cells = new ArrayList<Cell>();
			
			JSONArray hFields = null;
			
			if (TestComposer.this.pageHeaderField == null) {				
				Properties pHeader = TestComposer.this.getField("page_header");
				if (pHeader == null) {
					/* Looks like no page header */
					return;
				}
				TestComposer.this.pageHeaderField = new JSONObject((String) pHeader.get("OPTIONS"));	
			}
			
			if (TestComposer.this.pageHeaderField != null) {
				hFields = TestComposer.this.pageHeaderField.getJSONArray("fields");		
				for (int i = 0; i < hFields.length(); i++) {				
					TestComposer.this.usedFields.remove(hFields.getString(i));				
				}		
			}
			
			for (int i = 0; i < TestComposer.this.headerLines.length(); i++) {
				
				previousEndColumn = 0;
				cols = new ArrayList<Float>();
				cells = new ArrayList<Cell>();
				line = TestComposer.this.headerLines.getJSONArray(i);
				
				if (line.length() > 0) {				
					
					for (int j = 0; j < line.length(); j++) {					
						
						if (TestComposer.this.fields.containsKey(line.getString(j)) && !TestComposer.this.usedFields.contains(line.getString(j))) {
							
							TestComposer.this.usedFields.add(line.getString(j));
							
							field = (Properties) TestComposer.this.fields.get(line.getString(j));
							fieldType = (String) field.get("FIELD_TYPE");
							fieldOption = new JSONObject((String) field.get("OPTIONS"));
							
							/* Skip if the repeat header option false */
							if (TestComposer.this.pageIndex > 0 && !fieldOption.getBoolean("repeat_field")) {
								continue;
							}
							
							startColumn = fieldOption.getInt("column_start");
							
							isEmptyCell = false;					
							/* Determine any empty cell on start of the line */
							if ((previousEndColumn == 0) && (startColumn > TestComposer.this.marginLeft)) {
								isEmptyCell = true;
								cols.add((float) Math.round(TestComposer.this.charWidth * (startColumn - previousEndColumn)));						
							} else if (startColumn > previousEndColumn) {
								isEmptyCell = true;
								cols.add((float) Math.round(TestComposer.this.charWidth * (startColumn - previousEndColumn)));						
							}
							
							/* Create cell for placeholder */
							if (isEmptyCell) {							
								cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
							}
							
							/* Create cell for the actual column with data */
							cols.add((float) Math.round(TestComposer.this.charWidth * fieldOption.getInt("width")));
							
							/* Get the data */
							Object cellData = "";
							
							if (fieldType.equals("static_text")) {
								cellData = fieldOption.getString("label");
							} else if (fieldType.equals("dynamic_text")) {
								if (TestComposer.this.collect.has(fieldOption.getString("handle"))) {
									cellData = TestComposer.this.collect.get(fieldOption.getString("handle"));
								}								
							} else if (fieldType.equals("page_number")) {
								cellData = "Page : "+ String.valueOf(TestComposer.this.pageIndex + 1);
							} else {
								/* Ignore it */
							}				
							
							if (!(cellData instanceof String)) {
								cellData = String.valueOf(cellData);
							}
							
							Cell cell = new Cell().add(new Paragraph((String) cellData)).setBorder(Border.NO_BORDER);
							
							if (fieldOption.getString("alignment").equals("left")) {
								cell.setTextAlignment(TextAlignment.LEFT);
							} else if (fieldOption.getString("alignment").equals("right")) {
								cell.setTextAlignment(TextAlignment.RIGHT);
							} else if (fieldOption.getString("alignment").equals("center")) {
								cell.setTextAlignment(TextAlignment.CENTER);
							} else {
								cell.setTextAlignment(TextAlignment.JUSTIFIED);
							}
							
							if (fieldOption.getString("font_weight").equals("bold")) {
								cell.setBold();
							}
							
							if (fieldOption.getString("font_style").equals("italic")) {
								cell.setItalic();
							}
							
							if (fieldOption.getString("text_decoration").equals("underline")) {
								cell.setUnderline();
							}
							
							cells.add(cell);
							
							/* Store it later use */					
							previousEndColumn = fieldOption.getInt("column_end");
						}				
						
					}	
					
					/* Check whether we need to add last empty placeholder cell */
					if (cells.size() > 0 && previousEndColumn < TestComposer.this.totalColumns) {					
						cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
						cols.add((float) Math.round(TestComposer.this.charWidth * (TestComposer.this.totalColumns - previousEndColumn)));					
					}				
					
					/* Create line table */
					if (cells.size() > 0) {
						int x = 0;
						float[] nCols = new float[cols.size()];
						
						for (x = 0; x < cols.size(); x++) {
							nCols[x] = cols.get(x);
						}			
						
						hTable = new Table(UnitValue.createPercentArray(nCols));
						hTable.setWidth(TestComposer.this.pageWidth - (TestComposer.this.marginLeft + TestComposer.this.marginRight));
						LinkedHashSet<Cell> cellSet = new LinkedHashSet<>(cells);
						for( Iterator<Cell> iter = cellSet.iterator(); iter.hasNext();) {					
							hTable.addCell(iter.next());
						}				
					
						/* Append the line table into the document */
						header.add(hTable);
					}			
					
				} else {
					/* Add an empty line */
					hTable = new Table(1);
					hTable.setWidth(TestComposer.this.pageWidth - (TestComposer.this.marginLeft + TestComposer.this.marginRight));			
					Cell cell = new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER); 
					hTable.addCell(cell);
					header.add(hTable).setBorder(Border.NO_BORDER);
				}
				
			}
			
			header.setFont(TestComposer.this.globalFont);
			header.setFontSize(TestComposer.this.globalFontSize);
			header.setCharacterSpacing((float) -0.5);
			
			//header.setMarginTop(TestComposer.this.document.getTopMargin());
			header.setMarginRight(TestComposer.this.document.getRightMargin());
			header.setMarginLeft(TestComposer.this.document.getLeftMargin());
			
            PdfPage page = this.docEvent.getPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), TestComposer.this.pdf);
            
            /* Check if the current field that is Record Table, then render the table header */            
            IBlockElement ib = TestComposer.this.renderRecordTableHeader();
            if (ib != null) {
            	header.add(ib);
            }
            
            PageSize pageSize = TestComposer.this.pdf.getDefaultPageSize();           
            height = (int) TestComposer.this.calculateHeight(header);
            Canvas canvas = new Canvas(pdfCanvas, TestComposer.this.pdf, pageSize);
            TestComposer.this.document.setTopMargin(height);           
            canvas.add(header);   
			            
		}

	}
	
	private void setupPageSize() throws Exception {

		PageSize ps = null; 
		
		switch (this.templateConfig.getString("paper")) {

			case "A0":
				ps = PageSize.A0;		
				this.pdf.setDefaultPageSize(PageSize.A0);
				break;
			case "A1":
				ps = PageSize.A1;
				this.pdf.setDefaultPageSize(PageSize.A1);
				break;
			case "A2":
				ps = PageSize.A2;
				this.pdf.setDefaultPageSize(PageSize.A2);
				break;
			case "A3":
				ps = PageSize.A3;
				this.pdf.setDefaultPageSize(PageSize.A3);
				break;
			case "A4":
				ps = PageSize.A4;
				this.pdf.setDefaultPageSize(PageSize.A4);
				break;
			case "A5":
				ps = PageSize.A5;
				this.pdf.setDefaultPageSize(PageSize.A5);
				break;
			case "A6":
				ps = PageSize.A6;
				this.pdf.setDefaultPageSize(PageSize.A6);
				break;
			case "A7":
				ps = PageSize.A7;
				this.pdf.setDefaultPageSize(PageSize.A7);
				break;
			case "A8":
				ps = PageSize.A8;
				this.pdf.setDefaultPageSize(PageSize.A8);
				break;
			case "A9":
				ps = PageSize.A9;
				this.pdf.setDefaultPageSize(PageSize.A9);
				break;
			case "A10":
				ps = PageSize.A10;
				this.pdf.setDefaultPageSize(PageSize.A10);
				break;
			case "Legal":
				ps = PageSize.LEGAL;
				this.pdf.setDefaultPageSize(PageSize.LEGAL);
				break;
			case "Letter":
				ps = PageSize.LETTER;
				this.pdf.setDefaultPageSize(PageSize.LETTER);
				break;
			case "Ledger":
				ps = PageSize.LEDGER;
				this.pdf.setDefaultPageSize(PageSize.LEDGER);
				break;
			default:
				ps = PageSize.A4;
				this.pdf.setDefaultPageSize(PageSize.A4);
		}

		this.pageWidth = ps.getWidth();
		this.pageHeight = ps.getHeight();		
		
		this.lineHeight = (float) Math.round(this.pageHeight / this.totalRows);
		this.charWidth = (float) Math.round(this.pageWidth / this.totalColumns);		
		this.lineLength = (int) (this.pageWidth - ((this.marginLeft * this.charWidth) + (this.marginRight * this.charWidth)));
		
	}

	private void setupDocument() throws Exception {

		/* Set page margins */
		this.document.setTopMargin(this.templateConfig.getInt("margin_top"));
		this.document.setRightMargin(this.templateConfig.getInt("margin_right"));
		this.document.setBottomMargin(this.templateConfig.getInt("margin_bottom"));
		this.document.setLeftMargin(this.templateConfig.getInt("margin_left"));

		PdfFont font = null;

		/* Set font family */
		switch (this.templateConfig.getString("font")) {

		case "courier":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.COURIER);
			break;
		case "courier-bold":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
			break;
		case "courier-italic":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.COURIER_OBLIQUE);
			break;
		case "courier-bold-italic":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.COURIER_BOLDOBLIQUE);
			break;
		case "helvetica":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			break;
		case "helvetica-bold":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			break;
		case "helvetica-italic":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
			break;
		case "helvetica-bold-italic":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
			break;
		case "roman":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
			break;
		case "roman-bold":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
			break;
		case "roman-italic":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
			break;
		case "roman-bold-italic":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLDITALIC);
			break;
		case "symbol":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.SYMBOL);
			break;
		case "zapfdingbats":
			this.globalFont = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
			break;
		default:
			this.globalFont = PdfFontFactory.createFont(StandardFonts.COURIER);

		}
		
		this.globalFontSize = this.templateConfig.getInt("font_size");

		this.document.setFont(this.globalFont);
		this.document.setFontColor(ColorConstants.BLACK);
		this.document.setFontSize(this.globalFontSize);
		this.document.setCharacterSpacing((float) -0.5);

	}

	private Properties getField(String _type) throws Exception {

		Properties field = null;
		Enumeration<?> e = this.fields.propertyNames();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			field = (Properties) this.fields.get(key);
			if (((String) field.get("FIELD_TYPE")).equals(_type)) {
				return field;				
			}
		}

		return null;
	}
	
	/**
	 * 
	 * @param _field
	 * @return float
	 * 
	 */
	private float calculateHeight(IBlockElement _field) {
		// Create renderer tree
		IRenderer renderer = _field.createRendererSubTree();
		// Do not forget setParent(). Set the dimensions of the viewport as needed
		LayoutResult result = renderer.setParent(document.getRenderer()).layout(new LayoutContext(new LayoutArea(1, new Rectangle(100, 1000))));
		return result.getOccupiedArea().getBBox().getHeight();
	}

}
