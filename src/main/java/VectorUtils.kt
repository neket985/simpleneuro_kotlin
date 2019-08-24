import java.util.*

object VectorUtils {
    operator fun Vector<Double>.times(other: Vector<Double>): Double {
        if(this.size!= other.size) throw Error("Vectors are not comparable")
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
        if(this.size!= other.size) throw Error("Vectors are not comparable")
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