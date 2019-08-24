import java.util.*

object VectorUtils {
    operator fun Vector<Double>.times(other: Vector<Double>): Double {
        var res = 0.0
        this.withIndex().map { (i, n) ->
            res += n * other[i]
        }
        return res
    }

    operator fun Vector<Double>.plusAssign(other: Double) {
        for ((i, n) in this.withIndex()) {
            this[i] += other
        }
    }

    operator fun Vector<Double>.plusAssign(other: Vector<Double>) {
        for ((i, n) in this.withIndex()) {
            this[i] += other[i]
        }
    }

    operator fun Vector<Double>.times(other: Double): Vector<Double> {
        return Vector(
                this.withIndex().map { (i, n) ->
                    n * other
                }
        )
    }
}