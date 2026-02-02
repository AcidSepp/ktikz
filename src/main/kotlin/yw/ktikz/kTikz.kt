package yw.ktikz.yw.ktikz

import java.awt.Color
import java.io.PrintWriter

const val LINE_WIDTH = 0.05
const val POINT_SIZE = 0.015

enum class Shape {
    CIRCLE, BOX, X
}

fun PrintWriter.drawPoint(
    x: Double,
    y: Double,
    shape: Shape = Shape.CIRCLE,
    size: Double = POINT_SIZE,
    color: Color = Color.BLACK,
) {
    when (shape) {
        Shape.CIRCLE -> {
            drawCircle(x, y, size / 2.0, color)
        }

        Shape.BOX -> {
            val xLeft = x - (size / 2.0)
            val yLeft = y - (size / 2.0)
            val xRight = x + (size / 2.0)
            val yRight = y + (size / 2.0)
            this.drawRect(xLeft, yLeft, xRight, yRight, strokeColor = color)
        }

        Shape.X -> {
            val xDownLeft = x - (size / 2.0)
            val yDownLeft = y - (size / 2.0)
            val xUpRight = x + (size / 2.0)
            val yUpRight = y + (size / 2.0)
            val xUpLeft = x - (size / 2.0)
            val yUpLeft = y + (size / 2.0)
            val xDownRight = x + (size / 2.0)
            val yDownRight = y - (size / 2.0)
            this.drawLine(xDownLeft, yDownLeft, xUpRight, yUpRight, color = color)
            this.drawLine(xUpLeft, yUpLeft, xDownRight, yDownRight, color = color)
        }
    }
}

fun PrintWriter.drawLine(
    xStart: Double,
    yStart: Double,
    xEnd: Double,
    yEnd: Double,
    color: Color = Color.BLACK,
    lineWidth: Double = LINE_WIDTH,
    dotted: Boolean = false,
) {
    val pattern = if (dotted) ", dash pattern={on 0.5 off 0.5}" else ""
    this.println("\\draw[line width=$lineWidth, color=${color.toColorString()}$pattern] (${xStart},${yStart}) -- (${xEnd},${yEnd});")
}

fun PrintWriter.drawText(
    x: Double,
    y: Double,
    text: String,
    anchor: String = "north",
    scale: Double = 0.25,
    color: Color = Color.BLACK,
) {
    val opacityString = if (color.alpha != 255) ", opacity=${color.toAlphaString()}" else ""
    this.println("\\node[scale=$scale, color=${color.toColorString()}, anchor=$anchor, align=center$opacityString] at ($x,${y}) {\\tiny $text};")
}

fun PrintWriter.drawRect(
    xStart: Double,
    yStart: Double,
    xEnd: Double,
    yEnd: Double,
    strokeColor: Color = Color.BLACK,
    lineWidth: Double = LINE_WIDTH,
    fillColor: Color? = null,
) {
    this.println("${getShapeString(strokeColor, fillColor, lineWidth)} ($xStart,$yStart) rectangle ($xEnd,$yEnd);")
}

fun PrintWriter.drawCircle(
    x: Double,
    y: Double,
    radius: Double,
    strokeColor: Color = Color.BLACK,
    lineWidth: Double = LINE_WIDTH,
    fillColor: Color? = null
) {
    this.println("${getShapeString(strokeColor, fillColor, lineWidth)} ($x,$y) circle ($radius);")
}

private fun getShapeString(strokeColor: Color, fillColor: Color?, lineWidth: Double): String {
    val strokeOpacityString = if (strokeColor.alpha != 255) ", opacity=${strokeColor.toAlphaString()}" else ""
    val strokeColorString = "color=${strokeColor.toColorString()}$strokeOpacityString"

    val fillOpacity =
        if (fillColor != null && strokeColor.alpha != 255) ", fill opacity=${fillColor.toAlphaString()}" else ""
    val fillString = fillColor?.let { ", fill=${fillColor.toColorString()}$fillOpacity" } ?: ""
    val fillPrefix = fillColor?.let { "fill" } ?: ""

    val shapeString = "\\${fillPrefix}draw[$strokeColorString, line width=$lineWidth$fillString]"
    return shapeString
}

private fun Color.toColorString() = "{rgb,255:red,$red; green,$green; blue,$blue}"

private fun Color.toAlphaString() = "${alpha.toDouble().minMaxNormalize(0.0, 255.0)}"

private fun Double.minMaxNormalize(min: Double, max: Double) = (this - min) / (max - min)