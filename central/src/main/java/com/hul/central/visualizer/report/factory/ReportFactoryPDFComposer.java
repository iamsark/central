package com.hul.central.visualizer.report.factory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.hul.central.visualizer.report.factory.TestComposer.HeaderHandler;
import com.itextpdf.io.font.FontConstants;
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
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * 
 * @Since 
 * @author Saravana Kumar K
 * @datetime 15-Dec-2019 -- 11:39:18 pm
 */

@SuppressWarnings("deprecation")
public class ReportFactoryPDFComposer extends ReportFactoryComposer {

	private String type = null;
	private String fkey = null;
	private JSONArray line = null;
	private Properties field = null;
	private JSONObject fmeta = null;
	private boolean isEmptyLine = true;

	private PdfWriter writer = null;
	private PdfDocument pdf = null;
	private Document document = null;

	private JSONArray headerLines = null;
	private JSONArray footerLines = null;
	
	public ReportFactoryPDFComposer(Properties _template, JSONArray _lines, JSONArray _hLines, JSONArray _fLines,  Properties _fields) throws Exception {
		super(_template, _lines, _fields);
		this.headerLines = _hLines;
		this.footerLines = _fLines;
	}
	
	public ByteArrayOutputStream compose() throws Exception {		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.writer = new PdfWriter(baos);

		this.pdf = new PdfDocument(this.writer);
		this.setupPageSize();

		this.document = new Document(this.pdf);
		this.setupDocument();

		this.pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new PageEventHandler());		
		
		this.collection = this.getData().getJSONArray("collection");
		  
		for (int i = 0; i < this.collection.length(); i++) {
			
			/**
		     * Increment the collection index
		     */
			this.collectionIndex++;					  
			this.collect = this.collection.getJSONObject(i);
			
			this.checkPagePostion(true);
			
			/* Begin the line scanning */
			for (int j = this.headerLines.length(); j < this.lines.length(); j++) {
				
			}
			
		}	
		
