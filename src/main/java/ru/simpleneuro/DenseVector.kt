package ru.simpleneuro

import jeigen.DenseMatrix


class DenseVector(array: DoubleArray) : DenseMatrix(array.map { doubleArrayOf(it) }.toTypedArray()) {
}

fun DenseMatrix.toVector() =
        if (this.cols == 1) DenseVector(this.values)
        else throw Error("Это не вектор :(")
