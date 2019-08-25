import org.apache.commons.math3.linear.MatrixUtils
import org.junit.Test
import java.util.*

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

        val web = NeuronWeb(1, listOf(3, 1))

        web.trainAll(0.1, 1000, training_set_inputs.toList(), training_set_outputs.toList())

        assert(web.calcOut(MatrixUtils.createRealVector(arrayOf(1.0, 0.0, 0.0).toDoubleArray())).getEntry(0) > 0.9)
    }
}