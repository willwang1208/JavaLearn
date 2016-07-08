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
		
		
//		// step 1
//		Document document = new Document();
//		try {
//			// step 2
//			PdfWriter.getInstance(document, new FileOutputStream("results/in_action/chapter08/ttc.pdf"));
//
//			// step 3: we open the document
//			document.open();
//			// step 4
//			BaseFont bf;
//			Font font;
//			bf = BaseFont.createFont("c:/windows/fonts/msgothic.ttc,0",
//					BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//			font = new Font(bf, 12);
//			System.out.println(bf.getClass().getName());
//			document.add(new Paragraph("Rash\u00f4mon", font));
//			document.add(new Paragraph("Directed by Akira Kurosawa", font));
//			document.add(new Paragraph("\u7f85\u751f\u9580", font));
//			String[] names = BaseFont
//					.enumerateTTCNames("c:/windows/fonts/msgothic.ttc");
//			for (int i = 0; i < names.length; i++) {
//				document
//						.add(new Paragraph("font " + i + ": " + names[i], font));
//			}
//		} catch (DocumentException de) {
//			System.err.println(de.getMessage());
//		} catch (IOException ioe) {
//			System.err.println(ioe.getMessage());
//		}
//
//		// step 5: we close the document
//		document.close();
	}
	
}
