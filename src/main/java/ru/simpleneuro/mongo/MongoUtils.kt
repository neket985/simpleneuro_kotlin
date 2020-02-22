package ru.simpleneuro.mongo

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import com.typesafe.config.ConfigFactory
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoConfiguration
import ru.simpleneuro.Neuron
import ru.simpleneuro.NeuronLayer
import ru.simpleneuro.NeuronWeb
import kotlin.reflect.full.declaredMemberProperties

object MongoUtils {
    private val config = ConfigFactory.load().getConfig("mongoDb")
    private val servers = config.getStringList("servers").map {
        ServerAddress(it)
    }
    private val dbName = config.getString("database")
    private val credentials = config.getConfigList("credentials").map {
        val user = it.getString("username")
        val pass = it.getString("password").toCharArray()
        MongoCredential.createCredential(user, dbName, pass)
    }

    private val mongoClient = KMongo.createClient(servers, credentials)
    private val db = mongoClient.getDatabase(dbName)
    private val webCollection = db.getCollection<NeuronWebMongoEntity>("web")
    private val neuronLayerCollection = db.getCollection<NeuronLayerMongo>("layer")
    private val neuronCollection = db.getCollection<NeuronMongo>("neuron")

    init {
        KMongoConfiguration.registerBsonModule(
                SimpleModule()
                        .addSerializer(object : StdSerializer<RealVector>(RealVector::class.java) {
                            override fun serialize(vector: RealVector, jgen: JsonGenerator, p2: SerializerProvider?) {
                                jgen.writeArray(vector.toArray(), 0, vector.dimension)
                            }

                        })
                        .addDeserializer<RealVector>(RealVector::class.java,
                                object : StdDeserializer<RealVector>(RealVector::class.java) {
                                    override fun deserialize(jparse: JsonParser, p1: DeserializationContext?): RealVector {
                                        val arr = jparse.readValueAs(DoubleArray::class.java)
                                        return MatrixUtils.createRealVector(arr)
                                    }
                                })
        )
    }

    fun saveWeb(web: NeuronWeb) {
        val neuronWeb = upsertWeb(web)
        web.layers.mapIndexed { i, v ->
            val neuronLayer = upsertLayer(v.size, v.prevLayerSize, i, neuronWeb._id!!)
            v.neurons.mapIndexed { ni, nv ->
                upsertNeuron(nv.relationsCount, nv.weights, nv.b, ni, neuronLayer._id!!)
            }
        }
    }

    fun loadWeb(name: String): NeuronWeb? =
            getWeb(name)?.let { web ->
                val layers = neuronLayerCollection
                        .find(NeuronLayerMongo::webId eq web._id!!)
                        .sort(NeuronLayerMongo::layerNum eq 1)
                        .map { layer ->
                            val neurons = neuronCollection
                                    .find(NeuronMongo::layerId eq layer._id!!)
                                    .sort(NeuronMongo::neuronNum eq 1)
                                    .map { neuron ->
                                        Neuron(neuron.relationsCount, neuron.weights, neuron.b)
                                    }.toList()
                            NeuronLayer(
                                    layer.size,
                                    layer.prevLayerSize,
                                    neurons
                            )
                        }.toList()

                NeuronWeb(web.name, web.layersCount, web.connectionsDimension, layers)
            }


    fun deleteWeb(name: String) {
        getWeb(name)?.let {web ->
            webCollection.deleteOneById(web._id!!)
            val layersIds = neuronLayerCollection.find(NeuronLayerMongo::webId eq web._id).map { it._id }.toList()
            neuronLayerCollection.deleteMany(NeuronLayerMongo::webId eq web._id)
            neuronCollection.deleteMany(NeuronMongo::layerId `in` layersIds)
        }
    }

    fun getWeb(name: String) = webCollection.findOne(NeuronWebMongoEntity::name eq name)
    fun upsertWeb(name: String, layersCount: Int, layersSizes: List<Int>): NeuronWebMongoEntity {
        webCollection.upsert(
                NeuronWebMongoEntity::name eq name,
                NeuronWebMongoEntity(name, layersCount, layersSizes)
        )
        return getWeb(name)!!
    }

    fun upsertWeb(web: NeuronWeb) = upsertWeb(web.name, web.layersCount, web.connectionsDimension)

    fun getLayer(webId: ObjectId, layerNum: Int) = neuronLayerCollection.findOne(
            NeuronLayerMongo::webId eq webId,
            NeuronLayerMongo::layerNum eq layerNum
    )

    fun upsertLayer(size: Int, prevLayerSize: Int, layerNum: Int, webId: ObjectId): NeuronLayerMongo {
        neuronLayerCollection.upsert(
                and(
                        NeuronLayerMongo::webId eq webId,
                        NeuronLayerMongo::layerNum eq layerNum
                ),
                NeuronLayerMongo(size, prevLayerSize, layerNum, webId)
        )
        return getLayer(webId, layerNum)!!
    }

    fun getNeuron(layerId: ObjectId, neuronNum: Int) = neuronCollection.findOne(
            NeuronMongo::layerId eq layerId,
            NeuronMongo::neuronNum eq neuronNum
    )

    fun upsertNeuron(relationsCount: Int, weights: RealVector, b: Double, neuronNum: Int, layerId: ObjectId): NeuronMongo {
        neuronCollection.upsert(
                and(
                        NeuronMongo::layerId eq layerId,
                        NeuronMongo::neuronNum eq neuronNum
                ),
                NeuronMongo(relationsCount, weights, b, neuronNum, layerId)
        )
        return getNeuron(layerId, neuronNum)!!
    }

    fun <T : Any> MongoCollection<T>.upsert(filter: Bson, data: T) {
        val updates = data::class.declaredMemberProperties.mapNotNull {
            if (it.name != "_id") {
                setValue(it, it.getter.call(data))
            } else null
        }.toTypedArray()
        this.updateOne(filter, combine(*updates), UpdateOptions().upsert(true))
    }
}
