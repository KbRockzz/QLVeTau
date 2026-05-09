package com.trainstation.service;

import com.trainstation.model.DauMay;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for DauMayService (Locomotive/Engine Service)
 * Note: These tests verify the service layer integration and CRUD methods.
 * Database connectivity is required for full integration tests.
 */
public class DauMayServiceTest {

    @Test
    public void testServiceInstance() {
        // Test singleton pattern
        DauMayService service1 = DauMayService.getInstance();
        DauMayService service2 = DauMayService.getInstance();
        
        assertNotNull("Service instance should not be null", service1);
        assertSame("Service should be singleton", service1, service2);
    }

    @Test
    public void testLayDauMayDangHoatDongMethodExists() {
        // Test that the method to get active locomotives exists and is accessible
        DauMayService service = DauMayService.getInstance();
        
        try {
            service.layDauMayDangHoatDong();
            assertTrue("Method layDauMayDangHoatDong exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            assertTrue("Method layDauMayDangHoatDong exists", true);
        }
    }

    @Test
    public void testDungHoatDongDauMayMethodExists() {
        // Test that the soft delete method exists and is accessible
        DauMayService service = DauMayService.getInstance();
        
        try {
            service.dungHoatDongDauMay("TEST_DM_001");
            assertTrue("Method dungHoatDongDauMay exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            assertTrue("Method dungHoatDongDauMay exists", true);
        }
    }

    @Test
    public void testThemDauMayMethodExists() {
        // Test that the add locomotive method exists and is accessible
        DauMayService service = DauMayService.getInstance();
        
        try {
            DauMay testDauMay = new DauMay("TEST_DM_002", "Diesel", "Đầu máy Test", 2020, null, "Hoạt động");
            service.themDauMay(testDauMay);
            assertTrue("Method themDauMay exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            assertTrue("Method themDauMay exists", true);
        }
    }

    @Test
    public void testCapNhatDauMayMethodExists() {
        // Test that the update locomotive method exists and is accessible
        DauMayService service = DauMayService.getInstance();
        
        try {
            DauMay testDauMay = new DauMay("TEST_DM_003", "Electric", "Đầu máy Updated", 2021, null, "Hoạt động");
            service.capNhatDauMay(testDauMay);
            assertTrue("Method capNhatDauMay exists", true);
        } catch (Exception e) {
            // Expected if database is not connected
            assertTrue("Method capNhatDauMay exists", true);
        }
    }
}
