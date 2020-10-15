package ru.simpleneuro

import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import java.util.*

class NeuronLayer(
        val size: Int,
        val prevLayerSize: Int,
        neurons: List<Neuron>? = null
) {
    val neurons =
            if (neurons != null) {
                if (neurons.size != size) {
                    throw Error("Размер слоя не соответсвует размеру массива")
                }
                neurons
            } else {
                (0 until size).map {
                    Neuron(prevLayerSize)
                }
            }

    fun calcOut(input: RealVector): Single<RealVector> =


    neurons.toObservable().map {
                it.calcOut(input)
            }.toList().map {
                MatrixUtils.createRealVector(it.toDoubleArray())
            }

    fun trainOut(input: RealVector) =
            neurons.toObservable().map {
                it.trainOut(input)
            }.toList().map {
                MatrixUtils.createRealVector(it.map { it.first }.toDoubleArray()) to
                        MatrixUtils.createRealVector(it.map { it.second }.toDoubleArray())
            }

    fun getDeltaForLastLayer(input: RealVector, step: Double, etalon: RealVector, output: RealVector, deriv: RealVector) = Vector(
            neurons.mapIndexed { i, neuron ->
                val delta = (output.getEntry(i) - etalon.getEntry(i)) * deriv.getEntry(i)
                val correctVector = input.map {
                    //todo async
                    -step * it * delta
                }
                val oldWeights = neuron.weights.copy()
                neuron.correctWeights(correctVector)
                neuron.correctB(delta, step)
                Delta(
                        delta,
                        oldWeights
                )
            }
    )

    fun getDelta(deriv: RealVector, nextDeltas: List<Delta>, input: RealVector, step: Double) = Vector(
            neurons.mapIndexed { i, neuron ->
                val nextDeltaSum = nextDeltas.sumByDouble {
                    //todo async
                    it.delta * it.weights.getEntry(i)
                }
                val delta = nextDeltaSum * deriv.getEntry(i)
                val correctVector = input.map {
                    //todo async
                    -step * it * delta
                }
                val oldWeights = neuron.weights.copy()
                neuron.correctWeights(correctVector)
                neuron.correctB(delta, step)

                Delta(
                        delta,
                        oldWeights
                )
            }
    )

    override fun equals(other: Any?) =
            if (other is NeuronLayer) {
                this.prevLayerSize == other.prevLayerSize &&
                        this.size == other.size &&
                        this.neurons == other.neurons
            } else {
                super.equals(other)
            }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + prevLayerSize
        result = 31 * result + neurons.hashCode()
        return result
    }
}