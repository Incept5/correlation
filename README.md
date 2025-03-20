# Correlation Library

The correlation lib that will allow a correlation UUID to be propagated through the system and appear in the logging output.

Builds on that by not just supporting Slf4j MDC but any other MDC style implementation via the MDCAdapter interface.

## Installation

### Gradle

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.incept5:correlation:1.0.0-SNAPSHOT") // For local development
    // OR
    implementation("com.github.incept5:correlation:1.0.x") // For released version from JitPack
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.incept5</groupId>
        <artifactId>correlation</artifactId>
        <version>1.0.0-SNAPSHOT</version> <!-- For local development -->
    </dependency>
    <!-- OR -->
    <dependency>
        <groupId>com.github.incept5</groupId>
        <artifactId>correlation</artifactId>
        <version>1.0.x</version> <!-- For released version from JitPack -->
    </dependency>
</dependencies>
```

## Continuous Integration

This project uses CircleCI for continuous integration. When commits are pushed to the main branch:

1. CircleCI automatically builds and tests the project
2. If tests pass, it publishes the artifacts to JitPack with version `1.0.{build-number}`

The latest build status can be seen on the CircleCI dashboard.

## Sample Quarkus Implementation

The `sample-quarkus-correlation` module demonstrates how to use the CorrelationId library in a Quarkus application:

### JAX-RS Filter for Correlation ID

The sample includes a `CorrelationIdFilter` that implements both `ContainerRequestFilter` and `ContainerResponseFilter` to:

1. Extract the correlation ID from the `X-Correlation-ID` header if present
2. Generate a new correlation ID if none exists
3. Set the correlation ID in the thread-local context using `CorrelationId.setId()`
4. Add the correlation ID to the request headers
5. Add the correlation ID to the response headers

```kotlin
@Provider
@Priority(Priorities.HEADER_DECORATOR)
class CorrelationIdFilter : ContainerRequestFilter, ContainerResponseFilter {

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-ID"
        private const val CORRELATION_ID_PROPERTY = "correlationId"
    }

    override fun filter(requestContext: ContainerRequestContext) {
        // Get or create a new correlation ID
        val correlationId = requestContext.headers.getFirst(CORRELATION_ID_HEADER)
            ?: CorrelationId.getId()
            ?: UUID.randomUUID().toString()

        // Set the correlation ID in the thread local context
        CorrelationId.setId(correlationId)

        // Store the correlation ID in the request property for later use in the response filter
        requestContext.setProperty(CORRELATION_ID_PROPERTY, correlationId)

        // Add or update the correlation ID header in the request
        requestContext.headers.putSingle(CORRELATION_ID_HEADER, correlationId)
    }

    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        // Get the correlation ID from the request property
        val correlationId = requestContext.getProperty(CORRELATION_ID_PROPERTY) as? String
            ?: CorrelationId.getId()
            ?: UUID.randomUUID().toString()

        // Add the correlation ID to the response headers
        responseContext.headers.putSingle(CORRELATION_ID_HEADER, correlationId)
    }
}
```

### Logging Configuration

The sample configures Quarkus logging to include the correlation ID in log messages:

```yaml
quarkus:
  log:
    console:
      format: "%d{yyyy-MM-dd'T'HH:mm:ss.SSSZ} %-5p %H -- [%X{traceId}/%X{spanId}/%X{correlationId}] - [%c{3.}] (%t-%t{id}) %s%e%n"
