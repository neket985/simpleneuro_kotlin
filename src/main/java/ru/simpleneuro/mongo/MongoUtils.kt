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
import com.typesafe.config.ConfigFactory
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoConfiguration
import ru.simpleneuro.NeuronWeb
import java.net.InetAddress

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
    private val neuronCollection = db.getCollection<NeuronWebMongoEntity>("web")

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

    fun saveWeb(name: String, web: NeuronWeb): NeuronWebMongoEntity? {
        val result = neuronCollection.updateOne(NeuronWebMongoEntity::name eq name, setValue(NeuronWebMongoEntity::web, web))
        if (result.matchedCount == 0L) {
            neuronCollection.insertOne(NeuronWebMongoEntity(name, web))
        }

        return neuronCollection.findOne(NeuronWebMongoEntity::name eq name)
    }

    fun loadWeb(name: String) = neuronCollection.findOne(NeuronWebMongoEntity::name eq name)

}