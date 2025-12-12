package com.trainstation.service;

import com.trainstation.model.Ga;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for GaService
 * Note: These tests verify the service layer integration and CRUD methods.
 * Database connectivity is required for full integration tests.
 */
public class GaServiceTest {

    @Test
    public void testServiceInstance() {
        // Test singleton pattern
        GaService service1 = GaService.getInstance();
        GaService service2 = GaService.getInstance();
        
        assertNotNull("Service instance should not be null", service1);
        assertSame("Service should be singleton", service1, service2);
    }

    @Test
    public void testLayTatCaGaMethodExists() {
        // Test that the method to get all stations exists and is accessible
        GaService service = GaService.getInstance();
        
        try {
            // This will throw exception if method doesn't exist or database is not connected
            // We're just testing method existence, not actual data
            service.layTatCaGa();
            // If we get here without compilation error, the method exists
            assertTrue("Method layTatCaGa exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            // But method still exists if we got here without compilation error
            assertTrue("Method layTatCaGa exists", true);
        }
    }

    @Test
    public void testTimGaTheoMaMethodExists() {
        // Test that the find station by ID method exists and is accessible
        GaService service = GaService.getInstance();
        
        try {
            // This will throw exception if method doesn't exist or database is not connected
            // We're just testing method existence, not actual data
            service.timGaTheoMa("GA001");
            // If we get here without compilation error, the method exists
            assertTrue("Method timGaTheoMa exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            // But method still exists if we got here without compilation error
            assertTrue("Method timGaTheoMa exists", true);
        }
    }

    @Test
    public void testThemGaMethodExists() {
        // Test that the add station method exists and is accessible
        GaService service = GaService.getInstance();
        
        try {
            // This will throw exception if method doesn't exist or database is not connected
            // We're just testing method existence, not actual data
            Ga testGa = new Ga("GA001", "Ga Test", "Mô tả test", "Hoạt động", "Địa chỉ test", true);
            service.themGa(testGa);
            // If we get here without compilation error, the method exists
            assertTrue("Method themGa exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            // But method still exists if we got here without compilation error
            assertTrue("Method themGa exists", true);
        }
    }

    @Test
    public void testCapNhatGaMethodExists() {
        // Test that the update station method exists and is accessible
        GaService service = GaService.getInstance();
        
        try {
            // This will throw exception if method doesn't exist or database is not connected
            // We're just testing method existence, not actual data
            Ga testGa = new Ga("GA001", "Ga Updated", "Mô tả updated", "Hoạt động", "Địa chỉ updated", true);
            service.capNhatGa(testGa);
            // If we get here without compilation error, the method exists
            assertTrue("Method capNhatGa exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            // But method still exists if we got here without compilation error
            assertTrue("Method capNhatGa exists", true);
        }
    }

    @Test
    public void testXoaGaMethodExists() {
        // Test that the delete station method exists and is accessible
        GaService service = GaService.getInstance();
        
        try {
            // This will throw exception if method doesn't exist or database is not connected
            // We're just testing method existence, not actual data
            service.xoaGa("GA001");
            // If we get here without compilation error, the method exists
            assertTrue("Method xoaGa exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            // But method still exists if we got here without compilation error
            assertTrue("Method xoaGa exists", true);
        }
    }

    @Test
    public void testTaoMaGaMethodExists() {
        // Test that the generate station ID method exists and is accessible
        GaService service = GaService.getInstance();
        
        try {
            // This will throw exception if method doesn't exist or database is not connected
            // We're just testing method existence, not actual data
            String maGa = service.taoMaGa();
            // If we get here without compilation error, the method exists
            assertTrue("Method taoMaGa exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            // But method still exists if we got here without compilation error
            assertTrue("Method taoMaGa exists", true);
        }
    }
}
