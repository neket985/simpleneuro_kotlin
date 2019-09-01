package ru.simpleneuro.mongo

import org.bson.types.ObjectId
import ru.simpleneuro.NeuronWeb

data class NeuronWebMongoEntity(
        val name: String,
        val web: NeuronWeb,
        var _id: ObjectId? = null
)