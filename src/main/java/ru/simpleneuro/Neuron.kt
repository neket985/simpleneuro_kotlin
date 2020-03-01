package ru.simpleneuro

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import kotlin.random.Random

class Neuron(val relationsCount: Int, weights: RealVector? = null, b: Double? = null) {
    var weights =
            if (weights != null) {
                if (weights.dimension != relationsCount)
                    throw Error("Размер вектора весов не соответствует числу входных связей нейрона")
                weights
            } else {
                MatrixUtils.createRealVector(
                        DoubleArray(relationsCount
                        ) {
                            rand.nextDouble(2.0) - 1
                        }
                )
            }
    var b = b ?: 0.0

    fun calcOut(input: RealVector): Double {
        return synoidFun(z(input))
    }

    fun trainOut(input: RealVector): Pair<Double, Double> {
        val z = z(input)
        return synoidFun(z) to derivFun(z)
    }

    private fun z(input: RealVector): Double = input.dotProduct(weights) + b

    fun correctWeights(correctVector: RealVector) {
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
        result = 31 * result + (weights?.hashCode() ?: 0)
        result = 31 * result + b.hashCode()
        return result
    }

    companion object {
        fun synoidFun(x: Double) = 1 / (1 + Math.exp(-x))
        private fun derivFun(x: Double) = synoidFun(x).let { it * (1 - it) }
        val rand = Random(1)
    }
}