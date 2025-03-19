package com.velostone.correlation

/**
 * Mapped Diagnostic Context (MDC) handler interface.
 */
interface MDCAdaptor {

    fun put(key: String, value: String)

    fun remove(key: String)

}