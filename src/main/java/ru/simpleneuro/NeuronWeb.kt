package ru.simpleneuro

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import java.util.logging.Logger

class NeuronWeb(val name: String, val layersCount: Int, val connectionsDimension: List<Int>, layers: List<NeuronLayer>? = null) {
    private val meter = ApplicationVars.metrics.meter("$name.train.meter")
    private val distanceHist = ApplicationVars.metrics.histogram("$name.train.distance.histogram")
    private val inputSize = connectionsDimension.first()
    private val lock = Object()

    init {
        if (connectionsDimension.size != layersCount + 1)
            throw Error("Количество соединений не соответсвует количеству слоев")
    }

    val layers =
            if (layers != null) {
                if (connectionsDimension.size != layers.size + 1)
                    throw Error("Количество соединений не соответсвует списку слоев")
                layers
            } else {
                connectionsDimension.mapIndexedNotNull { i, size ->
                    if (i == 0) {
                        null
                    } else {
                        val prevSize = connectionsDimension[i - 1]
                        NeuronLayer(size, prevSize)
                    }
                }
            }

    fun calcOut(input: RealVector): RealVector {
        synchronized(lock) {
            if (input.dimension != inputSize) throw Error("Входной вектор по размерам не совпадает с ожидаемым")

            val iter = layers.iterator()
            var result: RealVector = input
            while (iter.hasNext()) {
                val layer = iter.next()
                result = layer.calcOut(result)
            }
            return result
        }
    }

    fun train(step: Double, input: RealVector, output: RealVector): Double {
        synchronized(lock) {
            meter.mark()
            val out = calcOut(input)

            val lastLayer = layers.last()
            var nextDeltas = lastLayer.getDeltasForLastLayer(output, step)
            if (layersCount > 1) {
                layers.reversed().subList(1, layersCount).forEach { layer ->
                    nextDeltas = layer.getDeltas(nextDeltas, step)
                }
            }
            val dist = output.getDistance(out)
            distanceHist.update((1000000 * dist).toLong())
            return dist
        }
    }

    fun trainAll(step: Double, iterations: Int, input: List<RealVector>, output: List<RealVector>) {
        synchronized(lock) {
            meter.mark(input.size.toLong())
            if (input.size != output.size) throw Error("Размер входного массива не соответствует размеру выходного")

            for (i in 0..iterations) {
                for (j in 0 until input.size) {
                    val train_input = input[j]
                    val train_otput = output[j]
                    train(step, train_input, train_otput)
                }
                println(calcOut(MatrixUtils.createRealVector(arrayOf(1.0, 0.0, 0.0).toDoubleArray())).toString())
//            logger.fine(calcOut(MatrixUtils.createRealVector(arrayOf(1.0, 0.0, 0.0).toDoubleArray()))
            }
        }
    }

    override fun equals(other: Any?) =
            if (other is NeuronWeb) {
                this.layersCount == other.layersCount &&
                        this.inputSize == other.inputSize &&
                        this.connectionsDimension == other.connectionsDimension &&
                        this.layers == other.layers
            } else {
                super.equals(other)
            }

    override fun hashCode(): Int {
        var result = layersCount
        result = 31 * result + connectionsDimension.hashCode()
        result = 31 * result + inputSize
        result = 31 * result + layers.hashCode()
        return result
    }


    companion object {
        private val logger = Logger.getLogger(this::class.java.name)
        private const val minStep = 0.00001
    }
}