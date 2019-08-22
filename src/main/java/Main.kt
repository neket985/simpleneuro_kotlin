import java.lang.Math.exp
import java.util.*
import kotlin.random.Random

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val training_set_inputs = arrayOf(
                Vector(mutableListOf(0.0, 0.0, 1.0)),
                Vector(mutableListOf(1.0, 1.0, 1.0)),
                Vector(mutableListOf(1.0, 0.0, 1.0)),
                Vector(mutableListOf(0.0, 1.0, 1.0))
        )
        val training_set_outputs = arrayOf(0, 1, 1, 0)

        val rand = Random(1)
        val synaptic_weights = Vector(
                (0..2).map {
                    rand.nextDouble(2.0) - 1
                }.toMutableList()
        )

        for (i in 0..10000) {
            for (j in 0..3) {
                val train_input = training_set_inputs[j]
                val input = train_input * synaptic_weights
                val train_otput = training_set_outputs[j]
                val output = synoidFun(input)
                val error = train_otput - output
                synaptic_weights += (train_input * (error * output * (1 - output)))
            }
        }
        println(synoidFun(Vector(mutableListOf(1.0, 0.0, 0.0)) * synaptic_weights))
    }

    private fun synoidFun(x: Number) = 1 / (1 + exp(-x.toDouble()))

    operator fun <T : Number> Vector<T>.times(other: Vector<T>): Double {
        var res5 = 0.0
        var i = 0

        this.forEach { n ->
            res5 += n.toDouble() * other[i].toDouble()
            i++
        }

        return res5
    }

    operator fun Vector<Double>.plusAssign(other: Number) {
        for ((i, n) in this.withIndex()) {
            this[i] += other.toDouble()
        }
    }

    operator fun Vector<Double>.plusAssign(other: Vector<Double>) {
        for ((i, n) in this.withIndex()) {
            this[i] += other[i]
        }
    }

    operator fun Vector<Double>.times(other: Number): Vector<Double> {
        return Vector(
                this.withIndex().map { (i, n) ->
                    n * other.toDouble()
                }
        )
    }

}