package ru.simpleneuro

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import ru.simpleneuro.mongo.MongoUtils
import java.awt.image.DataBufferByte
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import kotlin.random.Random

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val imgDir = File("/Users/nikitos/Downloads/mnistasjpg")
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

        val web4 = MongoUtils.loadWeb("numbers")
                ?: NeuronWeb("numbers", 2, listOf(784, 50, 10))
        ShutdownHook.addWeb(web4)
        infinityTrain(web4, trainVectors, 0.1)

    }

    fun infinityTrain(
            web: NeuronWeb,
            trainVectors: ConcurrentHashMap<Int, List<Pair<RealVector, RealVector>>>,
            trainScope: Double
    ) {
        while (true) {
            (0..9).forEach { i ->
                val train = trainVectors[i]!!
                val randFile = Random.nextInt(train.size)
                val (vector, outVector) = train[randFile]
                web.train(trainScope, vector, outVector)
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