```

This format includes `%X{correlationId}` to display the correlation ID from the MDC in each log entry.

### Usage in Controllers

Once the filter is in place, any controller in the application will automatically have access to the correlation ID in logs. Additionally, all responses will include the X-Correlation-ID header:

```kotlin
@Path("/hello")
class SampleController {
    private val logger = LoggerFactory.getLogger(SampleController::class.java)

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        logger.info("Hello endpoint invoked")  // This log will include the correlation ID
        return "Hello from Quarkus with Correlation ID!"
    }
}
```

### Testing the Correlation ID

You can test the correlation ID functionality with the following approaches:

1. **Providing a correlation ID in the request**:
   ```bash
   curl -H "X-Correlation-ID: my-custom-id-123" http://localhost:8080/hello -v
   ```
   The response will include the same correlation ID in the headers:
   ```
   < HTTP/1.1 200 OK
   < X-Correlation-ID: my-custom-id-123
   ```

2. **Without providing a correlation ID**:
   ```bash
   curl http://localhost:8080/hello -v
   ```
   The response will include a generated correlation ID in the headers:
   ```
   < HTTP/1.1 200 OK
   < X-Correlation-ID: 550e8400-e29b-41d4-a716-446655440000
   ```

## Spring Boot Implementation

Here's a suggested implementation for using the CorrelationId library in a Spring Boot application:

### Filter Implementation

Create a filter that will intercept all HTTP requests and handle the correlation ID:

```kotlin
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.incept5.correlation.CorrelationId
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CorrelationIdFilter : OncePerRequestFilter() {

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-ID"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // Extract correlation ID from header or generate a new one
            val correlationId = request.getHeader(CORRELATION_ID_HEADER) ?: CorrelationId.getId()
            
            // Set the correlation ID in the thread-local context
            CorrelationId.setId(correlationId)
            
            // Add the correlation ID to the response headers
            response.addHeader(CORRELATION_ID_HEADER, correlationId)
            
            // Continue with the filter chain
            filterChain.doFilter(request, response)
        } finally {
            // Clear the correlation ID after the request is processed
            CorrelationId.clear()
        }
    }
}
```

### Filter Registration

Register the filter in your Spring Boot application:

```kotlin
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
class WebConfig {

    @Bean
    fun correlationIdFilter(): FilterRegistrationBean<CorrelationIdFilter> {
        val registrationBean = FilterRegistrationBean<CorrelationIdFilter>()
        registrationBean.filter = CorrelationIdFilter()
        registrationBean.order = Ordered.HIGHEST_PRECEDENCE
        return registrationBean
    }
}
```

### Logging Configuration

Configure Spring Boot logging to include the correlation ID in log messages by updating your `application.yml` or `application.properties`:

```yaml
# application.yml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{correlationId}] - %msg%n"
```

Or in properties format:

```properties
# application.properties
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{correlationId}] - %msg%n
```

### Usage in Controllers

Once the filter is in place, any controller in the application will automatically have access to the correlation ID in logs. Additionally, all responses will include the X-Correlation-ID header:

```kotlin
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    private val logger = LoggerFactory.getLogger(HelloController::class.java)

    @GetMapping("/hello")
    fun hello(): String {
        logger.info("Hello endpoint invoked")  // This log will include the correlation ID
        return "Hello from Spring Boot with Correlation ID!"
    }
}
```

### Testing the Correlation ID

You can test the correlation ID functionality with the following approaches:

1. **Providing a correlation ID in the request**:
   ```bash
   curl -H "X-Correlation-ID: my-custom-id-123" http://localhost:8080/hello -v
   ```
   The response will include the same correlation ID in the headers:
   ```
   < HTTP/1.1 200 OK
   < X-Correlation-ID: my-custom-id-123
   ```

2. **Without providing a correlation ID**:
   ```bash
   curl http://localhost:8080/hello -v
   ```
   The response will include a generated correlation ID in the headers:
   ```
   < HTTP/1.1 200 OK
   < X-Correlation-ID: 550e8400-e29b-41d4-a716-446655440000
   ```

### WebClient Support (Optional)

If you're using Spring WebClient for outgoing HTTP requests, you can propagate the correlation ID to downstream services:

```kotlin
import org.incept5.correlation.CorrelationId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class WebClientConfig {

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-ID"
    }

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder
            .filter(correlationIdFilter())
            .build()
    }

    private fun correlationIdFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction { request, next ->
            val correlationId = CorrelationId.getId()
            val filteredRequest = ClientRequest.from(request)
                .header(CORRELATION_ID_HEADER, correlationId)
                .build()
            next.exchange(filteredRequest)
        }
    }
}
```

## Implement an MDCAdapter

To implement an MDCAdapter you need to implement the following methods:

```kotlin
class MyMDCAdapter: MDCAdapter {

    override fun put(key: String, value: String) {
        // put your context 
    }

    override fun remove(key: String) {
        // remove the context
    }
}
```

## Add in your MDCAdapter to the MDCContext

```kotlin

MDCContext.addMDCAdaptor(MyMDCAdapter());

```

And now when CorrelationId.setId(UUID) is called the MDCAdapter will be called to set the correlationId in your MDC as well as Slf4j's MDC.

