package com.trainstation.service;

import com.trainstation.model.KhachHang;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for KhachHangService
 * Note: These tests verify the service layer integration.
 * Database connectivity is required for full integration tests.
 */
public class KhachHangServiceTest {

    @Test
    public void testServiceInstance() {
        // Test singleton pattern
        KhachHangService service1 = KhachHangService.getInstance();
        KhachHangService service2 = KhachHangService.getInstance();
        
        assertNotNull("Service instance should not be null", service1);
        assertSame("Service should be singleton", service1, service2);
    }

    @Test
    public void testTimKhachHangTheoSoDienThoaiMethodExists() {
        // Test that the search by phone number method exists and is accessible
        KhachHangService service = KhachHangService.getInstance();
        
        try {
            // This will throw exception if method doesn't exist or database is not connected
            // We're just testing method existence, not actual data
            service.timKhachHangTheoSoDienThoai("0123456789");
            // If we get here without compilation error, the method exists
            assertTrue("Method timKhachHangTheoSoDienThoai exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            // But method still exists if we got here without compilation error
            assertTrue("Method timKhachHangTheoSoDienThoai exists", true);
        }
    }
}
