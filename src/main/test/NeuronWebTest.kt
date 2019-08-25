import org.junit.Test
import java.util.*

class NeuronWebTest {

    @Test
    fun simpleTest() {
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

        val web = NeuronWeb(1, listOf(3, 1))

        web.trainAll(0.1, 1000, training_set_inputs.toList(), training_set_outputs.toList())

        assert(web.calcOut(Vector(mutableListOf(1.0, 0.0, 0.0))).firstElement() > 0.9)
    }
}