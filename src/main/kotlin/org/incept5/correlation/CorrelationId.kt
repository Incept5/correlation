package com.velostone.correlation

import java.util.*

/**
 * Holder for correlation Ids which are made available in a ThreadLocal variable and
 * is also propagated to the MDCContext which is a composite of all registered MDCHandlers.
 * This allows correlation Ids to be propagated to loggers etc.
 */
object CorrelationId {

    private const val CORRELATION_ID_KEY = "correlationId"
    private val threadLocal = ThreadLocal<String>()

    /**
     * Returns the current correlation Id.
     * Will populate with a new id if one is not already set.
     */
    fun getId(): String {
        return threadLocal.get() ?: setNewId()
    }

    /**
     * Sets the current correlation Id.
     */
    fun setId(correlationId: String) {
        threadLocal.set(correlationId)
        MDCContext.put(CORRELATION_ID_KEY, correlationId)
    }

    /**
     * Clears the current correlation Id.
     */
    fun clear() {
        threadLocal.remove()
        MDCContext.remove(CORRELATION_ID_KEY)
    }

    private fun setNewId(): String {
        val newId = UUID.randomUUID().toString()
        setId(newId)
        return newId
    }
}
