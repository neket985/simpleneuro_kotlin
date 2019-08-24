import java.util.*

class NeuronLayer(
        private val size: Int,
        private val prevLayerSize: Int
) {
    val neurons = Vector<Neuron>((0 until size).map {
        Neuron(prevLayerSize)
    }.toMutableList())

    fun calcOut(input: Vector<Double>): Vector<Double> =
            Vector(neurons.map { neuron ->
                neuron.calcOut(input)
            })

    fun getDeltasForLastLayer(output: Vector<Double>) = Vector(
            neurons.mapIndexed { i, neuron ->
                neuron.deltaForLastLayer(output[i])
            }
    )

    fun getDeltas(nextDeltas: Vector<Delta>) = Vector(
            neurons.mapIndexed { i, neuron ->
                val nextDeltaSum = nextDeltas.sumByDouble {
                    it.delta * it.weights[i]
                }
                neuron.delta(nextDeltaSum)
            }
    )

}