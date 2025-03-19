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

