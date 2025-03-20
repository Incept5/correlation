package org.incept5.correlation.sample.quarkus

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.UUID

@QuarkusTest
class CorrelationIdFilterTest {

    @Test
    fun testWithProvidedCorrelationId() {
        // Generate a custom correlation ID
        val customCorrelationId = UUID.randomUUID().toString()

        // Make a request with the custom correlation ID in the header
        val response = given()
            .header(CorrelationIdFilter.CORRELATION_ID_HEADER, customCorrelationId)
            .`when`()
            .get("/hello")
            .then()
            .statusCode(200)
            .body(`is`("Hello from Quarkus with Correlation ID!"))
            .extract()
            .response()

        // Verify that the response contains the same correlation ID
        val responseCorrelationId = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)
        assertEquals(customCorrelationId, responseCorrelationId, 
            "Response should contain the same correlation ID that was provided in the request")
    }

    @Test
    fun testWithoutProvidedCorrelationId() {
        // Make a request without a correlation ID in the header
        val response = given()
            .`when`()
            .get("/hello")
            .then()
            .statusCode(200)
            .body(`is`("Hello from Quarkus with Correlation ID!"))
            .extract()
            .response()

        // Verify that the response contains a generated correlation ID
        val responseCorrelationId = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)
        org.junit.jupiter.api.Assertions.assertNotNull(responseCorrelationId, 
            "Response should contain a generated correlation ID")
        org.junit.jupiter.api.Assertions.assertTrue(responseCorrelationId.isNotEmpty(), 
            "Generated correlation ID should not be empty")
    }
}