import java.util.*

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val training_set_inputs = arrayOf(
                Vector(mutableListOf(0.0, 0.0, 1.0)),
                Vector(mutableListOf(1.0, 1.0, 1.0)),
                Vector(mutableListOf(1.0, 0.0, 1.0)),
                Vector(mutableListOf(0.0, 1.0, 1.0))
        )
        val training_set_outputs = arrayOf(
                Vector(mutableListOf(0.0)),
                Vector(mutableListOf(1.0)),
                Vector(mutableListOf(1.0)),
                Vector(mutableListOf(0.0))
        )

        val web = NeuronWeb(0.1, 3, listOf(3, 2, 2, 1))

        for (i in 0..10000) {
            for (j in 0..3) {
                val train_input = training_set_inputs[j]
                val train_otput = training_set_outputs[j]
                web.train(train_input, train_otput)
            }
            println(web.calcOut(Vector(mutableListOf(1.0, 0.0, 0.0))))
        }
    }
}