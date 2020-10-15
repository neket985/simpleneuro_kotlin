import org.apache.commons.math3.linear.MatrixUtils
import org.junit.Test
import ru.simpleneuro.NeuronWeb

class NeuronWebTest {

    @Test
    fun simpleTest() {
        val training_set_inputs = arrayOf(
                MatrixUtils.createRealVector(arrayOf(0.0, 0.0, 1.0).toDoubleArray()),
                MatrixUtils.createRealVector(arrayOf(1.0, 1.0, 1.0).toDoubleArray()),
                MatrixUtils.createRealVector(arrayOf(1.0, 0.0, 1.0).toDoubleArray()),
                MatrixUtils.createRealVector(arrayOf(0.0, 1.0, 1.0).toDoubleArray())
        )
        val training_set_outputs = arrayOf(
                MatrixUtils.createRealVector(arrayOf(0.0).toDoubleArray()),
                MatrixUtils.createRealVector(arrayOf(1.0).toDoubleArray()),
                MatrixUtils.createRealVector(arrayOf(1.0).toDoubleArray()),
                MatrixUtils.createRealVector(arrayOf(0.0).toDoubleArray())
        )

        val web = NeuronWeb("test", 4, listOf(3,
                2,
                2,
                2,
                1
        ))

        (0..100000).forEach {
            training_set_inputs.forEachIndexed { index, realVector ->
                web.train(0.1, realVector, training_set_outputs.get(index))
            }
        }

        val out = web.calcOut(MatrixUtils.createRealVector(arrayOf(1.0, 0.0, 0.0).toDoubleArray())).getEntry(0)
        println(out)
        assert(out > 0.9)
    }
}