		System.out.println("PDF Created.!");
		this.document.close();		
		return baos;		
		
	}
	
	private boolean checkPagePostion(Boolean _force) throws Exception {
		
		if (!_force) {
			float position = this.document.getRenderer().getCurrentArea().getBBox().getTop();		
			if (position < (this.pageHeight - this.document.getTopMargin())) {
				return false;
			}
		}				
		
		/* Add page */
		this.pageIndex++;
		this.pdf.addNewPage();	
		
		/* Render the Header */
		this.renderPageHeader();
		
		return true;
		
	}
	
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
		
		for (int i = 0; i < this.headerLines.length(); i++) {
			
			previousEndColumn = 0;
			cols = new ArrayList<Float>();
			cells = new ArrayList<Cell>();
			line = this.headerLines.getJSONArray(i);
			
			if (line.length() > 0) {				
				
				for (int j = 0; j < line.length(); j++) {
					
					if (this.fields.containsKey(line.getString(j))) {
						field = (Properties) this.fields.get(line.getString(j));
						fieldType = (String) field.get("FIELD_TYPE");
						fieldOption = new JSONObject((String) field.get("OPTIONS"));				
						
						startColumn = fieldOption.getInt("column_start");
						
						isEmptyCell = false;					
						/* Determine any empty cell on start of the line */
						if ((previousEndColumn == 0) && (startColumn > this.marginLeft)) {
							isEmptyCell = true;
							cols.add((float) Math.round(this.charWidth * (startColumn - previousEndColumn)));						
						} else if (startColumn > previousEndColumn) {
							isEmptyCell = true;
							cols.add((float) Math.round(this.charWidth * (startColumn - previousEndColumn)));						
						}
						
						/* Create cell for placeholder */
						if (isEmptyCell) {							
							cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
						}
						
						/* Create cell for the actual column with data */
						cols.add((float) Math.round(this.charWidth * fieldOption.getInt("width")));
						
						/* Get the data */
						Object cellData = null;
						
						if (fieldType.equals("static_text")) {
							cellData = fieldOption.getString("label");
						} else if (fieldType.equals("dynamic_text")) {
							cellData = this.collect.get(fieldOption.getString("handle"));
						} else {
							/* Ignore it */
						}				
						
						if (!(cellData instanceof String)) {
							cellData = String.valueOf(cellData);
						}
						
						Cell cell = new Cell().add(new Paragraph((String) cellData)).setBorder(Border.NO_BORDER); 
						cells.add(cell);
						
						/* Store it later use */					
						previousEndColumn = fieldOption.getInt("column_end");
					}				
					
				}	
				
				/* Check whether we need to add last empty placeholder cell */
				if (previousEndColumn < this.totalColumns) {					
					cells.add(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
					cols.add((float) Math.round(this.charWidth * (totalColumns - previousEndColumn)));					
				}				
				
				/* Create line table */
				int x = 0;
				float[] nCols = new float[cols.size()];
				
				for (x = 0; x < cols.size(); x++) {
					nCols[x] = cols.get(x);
				}			
				
				hTable = new Table(UnitValue.createPercentArray(nCols));
				hTable.setWidth(this.pageWidth - (this.marginLeft + this.marginRight));
				LinkedHashSet<Cell> cellSet = new LinkedHashSet<>(cells);
				for( Iterator<Cell> iter = cellSet.iterator(); iter.hasNext();) {					
					hTable.addCell(iter.next());
				}
				
				/* Append the line table into the document */
				header.add(hTable);
				
			} else {
				/* Add an empty line */
				hTable = new Table(1);
				hTable.setWidth(UnitValue.createPercentValue(100));
				hTable.addCell(new Paragraph("\n")).setBorder(Border.NO_BORDER);
				header.add(hTable);
			}
			
			height++;
		}
		
		this.document.add(header);
		
	}
	
	private void renderStaticText() throws Exception {
		this.document.add(this.getWrapper().add(this.getParagraph((String) this.fmeta.get("label"), this.fmeta, false, false)));		
	}
	
	private void renderDynamicText() throws Exception {
		String text = "";
		//this.temp = this.parser.parse((String) );
		JSONObject labelMeta = (JSONObject) this.fmeta.get("label_style");
		//this.temp = this.parser.parse((String) this.fmeta.get("label_style"));
		JSONObject valueMeta = (JSONObject) this.fmeta.get("value_style");

		this.fieldWidth = labelMeta.getInt("width");
		this.document.add(this.getWrapper().add(this.getParagraph((String) fmeta.get("label"), labelMeta, false, false)));
		
		this.fieldWidth = 1;
		this.startColumn = this.startColumn + labelMeta.getInt("width");
		this.document.add(this.getWrapper().add(this.getParagraph((String) fmeta.get("delimiter"), this.fmeta, false, false)));
		
		this.fieldWidth = valueMeta.getInt("width");
		this.startColumn = this.startColumn + 1;
		if (this.data.has(fmeta.getString("handle"))) {
			text = this.data.getString(fmeta.getString("handle"));
		}
		this.document.add(this.getWrapper().add(this.getParagraph(text, valueMeta, false, false)));
	}

	private void renderPageNumber() {
		
	}
	
	private void renderPageBreak() {
		this.document.add(new AreaBreak());
	}
	
	private void renderRecordTable() throws Exception {
		
		
	}
	
	
	private Div getWrapper() {
		return new Div().setFixedPosition(((this.startColumn - this.marginLeft) * this.charWidth), this.pageHeight - ((this.lineIndex+1) * this.lineHeight) - this.offsetTop, (this.fieldWidth * this.charWidth)).setHeight(this.lineHeight).setMargin(0).setPadding(0);
	}
	
	/**
	 *	 
	 * @param _text
	 * @param _meta
	 * @param _is_grid
	 * @param _is_record
	 * @return Paragraph
	 * @throws Exception
	 * 
	 */
	private Paragraph getParagraph(String _text, JSONObject _meta, boolean _is_grid, boolean _is_record) throws Exception {
		Paragraph p = new Paragraph(_text);
		if (_meta.has("alignment") || _meta.has("record_align") || _meta.has("column_align")) {
			String align = "left";
			if (_meta.has("alignment")) {
				align = (String) _meta.get("alignment");	
			}			
			if (_is_grid) {
				if (_meta.has("record_align")) {
					align = (String) _meta.get("record_align");
				} else {
					align = (String) _meta.get("column_align");
				}
			}			
			if (align.equals("left")) {
				p.setTextAlignment(TextAlignment.LEFT);
			} else if (align.equals("right")) {
				p.setTextAlignment(TextAlignment.RIGHT);
			} else if (align.equals("center")) {
				p.setTextAlignment(TextAlignment.CENTER);
			} else if (align.equals("justify")) {
				p.setTextAlignment(TextAlignment.JUSTIFIED);
			}
		} 
		if (_meta.has("font_style")) {
			//PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER_OBLIQUE);
			if (((String)_meta.get("font_style")).equals("italic")) {
				p.setItalic();
			}
		}
		if (_meta.has("font_weight")) {
			PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER_BOLD);
			if (((String)_meta.get("font_weight")).equals("bold")) {
				p.setFont(font).setFontColor(ColorConstants.BLACK).setFontSize(9);;
			}
		}
		if (_meta.has("text_decoration")) {
			if (((String)_meta.get("text_decoration")).equals("underline")) {
				p.setUnderline();
			}
		}
		return p.setPadding(0).setMargin(0);
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
		
		System.out.println("Page WIdth : "+ this.pageWidth);
		
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
			font = PdfFontFactory.createFont(StandardFonts.COURIER);
			break;
		case "courier-bold":
			font = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
			break;
		case "courier-italic":
			font = PdfFontFactory.createFont(StandardFonts.COURIER_OBLIQUE);
			break;
		case "courier-bold-italic":
			font = PdfFontFactory.createFont(StandardFonts.COURIER_BOLDOBLIQUE);
			break;
		case "helvetica":
			font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			break;
		case "helvetica-bold":
			font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			break;
		case "helvetica-italic":
			font = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
			break;
		case "helvetica-bold-italic":
			font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
			break;
		case "roman":
			font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
			break;
		case "roman-bold":
			font = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
			break;
		case "roman-italic":
			font = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
			break;
		case "roman-bold-italic":
			font = PdfFontFactory.createFont(StandardFonts.TIMES_BOLDITALIC);
			break;
		case "symbol":
			font = PdfFontFactory.createFont(StandardFonts.SYMBOL);
			break;
		case "zapfdingbats":
			font = PdfFontFactory.createFont(StandardFonts.ZAPFDINGBATS);
			break;
		default:
			font = PdfFontFactory.createFont(StandardFonts.COURIER);

		}

		this.document.setFont(font);
		this.document.setFontColor(ColorConstants.BLACK);
		this.document.setFontSize(this.templateConfig.getInt("font_size"));
		this.document.setCharacterSpacing((float) -0.5);

	}

	private JSONObject getFieldMeta(String _type) throws Exception {

		Properties field = null;
		Enumeration<?> e = this.fields.propertyNames();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			field = (Properties) this.fields.get(key);
			if (((String) field.get("FIELD_TYPE")).equals(_type)) {
				return new JSONObject((String) field.get("OPTIONS"));
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
	
	protected class PageEventHandler implements IEventHandler {

		@Override
		public void handleEvent(Event arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}	
	
}
