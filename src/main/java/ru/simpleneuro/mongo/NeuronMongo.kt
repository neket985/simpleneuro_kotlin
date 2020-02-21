package ru.simpleneuro.mongo

import org.apache.commons.math3.linear.RealVector
import org.bson.types.ObjectId

class NeuronMongo(
        val relationsCount: Int,
        val weights: RealVector,
        val b: Double,
        val nueronNum: Int,
        val layerId: ObjectId,
        val _id: ObjectId? = null
)