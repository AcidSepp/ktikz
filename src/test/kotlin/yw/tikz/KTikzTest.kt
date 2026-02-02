package yw.tikz

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import yw.ktikz.yw.ktikz.drawLine
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