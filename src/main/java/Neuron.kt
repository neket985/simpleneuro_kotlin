import java.util.*
import kotlin.random.Random
import VectorUtils.times
import VectorUtils.plusAssign

class Neuron(relationsCount: Int) {

    private val weights = Vector(
            (0 until relationsCount).map {
                rand.nextDouble(2.0) - 1
            }.toMutableList()
    )

    private val b = 0.0

    fun train(input: Vector<Number>, trainOutput: Double){
                val output = synoidFun(input * weights)
                val error = trainOutput - output
                weights += (input * (error * output * (1 - output)))
    }

    fun calcOut(input: Vector<Number>) = synoidFun(Vector(mutableListOf(1.0, 0.0, 0.0)) * weights + b)

    companion object {
        private fun synoidFun(x: Number) = 1 / (1 + Math.exp(-x.toDouble()))
        val rand = Random(1)
    }
}