package ru.simpleneuro

import ru.simpleneuro.mongo.MongoUtils

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val simpleWeb = ru.simpleneuro.NeuronWeb(1, listOf(3, 1))
        val qq = MongoUtils.saveWeb("simple", simpleWeb)
        val q2 = MongoUtils.loadWeb("simple")
        println()
    }
}