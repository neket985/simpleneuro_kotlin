package ru.simpleneuro

import org.apache.commons.math3.linear.RealVector
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.Logger
import kotlin.concurrent.read
import kotlin.concurrent.write

class NeuronWeb(val name: String, val layersCount: Int, val connectionsDimension: List<Int>, layers: List<NeuronLayer>? = null) {
    private val meter = ApplicationVars.metrics.meter("numbers.train.meter")
    private val success = ApplicationVars.metrics.histogram("numbers.success.histo")
    private val distanceHist = ApplicationVars.metrics.histogram("numbers.train.distance.histogram")
    private val inputSize = connectionsDimension.first()
    private val lock = ReentrantReadWriteLock()

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
        lock.read {
            if (input.dimension != inputSize) throw Error("Входной вектор по размерам не совпадает с ожидаемым")

            val iter = layers.iterator()
            var result: RealVector = input
            while (iter.hasNext()) {
                val layer = iter.next()
                result = layer.calcOut(result).blockingGet()
            }
            return result
        }
    }

    fun train(step: Double, input: RealVector, etalon: RealVector, result: Int): Double {
        lock.write {
            meter.mark()
            if (input.dimension != inputSize) throw Error("Входной вектор по размерам не совпадает с ожидаемым")

            val iter = layers.iterator()
            val (_, output) =
                    if (iter.hasNext()) {
                        recursiveTrain(iter, step, input, etalon)
                    } else throw IllegalStateException("Layers is empty")

            if(output.maxIndex == result){
                success.update(1)
            }else {
                success.update(0)
            }

            val dist = etalon.getDistance(output)
            distanceHist.update((1000000 * dist).toLong())
            return dist
        }
    }

    private fun recursiveTrain(iter: Iterator<NeuronLayer>, step: Double, input: RealVector, etalon: RealVector): Pair<Vector<Delta>, RealVector> {
        val layer = iter.next()
        val (out, deriv) = layer.trainOut(input).blockingGet()
        return if (iter.hasNext()) {
            val (nextDeltas, lastOutput) = recursiveTrain(iter, step, out, etalon)
            layer.getDelta(deriv, nextDeltas, input, step) to lastOutput
        } else {
            val deltas = layer.getDeltaForLastLayer(input, step, etalon, out, deriv)
            deltas to out
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