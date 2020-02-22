package ru.simpleneuro

import org.slf4j.LoggerFactory
import ru.simpleneuro.mongo.MongoUtils
import java.util.concurrent.ConcurrentSkipListSet

object ShutdownHook {
    private val webs = ConcurrentSkipListSet<NeuronWeb>()
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun addWeb(web: NeuronWeb) = webs.add(web)

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            webs.forEach {
                logger.debug("Saving web ${it.name} started")
                MongoUtils.saveWeb(it)
                logger.debug("Saving web ${it.name} success")
            }
        })
    }
}