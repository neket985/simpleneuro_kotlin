package ru.simpleneuro

import org.apache.commons.math3.linear.MatrixUtils
import ru.simpleneuro.mongo.MongoUtils
import java.awt.Graphics2D
import java.awt.Image.SCALE_FAST
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.File
import javax.imageio.ImageIO


object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val web = MongoUtils.loadWeb("numbers_bottle")!!

        val image = ImageIO.read(
                File("/home/nikita/IdeaProjects/simpleneuro_kotlin/src/main/resources/7.jpg")
        ).getScaledInstance(28, 28, SCALE_FAST).let{
            val bimage = BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB)
            val bGr: Graphics2D = bimage.createGraphics()
            bGr.drawImage(it, 0, 0, null)
            bGr.dispose()
            bimage
        }
        val img = (image.data.dataBuffer as DataBufferInt).data.map {
            (it.toDouble() / Byte.MAX_VALUE)
        }.toDoubleArray().let{
            MatrixUtils.createRealVector(it)
        }
        val q = web.calcOut(img)
        println(q.maxIndex)
    }
}