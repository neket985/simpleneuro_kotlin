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

    private var lastCalc: Double = 0.0
    private var lastInput: RealVector? = null
    private var lastZ: Double = 0.0


    fun calcOut(input: RealVector): Double {
        lastInput = input
        lastCalc = synoidFun(z(input))
        return lastCalc
    }

    private fun z(input: RealVector): Double {
        lastZ = input.dotProduct(weights) + b
        return lastZ
    }

    fun deltaForLastLayer(output: Double, step: Double): Delta {
        val delta = Delta(
                -(output - lastCalc) * derivFun(lastZ),
                weights
        )
        correctWeights(delta.delta, step)
        correctB(delta.delta, step)
        return delta
    }

    fun delta(summaryDelta: Double, step: Double): Delta {
        val delta = Delta(
                summaryDelta * derivFun(lastZ),
                weights
        )
        correctWeights(delta.delta, step)
        correctB(delta.delta, step)
        return delta
    }

    private fun correctWeights(delta: Double, step: Double) {
        if (lastInput != null) {
            val correctVector = lastInput!!.map {
                -step * it * delta
            }
            weights = weights.add(correctVector)
        }
    }

    private fun correctB(delta: Double, step: Double) {
        b += -step * delta
    }

    companion object {
        private fun synoidFun(x: Double) = 1 / (1 + Math.exp(-x))
        private fun derivFun(x: Double) = synoidFun(x).let { it * (1 - it) }
        val rand = Random(1)
    }
}