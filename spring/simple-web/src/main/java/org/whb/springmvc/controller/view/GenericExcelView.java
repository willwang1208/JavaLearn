package org.whb.springmvc.controller.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsView;
/**
 * 依赖poi相关jar包
 * 数据格式成excel的通用view
 * TODO
 * @author 
 *
 */
@Component("GenericExcelView")
public class GenericExcelView extends AbstractXlsView {

    @SuppressWarnings("unchecked")
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, String> revenueData = (Map<String, String>) model.get("revenueData");
        
        // create a word sheet
        Sheet sheet = workbook.createSheet("Revenue Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Month");
        header.createCell(1).setCellValue("Revenue");

        int rowNum = 1;
        for (Map.Entry<String, String> entry : revenueData.entrySet()) {
            // create the row data
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
    }

}
