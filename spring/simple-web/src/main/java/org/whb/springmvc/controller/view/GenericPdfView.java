package org.whb.springmvc.controller.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
/**
 * 依赖iText相关jar包
 * 数据格式成pdf的通用view
 * TODO
 * @author 
 *
 */
@Component("GenericPdfView")
public class GenericPdfView extends AbstractPdfView {

    @SuppressWarnings("unchecked")
	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document,
		PdfWriter writer, HttpServletRequest request,
		HttpServletResponse response) throws Exception {
 
		Map<String, String> revenueData = (Map<String, String>) model.get("revenueData");
		
		PdfPTable table = new PdfPTable(2);
		table.addCell("Month");
		table.addCell("Revenue");

		for (Map.Entry<String, String> entry : revenueData.entrySet()) {
			table.addCell(entry.getKey());
			table.addCell(entry.getValue());
		}

		document.add(table);
	}
	
}
