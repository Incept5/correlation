# Correlation Library

The correlation lib that will allow a correlation UUID to be propagated through the system and appear in the logging output.

Builds on that by not just supporting Slf4j MDC but any other MDC style implementation via the MDCAdapter interface.

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

