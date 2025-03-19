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

The sample includes a `CorrelationIdFilter` that implements `ContainerRequestFilter` to:

1. Extract the correlation ID from the `X-Correlation-ID` header if present
2. Generate a new correlation ID if none exists
3. Set the correlation ID in the thread-local context using `CorrelationId.setId()`
4. Add the correlation ID back to the request headers

```kotlin
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

Once the filter is in place, any controller in the application will automatically have access to the correlation ID in logs:

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

