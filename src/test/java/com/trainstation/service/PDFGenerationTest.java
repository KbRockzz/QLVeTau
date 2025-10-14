package com.trainstation.service;

import com.trainstation.model.Ve;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Test for PDF generation functionality
 */
public class PDFGenerationTest {

    @Test
    public void testTicketPDFGeneration() throws Exception {
        VeService veService = VeService.getInstance();
        
        // Create a test ticket
        Ve ve = new Ve();
        ve.setMaVe("VE_TEST_001");
        ve.setMaChuyen("CT001");
        ve.setGaDi("Ha Noi");
        ve.setGaDen("Ho Chi Minh");
        ve.setGioDi(LocalDateTime.now());
        ve.setSoToa("TOA01");
        ve.setMaSoGhe("A01");
        ve.setLoaiCho("Ngoi mem");
        ve.setLoaiVe("Nguoi lon");
        ve.setTrangThai("Da dat");
        
        // Generate PDF
        String fileName = veService.inVePDF(ve);
        
        // Verify file was created
        assertNotNull("File name should not be null", fileName);
        File pdfFile = new File(fileName);
        assertTrue("PDF file should exist", pdfFile.exists());
        assertTrue("PDF file should have content", pdfFile.length() > 0);
        
        System.out.println("Ticket PDF generated successfully: " + fileName);
    }

    @Test
    public void testDirectoryCreation() {
        File ticketsDir = new File("tickets");
        File invoicesDir = new File("invoices");
        
        // The directories should be created automatically by the services
        // This test just ensures they can be created if needed
        if (!ticketsDir.exists()) {
            assertTrue("Tickets directory should be creatable", ticketsDir.mkdirs());
        }
        if (!invoicesDir.exists()) {
            assertTrue("Invoices directory should be creatable", invoicesDir.mkdirs());
        }
        
        System.out.println("Directories verified/created successfully");
    }
}
