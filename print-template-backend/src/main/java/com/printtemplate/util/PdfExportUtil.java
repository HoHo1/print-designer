package com.printtemplate.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfExportUtil {

    public static byte[] exportToPdf(String htmlContent) throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        
        document.open();
        
        Paragraph paragraph = new Paragraph(htmlContent);
        paragraph.setFont(FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL));
        document.add(paragraph);
        
        document.close();
        return outputStream.toByteArray();
    }

    public static byte[] createSimplePdf(String title, String content) throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        
        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(20f);
        document.add(titlePara);
        
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        Paragraph contentPara = new Paragraph(content, contentFont);
        document.add(contentPara);
        
        document.close();
        return outputStream.toByteArray();
    }
}
