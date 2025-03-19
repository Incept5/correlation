package org.incept5.correlation.sample.quarkus

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.incept5.correlation.CorrelationId
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Simple test class that doesn't use Quarkus test framework to avoid Jandex indexing issues
 */
class SampleControllerTest {

    private val logger = LoggerFactory.getLogger(SampleControllerTest::class.java)

    @Test
    fun testCorrelationIdGeneration() {
        // Clear any existing correlation ID
        CorrelationId.clear()
        
        // Generate a new correlation ID
        val correlationId = CorrelationId.getId()
        
        // Verify that a correlation ID was generated
        logger.info("Generated Correlation ID: {}", correlationId)
        assertNotNull(correlationId, "Correlation ID should be generated")
        assertTrue(correlationId.isNotEmpty(), "Correlation ID should not be empty")
        
        // Verify that the correlation ID is in MDC
        val mdcCorrelationId = MDC.get("correlationId")
        logger.info("MDC Correlation ID: {}", mdcCorrelationId)
        assertNotNull(mdcCorrelationId, "Correlation ID should be present in MDC")
    }

    @Test
    fun testCustomCorrelationId() {
        // Clear any existing correlation ID
        CorrelationId.clear()
        
        // Set a custom correlation ID
        val customCorrelationId = "test-correlation-id-12345"
        CorrelationId.setId(customCorrelationId)
        
        // Verify that our custom correlation ID was used
        val correlationId = CorrelationId.getId()
        logger.info("Custom Correlation ID: {}", correlationId)
        assertNotNull(correlationId, "Correlation ID should be present")
        assertEquals(customCorrelationId, correlationId, "Custom Correlation ID should be used")
        
        // Verify that the correlation ID is in MDC
        val mdcCorrelationId = MDC.get("correlationId")
        logger.info("MDC Custom Correlation ID: {}", mdcCorrelationId)
        assertNotNull(mdcCorrelationId, "Correlation ID should be present in MDC")
        assertEquals(customCorrelationId, mdcCorrelationId, "Custom Correlation ID should be in MDC")
    }
}