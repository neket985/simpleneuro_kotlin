package ru.simpleneuro

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.util.MathUtils
import java.util.logging.Logger

class NeuronWeb(val layersCount: Int, val layersSizes: List<Int>, layers: List<NeuronLayer>? = null) {
    private val inputSize = layersSizes.first()
    private val lock = Object()

    init {
        if (layersSizes.size != layersCount + 1)
            throw Error("Количество размеров слоев не соответсвует количеству слоев")
    }

    val layers =
            if (layers != null) {
                if (layersSizes.size != layers.size + 1)
                    throw Error("Количество размеров слоев не соответсвует списку слоев")
                layers
            } else {
                layersSizes.mapIndexedNotNull { i, size ->
                    if (i == 0) {
                        null
                    } else {
                        val prevSize = layersSizes[i - 1]
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

    fun train(step: Double, input: RealVector, output: RealVector): Double{
        synchronized(lock) {
            val out = calcOut(input)

            val lastLayer = layers.last()
            var nextDeltas = lastLayer.getDeltasForLastLayer(output, step)
            if (layersCount > 1) {
                layers.reversed().subList(1, layersCount).forEach { layer ->
                    nextDeltas = layer.getDeltas(nextDeltas, step)
                }
            }
            return output.getDistance(out)
        }
    }

    fun trainAll(step: Double, iterations: Int, input: List<RealVector>, output: List<RealVector>) {
        synchronized(lock) {
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
                        this.layersSizes == other.layersSizes &&
                        this.layers == other.layers
            } else {
                super.equals(other)
            }

    override fun hashCode(): Int {
        var result = layersCount
        result = 31 * result + layersSizes.hashCode()
        result = 31 * result + inputSize
        result = 31 * result + layers.hashCode()
        return result
    }


    companion object {
        private val logger = Logger.getLogger(this::class.java.name)
        private const val minStep = 0.00001
    }
}