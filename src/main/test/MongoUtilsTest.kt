import org.apache.commons.math3.linear.MatrixUtils
import org.junit.Test
import ru.simpleneuro.NeuronWeb
import ru.simpleneuro.mongo.MongoUtils
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MongoUtilsTest {
    private val web = NeuronWeb("test123", 3, listOf(3, 2, 2, 1))

    init {
        web.train(0.1, //случайные вектора для изменения начальных значений весов в нейронах
                MatrixUtils.createRealVector(DoubleArray(3) {
                    1.0
                }),
                MatrixUtils.createRealVector(DoubleArray(1) {
                    1.0

                })
        )
    }

    @Test
    fun saveTest() {
        MongoUtils.saveWeb(web)
        val saved = MongoUtils.loadWeb("test123")

        assertNotNull(saved)
        assertEquals(web, saved)

        deleteTest()
    }

    @Test
    fun deleteTest() {
        val delResult = MongoUtils.deleteWeb("test123")

        val saved = MongoUtils.loadWeb("test123")
        assertNull(saved)
    }
}