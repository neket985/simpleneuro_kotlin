package ru.simpleneuro.mongo

import org.bson.types.ObjectId
import ru.simpleneuro.NeuronWeb

data class NeuronWebMongoEntity(
        val name: String,
        val layersCount: Int,
        val layersSizes: List<Int>,
        var _id: ObjectId? = null
)