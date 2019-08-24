import java.util.*

class NeuronWeb(alpha: Double, val layersCount: Int, val layersSizes: List<Int>) {

    init {
        if (layersSizes.size != layersCount + 1) throw Error("Количество размеров слоев не соответсвует количеству слоев")
    }


    val layers = layersSizes.mapIndexedNotNull { i, size ->
        if (i == 0) {
            null
        } else {
            val prevSize = layersSizes[i - 1]
            NeuronLayer(size, alpha, prevSize)
        }
    }

    fun calcOut(input: Vector<Double>): Vector<Double> {
        val iter = layers.iterator()
        var result: Vector<Double> = input
        while (iter.hasNext()) {
            val layer = iter.next()
            result = layer.calcOut(result)
        }
        return result
    }

    fun train(input: Vector<Double>, output: Vector<Double>) {
        calcOut(input)
        val lastLayer = layers.last()
        var nextDeltas = lastLayer.getDeltasForLastLayer(output)
        if (layersCount > 1) {
            layers.reversed().subList(1, layersCount).forEach { layer ->
                nextDeltas = layer.getDeltas(nextDeltas)
            }
        }
    }
}