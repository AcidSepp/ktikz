package yw.tikz

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import yw.ktikz.yw.ktikz.drawCircle
import yw.ktikz.yw.ktikz.drawLine
import yw.ktikz.yw.ktikz.drawRect
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.nio.file.Path
import kotlin.io.path.writeText

class KTikzTest {

    @Test
    fun test_drawSimpleLine(@TempDir tempDir: Path) {
        val pos = ByteArrayOutputStream()
        val printWriter = PrintWriter(pos)
        printWriter.drawLine(0.0, 0.0, 1.0, 1.0)
        printWriter.flush()
        assertThat(
            pos.toString().trim()
        ).isEqualTo("\\draw[line width=0.05, color={rgb,255:red,0; green,0; blue,0}] (0.0,0.0) -- (1.0,1.0);")
        assertLatex(pos, tempDir)
    }

    @Test
    fun test_drawComplexLine(@TempDir tempDir: Path) {
        val pos = ByteArrayOutputStream()
        val printWriter = PrintWriter(pos)
        val color = Color(100, 101, 102)
        printWriter.drawLine(0.0, 0.0, 1.0, 1.0, color, 0.1, true)
        printWriter.flush()
        assertThat(
            pos.toString().trim()
        ).isEqualTo("\\draw[line width=0.1, color={rgb,255:red,100; green,101; blue,102}, dash pattern={on 0.5 off 0.5}] (0.0,0.0) -- (1.0,1.0);")
        assertLatex(pos, tempDir)
    }

    @Test
    fun test_drawCircle(@TempDir tempDir: Path) {
        val pos = ByteArrayOutputStream()
        val printWriter = PrintWriter(pos)
        printWriter.drawCircle(0.0, 0.0, 0.5)
        printWriter.flush()
        assertThat(
            pos.toString().trim()
        ).isEqualTo("\\draw[line width=0.05, color={rgb,255:red,0; green,0; blue,0}] (0.0,0.0) circle (0.5);")
        assertLatex(pos, tempDir)
    }

    @Test
    fun test_drawComplexCircle(@TempDir tempDir: Path) {
        val pos = ByteArrayOutputStream()
        val printWriter = PrintWriter(pos)
        val color = Color(100, 101, 102)
        printWriter.drawCircle(0.0, 0.0, 0.5, color)
        printWriter.flush()
        assertThat(
            pos.toString().trim()
        ).isEqualTo("\\draw[line width=0.05, color={rgb,255:red,100; green,101; blue,102}] (0.0,0.0) circle (0.5);")
        assertLatex(pos, tempDir)
    }

    @Test
    fun test_drawRect(@TempDir tempDir: Path) {
        val pos = ByteArrayOutputStream()
        val printWriter = PrintWriter(pos)
        printWriter.drawRect(0.0, 0.0, 1.0, 1.0)
        printWriter.flush()
        assertThat(
            pos.toString().trim()
        ).isEqualTo("\\draw[color={rgb,255:red,0; green,0; blue,0}, line width=0.05] (0.0,0.0) rectangle (1.0,1.0);")
        assertLatex(pos, tempDir)
    }

    @Test
    fun test_drawComplexRect(@TempDir tempDir: Path) {
        val pos = ByteArrayOutputStream()
        val printWriter = PrintWriter(pos)
        val strokeColor = Color(100, 101, 102, 102)
        val fillColor = Color(99, 98, 97, 51)
        printWriter.drawRect(0.0, 0.0, 1.0, 1.0, strokeColor = strokeColor, fillColor = fillColor, lineWidth = 0.1)
        printWriter.flush()
        assertLatex(pos, tempDir)
        assertThat(
            pos.toString().trim()
        ).isEqualTo("\\filldraw[color={rgb,255:red,100; green,101; blue,102}, opacity=0.4, line width=0.1, fill={rgb,255:red,99; green,98; blue,97}, fill opacity=0.2] (0.0,0.0) rectangle (1.0,1.0);")
    }

    private fun assertLatex(pos: ByteArrayOutputStream, tempDir: Path) {
        val latexCode = """
                \documentclass{standalone}
                \usepackage{adjustbox}
                \usepackage{tikz}
    
                \begin{document}
                    \begin{tikzpicture}
                        ${pos.toString().trim()}
                    \end{tikzpicture}
                \end{document}
            """.trimIndent()
        println(latexCode)

        val latexFilePath = tempDir.resolve("output.tex").apply {
            writeText(latexCode)
        }.toAbsolutePath().toString()

        val start =
            ProcessBuilder().command("pdflatex", "-interaction=nonstopmode", latexFilePath).directory(tempDir.toFile())
                .start()
        start.waitFor()
        assertThat(start.exitValue()).isEqualTo(0)
    }
}