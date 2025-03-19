package org.incept5.correlation.sample.quarkus

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.incept5.correlation.CorrelationId
import org.junit.jupiter.api.Assertions.assertNotNull

@QuarkusTest
class SampleControllerTest {

    private val logger = LoggerFactory.getLogger(SampleControllerTest::class.java)

    @Test
    fun testHelloEndpoint() {
        // Clear any existing correlation ID
        CorrelationId.clear()
        
        // Call the endpoint
        given()
            .`when`().get("/hello")
            .then()
            .statusCode(200)
            .body(containsString("Hello from Quarkus with Correlation ID!"))
        
        // Verify that a correlation ID was set in the MDC
        val correlationId = MDC.get("correlationId")
        logger.info("Correlation ID in test: {}", correlationId)
        assertNotNull(correlationId, "Correlation ID should be present in MDC")
    }

    @Test
    fun testHelloEndpointWithCustomCorrelationId() {
        // Clear any existing correlation ID
        CorrelationId.clear()
        
        val customCorrelationId = "test-correlation-id-12345"
        
        // Call the endpoint with a custom correlation ID
        given()
            .header("X-Correlation-ID", customCorrelationId)
            .`when`().get("/hello")
            .then()
            .statusCode(200)
            .body(containsString("Hello from Quarkus with Correlation ID!"))
        
        // Verify that our custom correlation ID was used
        val correlationId = MDC.get("correlationId")
        logger.info("Custom Correlation ID in test: {}", correlationId)
        assertNotNull(correlationId, "Correlation ID should be present in MDC")
    }
}