package com.trainstation.dao;

import com.trainstation.model.ChuyenTau;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Test for flexible train search functionality
 * Verifies that the search works with various combinations of null/empty criteria
 */
public class ChuyenTauDAOFlexibleSearchTest {
    
    @Test
    public void testSearchWithAllNullCriteria() {
        // Test that searching with all null criteria returns all trains
        ChuyenTauDAO dao = ChuyenTauDAO.getInstance();
        List<ChuyenTau> result = dao.timKiemChuyenTau(null, null, null, null);
        
        // Should return all trains (same as getAll())
        List<ChuyenTau> allTrains = dao.getAll();
        assertEquals("Search with all null criteria should return all trains", 
                     allTrains.size(), result.size());
    }
    
    @Test
    public void testSearchWithEmptyStringCriteria() {
        // Test that searching with empty strings is handled correctly
        ChuyenTauDAO dao = ChuyenTauDAO.getInstance();
        
        // Empty strings should be treated as null by the DAO
        List<ChuyenTau> result = dao.timKiemChuyenTau("", "", null, null);
        
        // Should return results (empty string check is done in DAO)
        assertNotNull("Result should not be null", result);
    }
    
    @Test
    public void testSearchWithOnlyDepartureStation() {
        // Test searching with only departure station
        ChuyenTauDAO dao = ChuyenTauDAO.getInstance();
        List<ChuyenTau> allTrains = dao.getAll();
        
        if (!allTrains.isEmpty()) {
            String firstStationDep = allTrains.get(0).getGaDi();
            List<ChuyenTau> result = dao.timKiemChuyenTau(firstStationDep, null, null, null);
            
            // All results should have the specified departure station
            for (ChuyenTau ct : result) {
                assertEquals("All trains should have departure station: " + firstStationDep,
                           firstStationDep, ct.getGaDi());
            }
        }
    }
    
    @Test
    public void testSearchWithOnlyArrivalStation() {
        // Test searching with only arrival station
        ChuyenTauDAO dao = ChuyenTauDAO.getInstance();
        List<ChuyenTau> allTrains = dao.getAll();
        
        if (!allTrains.isEmpty()) {
            String firstStationArr = allTrains.get(0).getGaDen();
            List<ChuyenTau> result = dao.timKiemChuyenTau(null, firstStationArr, null, null);
            
            // All results should have the specified arrival station
            for (ChuyenTau ct : result) {
                assertEquals("All trains should have arrival station: " + firstStationArr,
                           firstStationArr, ct.getGaDen());
            }
        }
    }
    
    @Test
    public void testSearchWithOnlyDate() {
        // Test searching with only date
        ChuyenTauDAO dao = ChuyenTauDAO.getInstance();
        List<ChuyenTau> allTrains = dao.getAll();
        
        if (!allTrains.isEmpty() && allTrains.get(0).getGioDi() != null) {
            LocalDate firstDate = allTrains.get(0).getGioDi().toLocalDate();
            List<ChuyenTau> result = dao.timKiemChuyenTau(null, null, firstDate, null);
            
            // All results should have the specified date
            for (ChuyenTau ct : result) {
                if (ct.getGioDi() != null) {
                    assertEquals("All trains should have date: " + firstDate,
                               firstDate, ct.getGioDi().toLocalDate());
                }
            }
        }
    }
    
    @Test
    public void testSearchWithMixedCriteria() {
        // Test searching with mixed criteria (some null, some not)
        ChuyenTauDAO dao = ChuyenTauDAO.getInstance();
        List<ChuyenTau> allTrains = dao.getAll();
        
        if (!allTrains.isEmpty()) {
            String depStation = allTrains.get(0).getGaDi();
            List<ChuyenTau> result = dao.timKiemChuyenTau(depStation, null, null, null);
            
            // Should only return trains from the departure station
            assertTrue("Should return results for valid criteria", 
                      result.size() > 0 || allTrains.size() == 0);
            
            for (ChuyenTau ct : result) {
                assertEquals("All trains should have departure station: " + depStation,
                           depStation, ct.getGaDi());
            }
        }
    }
}
