package ru.simpleneuro.mongo

import org.bson.types.ObjectId

class NeuronLayerMongo(
        val size: Int,
        val prevLayerSize: Int,
        val webId: ObjectId,
        val _id: ObjectId? = null
)