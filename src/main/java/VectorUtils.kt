import java.util.*

object VectorUtils {
    operator fun <T : Number, R : Number> Vector<T>.times(other: Vector<R>): Double {
        var res = 0.0
        this.withIndex().map { (i, n) ->
            res += n.toDouble() * other[i].toDouble()
        }
        return res
    }

    operator fun Vector<Double>.plusAssign(other: Number) {
        for ((i, n) in this.withIndex()) {
            this[i] += other.toDouble()
        }
    }

    operator fun Vector<Double>.plusAssign(other: Vector<Double>) {
        for ((i, n) in this.withIndex()) {
            this[i] += other[i]
        }
    }

    operator fun <T: Number> Vector<T>.times(other: Number): Vector<Double> {
        return Vector(
                this.withIndex().map { (i, n) ->
                    n.toDouble() * other.toDouble()
                }
        )
    }
}