package ru.simpleneuro

import jeigen.DenseMatrix
import java.util.logging.Logger

class NeuronWeb(val name: String, val layersCount: Int, val layersSizes: List<Int>, layers: List<NeuronLayer>? = null) {
    private val meter = ApplicationVars.metrics.meter("$name.train.meter")
    private val distanceHist = ApplicationVars.metrics.histogram("$name.train.distance.histogram")
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

    fun calcOut(input: DenseVector): DenseMatrix {
        synchronized(lock) {
            if (input.rows != inputSize) throw Error("Входной вектор по размерам не совпадает с ожидаемым")

            val iter = layers.iterator()
            var result: DenseVector = input
            while (iter.hasNext()) {
                val layer = iter.next()
                result = layer.calcOut(result)
            }
            return result
        }
    }

    fun train(step: Double, input: DenseVector, output: DenseVector): Double {
        synchronized(lock) {
            meter.mark(input.rows.toLong())
            val out = calcOut(input)

            val lastLayer = layers.last()
            var nextDeltas = lastLayer.getDeltas(listOf(), output, step)
            if (layersCount > 1) {
                layers.reversed().subList(1, layersCount).forEach { layer ->
                    nextDeltas = layer.getDeltas(nextDeltas, output, step)
                }
            }
//            val dist = output.di(out)
//            distanceHist.update((1000000 * dist).toLong())
            return 0.0
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