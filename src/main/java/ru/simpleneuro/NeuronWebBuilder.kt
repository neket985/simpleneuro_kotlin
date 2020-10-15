package ru.simpleneuro

import org.apache.commons.math3.linear.RealVector

class NeuronWebBuilder {
    private var name: String? = null
    private var layersCount: Int? = null
    private var connectionsDimension: List<Int>? = null
    private var layers: () -> List<NeuronLayer>? = { null }

    fun name(name: String) = apply {
        this.name = name
    }

    fun customLayers(layersCount: Int, connectionsDimension: List<Int>) = apply {
        this.layersCount = layersCount
        this.connectionsDimension = connectionsDimension
    }

    /** Методы для генерации наиболее распространенных структур нейронных сетей todo дополнить*/

    fun rectangle(layersCount: Int, dimension: Int) =
            customLayers(
                    layersCount,
                    (0..layersCount + 1).map { dimension }
            )

    /** ------------------------------------------------------------------------------------------------------------- */

    fun initialFillLayers(layers: List<NeuronLayer>) = apply {
        this.layers = { layers }
    }

    fun initialFillLayers(map: (Int) -> NeuronLayer) = initialFillLayers(
            (0..this.layersCount!!).map { i ->
                map(i)
            }
    )

    fun initialFillLayers(map: (Int, Int, Int) -> Neuron) = initialFillLayers { i ->
        val dimension = connectionsDimension!![i + 1]
        val prevDimension = connectionsDimension!![i]
        NeuronLayer(
                dimension,
                prevDimension,
                (0..dimension).map { j ->
                    map(prevDimension, i, j)
                }
        )
    }

    fun initialFillLayers(map: (Int, Int) -> Pair<RealVector, Double>) = initialFillLayers { prevDimension, i, j ->
        val (weights, b) = map(i, j)
        Neuron(prevDimension, weights, b)
    }

    fun build(): NeuronWeb {
        if (layersCount == null || connectionsDimension == null || name == null) throw IllegalArgumentException("Layers count must be initialized before")

        if (layersCount != connectionsDimension!!.size - 1) throw IllegalArgumentException("Connections dimension size must be equals layers count + 1")
        if (layersCount!! < 1) throw IllegalArgumentException("Layers count must be grater than 0")
        connectionsDimension!!.forEach {
            if (it < 1) throw IllegalArgumentException("All dimensions must be grater than 0")
        }
        val initLayers = layers()
        if (initLayers != null && initLayers.size != layersCount) throw IllegalArgumentException("Layers count is not equal to initial fill layers size")

        return NeuronWeb(name!!, layersCount!!, connectionsDimension!!, initLayers)
    }

    companion object {
        fun builder(): NeuronWebBuilder = NeuronWebBuilder()
    }
}