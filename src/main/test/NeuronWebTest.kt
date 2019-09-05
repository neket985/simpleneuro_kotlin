import jeigen.DenseMatrix
import org.junit.Test
import ru.simpleneuro.DenseVector
import ru.simpleneuro.NeuronWeb

class NeuronWebTest {

    @Test
    fun simpleTest() {
        val training_set_inputs =
                arrayOf(
                        DenseVector(arrayOf(0.0, 0.0, 1.0).toDoubleArray()),
                        DenseVector(arrayOf(1.0, 1.0, 1.0).toDoubleArray()),
                        DenseVector(arrayOf(1.0, 0.0, 1.0).toDoubleArray()),
                        DenseVector(arrayOf(0.0, 1.0, 1.0).toDoubleArray())
                )

        val training_set_outputs =
                arrayOf(
                        DenseVector(arrayOf(0.0).toDoubleArray()),
                        DenseVector(arrayOf(1.0).toDoubleArray()),
                        DenseVector(arrayOf(1.0).toDoubleArray()),
                        DenseVector(arrayOf(0.0).toDoubleArray())
                )


        val web = NeuronWeb("test1", 4, listOf(3,
                2,
                2,
                2,
                1
        ))

        training_set_inputs.forEachIndexed { i, input ->
            val output = training_set_outputs[i]
            web.train(0.1, input, output)
        }

        println(web.calcOut(DenseVector(arrayOf(1.0, 0.0, 0.0).toDoubleArray())))
//        assert(web.calcOut(DenseMatrix(arrayOf(arrayOf(1.0, 0.0, 0.0).toDoubleArray()))).get(0) > 0.9)
    }
}