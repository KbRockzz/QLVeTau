package com.trainstation.gui;

import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

/**
 * Test class to validate regex patterns used in PnlTau
 */
public class PnlDauMayValidationTest {
    
    // Same regex patterns as in PnlTau
    private static final Pattern PATTERN_MA_TAU = Pattern.compile("^T\\d{3}$");
    private static final Pattern PATTERN_TEN_TAU = Pattern.compile("^[a-zA-Z0-9\\s\\-]{1,100}$");
    private static final Pattern PATTERN_SO_TOA = Pattern.compile("^([1-9]|[1-9][0-9])$");
    
    @Test
    public void testMaTauValidation() {
        // Valid train codes
        assertTrue("T001 should be valid", PATTERN_MA_TAU.matcher("T001").matches());
        assertTrue("T123 should be valid", PATTERN_MA_TAU.matcher("T123").matches());
        assertTrue("T999 should be valid", PATTERN_MA_TAU.matcher("T999").matches());
        
        // Invalid train codes
        assertFalse("T0001 should be invalid (4 digits)", PATTERN_MA_TAU.matcher("T0001").matches());
        assertFalse("T01 should be invalid (2 digits)", PATTERN_MA_TAU.matcher("T01").matches());
        assertFalse("T1 should be invalid (1 digit)", PATTERN_MA_TAU.matcher("T1").matches());
        assertFalse("001 should be invalid (no T prefix)", PATTERN_MA_TAU.matcher("001").matches());
        assertFalse("t001 should be invalid (lowercase t)", PATTERN_MA_TAU.matcher("t001").matches());
        assertFalse("TA01 should be invalid (contains letter)", PATTERN_MA_TAU.matcher("TA01").matches());
        assertFalse("T 001 should be invalid (contains space)", PATTERN_MA_TAU.matcher("T 001").matches());
    }
    
    @Test
    public void testTenTauValidation() {
        // Valid train names
        assertTrue("SE1 should be valid", PATTERN_TEN_TAU.matcher("SE1").matches());
        assertTrue("Tau Thong Nhat should be valid", PATTERN_TEN_TAU.matcher("Tau Thong Nhat").matches());
        assertTrue("Express-Train should be valid", PATTERN_TEN_TAU.matcher("Express-Train").matches());
        assertTrue("Train123 should be valid", PATTERN_TEN_TAU.matcher("Train123").matches());
        assertTrue("A-1 B should be valid", PATTERN_TEN_TAU.matcher("A-1 B").matches());
        
        // Invalid train names
        String tooLong = "a".repeat(101);
        assertFalse("Name with 101 chars should be invalid", PATTERN_TEN_TAU.matcher(tooLong).matches());
        assertFalse("Name with special chars should be invalid", PATTERN_TEN_TAU.matcher("Train@123").matches());
        assertFalse("Name with Vietnamese chars should be invalid", PATTERN_TEN_TAU.matcher("Tàu").matches());
        assertFalse("Empty string should be invalid", PATTERN_TEN_TAU.matcher("").matches());
    }
    
    @Test
    public void testSoToaValidation() {
        // Valid carriage numbers
        assertTrue("1 should be valid", PATTERN_SO_TOA.matcher("1").matches());
        assertTrue("5 should be valid", PATTERN_SO_TOA.matcher("5").matches());
        assertTrue("9 should be valid", PATTERN_SO_TOA.matcher("9").matches());
        assertTrue("10 should be valid", PATTERN_SO_TOA.matcher("10").matches());
        assertTrue("50 should be valid", PATTERN_SO_TOA.matcher("50").matches());
        assertTrue("99 should be valid", PATTERN_SO_TOA.matcher("99").matches());
        
        // Invalid carriage numbers
        assertFalse("0 should be invalid", PATTERN_SO_TOA.matcher("0").matches());
        assertFalse("00 should be invalid", PATTERN_SO_TOA.matcher("00").matches());
        assertFalse("01 should be invalid (leading zero)", PATTERN_SO_TOA.matcher("01").matches());
        assertFalse("100 should be invalid", PATTERN_SO_TOA.matcher("100").matches());
        assertFalse("-1 should be invalid", PATTERN_SO_TOA.matcher("-1").matches());
        assertFalse("1.5 should be invalid", PATTERN_SO_TOA.matcher("1.5").matches());
        assertFalse("abc should be invalid", PATTERN_SO_TOA.matcher("abc").matches());
        assertFalse("Empty string should be invalid", PATTERN_SO_TOA.matcher("").matches());
    }
    
    @Test
    public void testBoundaryValues() {
        // Test boundary values for train name length
        String maxLength = "a".repeat(100);
        assertTrue("Name with exactly 100 chars should be valid", PATTERN_TEN_TAU.matcher(maxLength).matches());
        
        String justOverMax = "a".repeat(101);
        assertFalse("Name with 101 chars should be invalid", PATTERN_TEN_TAU.matcher(justOverMax).matches());
        
        // Test boundary values for carriage count
        assertTrue("1 (minimum) should be valid", PATTERN_SO_TOA.matcher("1").matches());
        assertTrue("99 (maximum) should be valid", PATTERN_SO_TOA.matcher("99").matches());
        assertFalse("0 (below minimum) should be invalid", PATTERN_SO_TOA.matcher("0").matches());
        assertFalse("100 (above maximum) should be invalid", PATTERN_SO_TOA.matcher("100").matches());
    }
}
