package org.incept5.correlation

import java.util.*

/**
 * Holder for traces Ids which are made available in a ThreadLocal variable and
 * is also propagated to the MDCContext which is a composite of all registered MDCHandlers.
 * This allows correlation Ids to be propagated to loggers etc.
 */
object TraceId {

    private const val TRACE_ID_KEY = "traceId"
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
    fun setId(traceId: String) {
        threadLocal.set(traceId)
        MDCContext.put(TRACE_ID_KEY, traceId)
    }

    /**
     * Clears the current correlation Id.
     */
    fun clear() {
        threadLocal.remove()
        MDCContext.remove(TRACE_ID_KEY)
    }

    private fun setNewId(): String {
        val newId = UUID.randomUUID().toString()
        setId(newId)
        return newId
    }
}
