package org.incept5.correlation.sample.quarkus

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.slf4j.LoggerFactory

@Path("/hello")
class SampleController {

    private val logger = LoggerFactory.getLogger(SampleController::class.java)

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        logger.info("Hello endpoint invoked")
        return "Hello from Quarkus with Correlation ID!"
    }
}