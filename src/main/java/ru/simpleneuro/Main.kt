package ru.simpleneuro

import kotlinx.coroutines.runBlocking
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import ru.simpleneuro.mongo.MongoUtils
import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO

object Main {
    private val web = MongoUtils.loadWeb("numbers2")?.web ?: NeuronWeb(2, listOf(784, 300, 10))
    @JvmStatic
    fun main(args: Array<String>) {
        val imgDir = File("/Users/nikitos/Downloads/mnistasjpg")
        val trainDir = File(imgDir, "trainingSet")
        var fileNum = 0

        (0..9999).forEach {

            runBlocking {
                (0..9).map { i ->
                    //                    GlobalScope.async {
                    val outVector = createOutVector(i)
                    val numberDir = File(trainDir, i.toString())
                    numberDir.listFiles()[fileNum].let {
                        val arr = (ImageIO.read(it).data.dataBuffer as DataBufferByte).data.map {
                            (it.toDouble() / Byte.MAX_VALUE)
                        }.toDoubleArray()
                        val vector = MatrixUtils.createRealVector(arr)
                        val q = web.train(0.1, vector, outVector)
                        println(q)
//                        val out = web.calcOut(vector)
//                        println("" + outVector.toOutNumber() + "=" + out.toOutNumber())
                    }
//                    }
//                }.forEach {
//                    it.join()
                }
            }

        }
            MongoUtils.saveWeb("numbers2", web)
    }

    fun createOutVector(i: Int) = MatrixUtils.createRealVector(
            (0..9).map { index ->
                if (index == i) 1.0
                else 0.0
            }.toDoubleArray()
    )

    fun RealVector.toOutNumber() = this.maxIndex
}