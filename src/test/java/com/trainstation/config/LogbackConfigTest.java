package com.trainstation.config;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Test to verify SLF4J binding with Logback Classic is working correctly
 */
public class LogbackConfigTest {

    @Test
    public void testSLF4JBindingAvailable() {
        // This test verifies that SLF4J binding is available
        // If Logback is properly configured, no warnings should appear
        Logger logger = LoggerFactory.getLogger(LogbackConfigTest.class);
        
        assertNotNull("Logger should not be null", logger);
        
        // Test logging at different levels
        logger.trace("Trace level message");
        logger.debug("Debug level message");
        logger.info("Info level message - SLF4J binding test");
        logger.warn("Warning level message");
        logger.error("Error level message");
        
        // If we get here without exceptions, SLF4J binding is working
        assertTrue("Logger should be enabled", logger.isInfoEnabled() || 
                   logger.isDebugEnabled() || logger.isTraceEnabled());
    }
    
    @Test
    public void testLoggerForDifferentClasses() {
        // Test that loggers can be created for different classes
        Logger logger1 = LoggerFactory.getLogger("com.trainstation.test1");
        Logger logger2 = LoggerFactory.getLogger("com.trainstation.test2");
        
        assertNotNull("First logger should not be null", logger1);
        assertNotNull("Second logger should not be null", logger2);
        assertNotSame("Loggers should be different instances", logger1, logger2);
    }
}
