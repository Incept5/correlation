package org.incept5.correlation

/**
 * MDCAdaptor implementation that uses SLF4J's MDC.
 */
class Slf4jMDCAdaptor : MDCAdaptor {

    override fun put(key: String, value: String) {
        org.slf4j.MDC.put(key, value)
    }

    override fun remove(key: String) {
        org.slf4j.MDC.remove(key)
    }

}