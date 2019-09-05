package ru.simpleneuro

import jeigen.DenseMatrix
import org.apache.commons.math3.linear.RealVector
import kotlin.math.exp
import kotlin.random.Random

class Neuron(val relationsCount: Int, weights: DenseMatrix? = null, b: Double? = null) {
    var weights =
            if (weights != null) {
                if (weights.cols != relationsCount)
                    throw Error("Размер вектора весов не соответствует числу входных связей нейрона")
                else if (weights.rows != 1)
                    throw Error("Входные веса не являются вектором")

                weights
            } else {
                DenseMatrix(
                        arrayOf(
                                DoubleArray(relationsCount) {
                                    rand.nextDouble(2.0) - 1
                                }
                        )
                )
            }

    var b = b ?: 0.0

    fun correctWeights(correctVector: DenseMatrix) {
        weights = weights.add(correctVector)
    }

    fun correctB(delta: Double, step: Double) {
        b += -step * delta
    }

    override fun equals(other: Any?) =
            if (other is Neuron) {
                this.relationsCount == other.relationsCount &&
                        this.b == other.b &&
                        this.weights == other.weights
            } else {
                super.equals(other)
            }

    override fun hashCode(): Int {
        var result = relationsCount
        result = 31 * result + (weights.hashCode())
        result = 31 * result + b.hashCode()
        return result
    }

    companion object {
        private fun synoidFun(x: Double) = 1 / (1 + exp(-x))
        private fun synoidFun(x: RealVector) = x.map { 1 / (1 + exp(-it)) }
        private fun derivFun(x: Double) = synoidFun(x).let { it * (1 - it) }
        private fun derivFun(x: RealVector) = synoidFun(x).map { it * (1 - it) }.toArray().sum()
        val rand = Random(1)
    }
}