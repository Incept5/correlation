package org.incept5.correlation.sample.quarkus

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.ext.Provider
import com.velostone.correlation.CorrelationId

@Provider
class CorrelationIdFilter : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext) {
        // Get or create a new correlation ID
        val correlationId = requestContext.headers.getFirst("X-Correlation-ID") ?: CorrelationId.getId()
        
        // Set the correlation ID in the thread local context
        CorrelationId.setId(correlationId)
        
        // Add or update the correlation ID header in the request
        requestContext.headers.putSingle("X-Correlation-ID", correlationId)
    }
}