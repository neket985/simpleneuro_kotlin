import java.util.*

class NeuronWeb(val layersCount: Int, val layerSize: Int) {
    //todo
    val layers = (0 until layersCount).map {
        NeuronLayer(layerSize, layerSize)
    }

    fun calcOut(input: Vector<Double>): Vector<Double> {
        val iter = layers.iterator()
        var result: Vector<Double> = input
        while (!iter.hasNext()) {
            val layer = iter.next()
            result = layer.calcOut(result)
        }
        return result
    }

    fun train(input: Vector<Double>, output: Vector<Double>) {
        calcOut(input)
        val lastLayer = layers.last()
        var nextDeltas = lastLayer.getDeltasForLastLayer(output)
        layers.reversed().subList(1, layersCount - 2).forEach { layer ->
            nextDeltas = layer.getDeltas(nextDeltas)
        }
    }
}