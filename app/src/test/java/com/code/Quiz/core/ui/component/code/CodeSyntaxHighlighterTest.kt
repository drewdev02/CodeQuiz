package com.code.Quiz.core.ui.component.code

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import com.code.Quiz.core.theme.SuccessMint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CodeSyntaxHighlighterTest {

    private fun findSpanStyleFor(code: String, target: String): SpanStyle? {
        val highlighted = CodeSyntaxHighlighter.highlight(code)
        val start = code.indexOf(target)
        if (start == -1) return null
        val end = start + target.length
        return highlighted.spanStyles.find { it.start == start && it.end == end }?.item
    }

    @Test
    fun testHighlighting_keywords() {
        val code = "val status = if (isOnline) true else false"
        val highlighted = CodeSyntaxHighlighter.highlight(code)
        assertEquals(code, highlighted.text)

        val valSpan = findSpanStyleFor(code, "val")
        assertNotNull("val keyword must have span style", valSpan)
        assertEquals(CodeSyntaxHighlighter.KeywordColor, valSpan?.color)

        val ifSpan = findSpanStyleFor(code, "if")
        assertNotNull("if keyword must have span style", ifSpan)
        assertEquals(CodeSyntaxHighlighter.KeywordColor, ifSpan?.color)

        val elseSpan = findSpanStyleFor(code, "else")
        assertNotNull("else keyword must have span style", elseSpan)
        assertEquals(CodeSyntaxHighlighter.KeywordColor, elseSpan?.color)

        val falseSpan = findSpanStyleFor(code, "false")
        assertNotNull("false keyword must have span style", falseSpan)
        assertEquals(CodeSyntaxHighlighter.KeywordColor, falseSpan?.color)
    }

    @Test
    fun testHighlighting_strings() {
        val code = "val message = \"Conectado\""
        val highlighted = CodeSyntaxHighlighter.highlight(code)
        assertEquals(code, highlighted.text)

        val stringSpan = findSpanStyleFor(code, "\"Conectado\"")
        assertNotNull("String literal must have span style", stringSpan)
        assertEquals(SuccessMint, stringSpan?.color)
    }

    @Test
    fun testHighlighting_comments_types_functions_numbers() {
        val code = """
            // This is a test comment
            fun calculate(score: Int): Boolean {
                return score > 42
            }
        """.trimIndent()

        val highlighted = CodeSyntaxHighlighter.highlight(code)
        assertEquals(code, highlighted.text)

        val commentSpan = findSpanStyleFor(code, "// This is a test comment")
        assertNotNull("Comment must have span style", commentSpan)
        assertEquals(CodeSyntaxHighlighter.CommentColor, commentSpan?.color)
        assertEquals(FontStyle.Italic, commentSpan?.fontStyle)

        val typeSpan = findSpanStyleFor(code, "Int")
        assertNotNull("Type Int must have span style", typeSpan)
        assertEquals(CodeSyntaxHighlighter.TypeColor, typeSpan?.color)

        val functionSpan = findSpanStyleFor(code, "calculate")
        assertNotNull("Function calculate must have span style", functionSpan)
        assertEquals(CodeSyntaxHighlighter.FunctionColor, functionSpan?.color)

        val numberSpan = findSpanStyleFor(code, "42")
        assertNotNull("Number 42 must have span style", numberSpan)
        assertEquals(CodeSyntaxHighlighter.NumberColor, numberSpan?.color)
    }

    @Test
    fun testFormatForMobile_splitsSingleLineIfElse() {
        val input = "val status = if (isOnline) \"Conectado\" else \"Desconectado\""
        val expected = "val status = if (isOnline) \"Conectado\"\n    else \"Desconectado\""
        val result = CodeSyntaxHighlighter.formatForMobile(input)

        assertEquals(expected, result)
    }

    @Test
    fun testFormatForMobile_preservesCodeAlreadyFormattedWithNewlines() {
        val input = """
            val status = if (isOnline) "Conectado"
                else "Desconectado"
        """.trimIndent()

        val result = CodeSyntaxHighlighter.formatForMobile(input)
        assertEquals(input, result)
    }

    @Test
    fun testFormatForMobile_preservesBraceFormattedBlocks() {
        val input = """
            if (isOnline) {
                println("Conectado")
            } else {
                println("Desconectado")
            }
        """.trimIndent()

        val result = CodeSyntaxHighlighter.formatForMobile(input)
        assertEquals(input, result)
    }

    @Test
    fun testFormatForMobile_doesNotMutateStringsInsideQuotes() {
        val input = "val action = \"do this or else fail\""
        val result = CodeSyntaxHighlighter.formatForMobile(input)

        assertEquals(input, result)
    }

    @Test
    fun testFormatForMobile_trimsTrailingSpacesPreservingIndentation() {
        val input = "    val a = 10    \n        val b = 20  "
        val expected = "    val a = 10\n        val b = 20"
        val result = CodeSyntaxHighlighter.formatForMobile(input)

        assertEquals(expected, result)
    }
}
