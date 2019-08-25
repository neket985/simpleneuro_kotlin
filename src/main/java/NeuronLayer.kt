import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import java.util.*

class NeuronLayer(
        val size: Int,
        val prevLayerSize: Int
) {
    val neurons = (0 until size).map {
        Neuron(prevLayerSize)
    }

    fun calcOut(input: RealVector): RealVector =
            MatrixUtils.createRealVector(
                    DoubleArray(size) { i ->
                        val neuron = neurons[i]
                        neuron.calcOut(input)
                    }
            )

    fun getDeltasForLastLayer(output: RealVector, step: Double) = Vector(
            neurons.mapIndexed { i, neuron ->
                neuron.deltaForLastLayer(output.getEntry(i), step)
            }
    )

    fun getDeltas(nextDeltas: List<Delta>, step: Double) = Vector(
            neurons.mapIndexed { i, neuron ->
                val nextDeltaSum = nextDeltas.sumByDouble {
                    it.delta * it.weights.getEntry(i)
                }
                neuron.delta(nextDeltaSum, step)
            }
    )
}