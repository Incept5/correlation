package com.velostone.correlation

/**
 * You can add other MDC adaptors to this context and then all MDC operations will be delegated to
 * all of the registered adaptors.
 *
 * Add a new adaptor by calling the addMDCHandler method.
 *
 */
object MDCContext : MDCAdaptor {

    private val ADAPTORS = mutableListOf<MDCAdaptor>()

    init {
        // always add SLF4J MDC adaptor
        ADAPTORS.add(Slf4jMDCAdaptor())
    }

    fun addMDCAdaptor(adaptor: MDCAdaptor) {
        ADAPTORS.add(adaptor)
    }

    fun removeMDCAdaptor(adaptor: MDCAdaptor) {
        ADAPTORS.remove(adaptor)
    }

    override fun put(key: String, value: String) {
        ADAPTORS.forEach { it.put(key, value) }
    }

    override fun remove(key: String) {
        ADAPTORS.forEach { it.remove(key) }
    }
}