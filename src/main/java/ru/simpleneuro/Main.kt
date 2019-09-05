package ru.simpleneuro

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import ru.simpleneuro.mongo.MongoUtils
import ru.simpleneuro.mongo.NeuronWebMongoEntity
import java.awt.image.DataBufferByte
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import kotlin.random.Random

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val imgDir = File("/home/nikita/Загрузки/mnist")
        val trainDir = File(imgDir, "trainingSet")

        val trainVectors = ConcurrentHashMap(
                (0..9).map { i ->
                    val outVector = createOutVector(i)
                    val numberDir = File(trainDir, i.toString())

                    i to numberDir.listFiles()!!.map {
                        //input to output
                        val arr = (ImageIO.read(it).data.dataBuffer as DataBufferByte).data.map {
                            (it.toDouble() / Byte.MAX_VALUE)
                        }.toDoubleArray()
                        val vector = MatrixUtils.createRealVector(arr)
                        vector to outVector
                    }
                }.toMap()
        )

        runBlocking {
            val web = MongoUtils.loadWeb("numbers")!!
            val fAs = infinityTrain(web, trainVectors, 0.1)

//            val web2 = MongoUtils.loadWeb("numbers2")!!
//            val sAs = infinityTrain(web2, trainVectors, 0.001)

            val web3 = MongoUtils.loadWeb("numbers3")
                    ?: NeuronWebMongoEntity("numbers3", NeuronWeb("numbers3", 3, listOf(784, 800, 800, 10)))
            val tAs = infinityTrain(web3, trainVectors, 0.5)
            fAs.join()
//            sAs.join()
            tAs.join()
        }

    }

    fun infinityTrain(web: NeuronWebMongoEntity, trainVectors: ConcurrentHashMap<Int, List<Pair<RealVector, RealVector>>>,
                      trainScope: Double) =
            GlobalScope.async {
                GlobalScope.async {
                    while (true) {
                        Thread.sleep(5 * 60 * 1000)
                        MongoUtils.saveWeb(web.name, web.web)
                    }
                }
                while (true) {
                    (0..9).forEach { i ->
                        val train = trainVectors[i]!!
                        val randFile = Random.nextInt(train.size)
                        val (vector, outVector) = train[randFile]
//                        web.web.train(trainScope, vector, outVector)
                    }
                }
            }

    fun createOutVector(i: Int) = MatrixUtils.createRealVector(
            (0..9).map { index ->
                if (index == i) 1.0
                else 0.0
            }.toDoubleArray()
    )

    fun RealVector.toOutNumber() = this.maxIndex
}