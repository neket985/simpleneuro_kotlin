import VectorUtils.times
import VectorUtils.plusAssign
import java.util.*
import kotlin.random.Random

class Neuron(relationsCount: Int, private val alpha: Double) {
    private val weights = Vector(
            (0 until relationsCount).map {
                rand.nextDouble(2.0) - 1
            }.toMutableList()
    )

    private var b = 0.0

    private var lastCalc: Double = 0.0
    private var lastInput: Vector<Double>? = null
    private var lastZ: Double = 0.0


    fun calcOut(input: Vector<Double>): Double {
        lastInput = input
        lastCalc = synoidFun(z(input))
        return lastCalc
    }

    private fun z(input: Vector<Double>): Double {
        lastZ = (input * weights) + b
        return lastZ
    }

    fun deltaForLastLayer(output: Double): Delta {
        val delta = Delta(
                -(output - lastCalc) * derivFun(lastZ),
                weights
        )
        correctWeights(delta.delta)
        correctB(delta.delta)
        return delta
    }

    fun delta(summaryDelta: Double): Delta {
        val delta = Delta(
                summaryDelta * derivFun(lastZ),
                weights
        )
        correctWeights(delta.delta)
        correctB(delta.delta)
        return delta
    }

    private fun correctWeights(delta: Double) {
        if (lastInput != null) {
            val correctVector = Vector(lastInput!!.map {
                -alpha * it * delta
            })
            weights += correctVector
        }
    }

    private fun correctB(delta: Double) {
        b += -alpha * delta
    }

    companion object {
        private fun synoidFun(x: Double) = 1 / (1 + Math.exp(-x))
        private fun derivFun(x: Double) = synoidFun(x).let { it * (1 - it) }
        val rand = Random(1)
    }
}