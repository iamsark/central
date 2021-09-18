package com.hul.central.visualizer.report.factory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

public class Test {

	public static void main(String[] args) throws Exception {
		
		PdfWriter writer = new PdfWriter("");
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);
		
		PageSize ps = new PageSize(842, 680);
		PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
		PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
		Table table = new Table(new float[]{1.5f, 7, 2, 2, 2, 2, 3, 4, 4, 2});
		table.setWidth(UnitValue.createPercentValue(100))
		        .setTextAlignment(TextAlignment.CENTER)
		        .setHorizontalAlignment(HorizontalAlignment.CENTER);
		BufferedReader br = new BufferedReader(new FileReader(""));
		String line = br.readLine();
		Test.process(table, line, bold, true);
		while ((line = br.readLine()) != null) {
			Test.process(table, line, font, false);
		}
		br.close();
		document.add(table);
		
	}
	
	public static void process(Table table, String line, PdfFont font, boolean isHeader) {
	    StringTokenizer tokenizer = new StringTokenizer(line, ";");
	    int columnNumber = 0;
	    while (tokenizer.hasMoreTokens()) {
	        if (isHeader) {
	            Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
	            //cell.setNextRenderer(new RoundedCornersCellRenderer(cell));
	            cell.setPadding(5).setBorder(null);
	            table.addHeaderCell(cell);
	        } else {
	            columnNumber++;
	            Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
	            //cell.setFont(font).setBorder(new SolidBorder(Color.BLACK, 0.5f));	            
	            table.addCell(cell);
	        }
	    }
	}

}
