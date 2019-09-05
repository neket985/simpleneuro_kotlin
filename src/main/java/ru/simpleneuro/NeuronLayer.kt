package ru.simpleneuro

import jeigen.DenseMatrix
import java.lang.Math.exp

class NeuronLayer(
        val size: Int,
        val prevLayerSize: Int,
        neurons: List<Neuron>? = null
) {
    private var lastCalc: DenseVector? = null
    private var lastInput: DenseVector? = null
    private var lastZ: DenseVector? = null

    val neurons =
            if (neurons != null) {
                if (neurons.size != size)
                    throw Error("Размер слоя не соответсвует размеру массива")

                neurons
            } else {
                (0 until size).map {
                    Neuron(prevLayerSize)
                }
            }

    fun calcOut(input: DenseVector): DenseVector {
        lastInput = input
        val layerWMatrix = DenseMatrix(
                Array(size) { i ->
                    neurons[i].weights.values
                }
        )
        val layerBVector = DenseVector(
                DoubleArray(size) { i ->
                    neurons[i].b
                }
        )

        lastCalc = synoidFun(z(input, layerWMatrix, layerBVector))
        return lastCalc!!
    }


    fun getDeltas(nextSumDeltas: List<Double>, output: DenseVector, step: Double): List<Double> {
        val deltas =
                if (nextSumDeltas.isEmpty()) {
                    output.neg().add(lastCalc).mmul(derivFun(lastZ!!).t())
                } else {
                    derivFun(lastZ!!).t()
                }
        val q = deltas.values.mapIndexed { i, delta ->
            val nextSumDelta = if (nextSumDeltas.isNotEmpty()) nextSumDeltas[i] else 1.0
            val normalDelta = nextSumDelta * delta
            val neuron = neurons[i]
            val summaryDelta = neuron.weights.mul(normalDelta)//для отправки в предыдущие слои

            val inputColumn = lastInput!!.col(i)
            val correctwVector = inputColumn.mul(-step * normalDelta) // it * delta *(-step)
            neuron.correctWeights(correctwVector)
            neuron.correctB(normalDelta, step)
            summaryDelta
        }
        return q.mapIndexed { i, _ ->
            q.sumByDouble {
                it[i, 0]
            } + (nextSumDeltas.getOrNull(i) ?: 0.0)
        }
    }

    private fun z(input: DenseVector, weights: DenseMatrix, b: DenseVector): DenseVector {
        lastZ = weights.mmul(input).add(b).toVector()
        return lastZ!!
    }

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

    companion object {
        private fun synoidFun(x: DenseVector, handle: (Double) -> Double = { it }) = DenseVector(
                x.values.map { x ->
                    handle(1 / (1 + exp(-x)))
                }.toTypedArray().toDoubleArray()
        ) //1 / (1 + exp(-x))

        private fun derivFun(x: DenseVector) = synoidFun(x) { it * (1 - it) }

    }

}