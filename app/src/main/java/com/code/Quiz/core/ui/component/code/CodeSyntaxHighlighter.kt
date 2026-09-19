package com.code.Quiz.core.ui.component.code

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.code.Quiz.core.theme.AccentBlue
import com.code.Quiz.core.theme.AccentCyan
import com.code.Quiz.core.theme.StreakOrange
import com.code.Quiz.core.theme.SuccessMint
import com.code.Quiz.core.theme.TextMuted
import com.code.Quiz.core.theme.TextPrimary
import com.code.Quiz.core.theme.TextSecondary

/**
 * Native syntax highlighter and mobile code formatter for CodeQuiz.
 *
 * Implements token-based syntax highlighting with theme colors from DESIGN.md
 * and smart responsive formatting for mobile viewports without horizontal clipping.
 */
object CodeSyntaxHighlighter {

    val KeywordColor: Color = Color(0xFF818CF8)
    val TypeColor: Color = AccentCyan
    val StringColor: Color = SuccessMint
    val NumberColor: Color = StreakOrange
    val CommentColor: Color = TextMuted
    val FunctionColor: Color = AccentBlue
    val OperatorColor: Color = TextSecondary
    val DefaultTextColor: Color = TextPrimary

    private val TOKEN_REGEX = Regex(
        // Group 1: Comments (single-line // or multi-line /* ... */)
        "(//.*|/\\*[\\s\\S]*?\\*/)" +
        // Group 2: Strings (triple-quoted, double-quoted, single-quoted with escape sequences)
        "|(\"\"\"[\\s\\S]*?\"\"\"|\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*')" +
        // Group 3: Keywords (Kotlin, Python, Java standard keywords)
        "|(\\b(?:val|var|fun|if|else|when|for|while|return|class|interface|data|object|sealed|import|package|override|is|in|as|try|catch|finally|throw|true|false|null|def|elif|from)\\b)" +
        // Group 4: Types & Classes (Built-in standard types and PascalCase identifiers)
        "|(\\b(?:Int|String|Boolean|Double|Float|Long|Char|Byte|Short|List|Set|Map|Array|Unit|Any|Nothing|[A-Z][a-zA-Z0-9_]*)\\b)" +
        // Group 5: Function calls (identifier followed by '(')
        "|(\\b[a-zA-Z_][a-zA-Z0-9_]*(?=\\s*\\())" +
        // Group 6: Numbers (hex, binary, floats, integers)
        "|(\\b(?:0[xX][0-9a-fA-F]+|0[bB][01]+|\\d+\\.\\d+(?:[fF]|L)?|\\d+(?:[fF]|L)?)\\b)" +
        // Group 7: Operators
        "|(==|!=|<=|>=|->|::|[=<>?:+\\-*/%])"
    )

    /**
     * Highlights the given code snippet according to language syntax rules and DESIGN.md palette.
     */
    fun highlight(code: String, language: String? = "Kotlin"): AnnotatedString {
        if (code.isEmpty()) return AnnotatedString("")

        val builder = AnnotatedString.Builder(code)
        builder.addStyle(SpanStyle(color = DefaultTextColor), 0, code.length)

        for (match in TOKEN_REGEX.findAll(code)) {
            val groupIndex = (1..7).firstOrNull { match.groups[it] != null } ?: continue
            val group = match.groups[groupIndex] ?: continue

            val spanStyle = when (groupIndex) {
                1 -> SpanStyle(color = CommentColor, fontStyle = FontStyle.Italic)
                2 -> SpanStyle(color = StringColor)
                3 -> SpanStyle(color = KeywordColor, fontWeight = FontWeight.SemiBold)
                4 -> SpanStyle(color = TypeColor)
                5 -> SpanStyle(color = FunctionColor)
                6 -> SpanStyle(color = NumberColor)
                7 -> SpanStyle(color = OperatorColor)
                else -> null
            }

            if (spanStyle != null) {
                builder.addStyle(spanStyle, group.range.first, group.range.last + 1)
            }
        }

        return builder.toAnnotatedString()
    }

    /**
     * Smart formatter for mobile displays:
     * - Breaks long single-line expressions like ') else ' or expressions containing 'else' onto a new line with 4-space indent.
     * - Never mutates strings inside quotes or comments.
     * - Preserves code that is already formatted with newlines.
     * - Trims trailing whitespace while preserving intentional code indentation.
     */
    fun formatForMobile(code: String): String {
        if (code.isBlank()) return code

        return code.lines()
            .map { line -> formatLineForMobile(line.trimEnd()) }
            .joinToString("\n")
    }

    private fun formatLineForMobile(line: String): String {
        var inDoubleQuote = false
        var inSingleQuote = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            // Handle escape sequences inside quotes
            if ((inDoubleQuote || inSingleQuote) && char == '\\') {
                i += 2
                continue
            }

            if (char == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote
                i++
                continue
            }

            if (char == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote
                i++
                continue
            }

            // Only look for keywords outside quotes
            if (!inDoubleQuote && !inSingleQuote) {
                // Single-line comment //: rest of the line is comment
                if (char == '/' && i + 1 < line.length && line[i + 1] == '/') {
                    break
                }

                // Check for keyword 'else'
                if (line.startsWith("else", i)) {
                    val isWordStart = (i == 0) || (!line[i - 1].isLetterOrDigit() && line[i - 1] != '_')
                    val nextIdx = i + 4
                    val isWordEnd = (nextIdx >= line.length) || (!line[nextIdx].isLetterOrDigit() && line[nextIdx] != '_')

                    if (isWordStart && isWordEnd) {
                        val prefix = line.substring(0, i)
                        val trimmedPrefix = prefix.trim()

                        // If prefix is empty or just '}', it's already on its own line
                        if (trimmedPrefix.isNotEmpty() && trimmedPrefix != "}") {
                            val baseIndent = line.takeWhile { it == ' ' }
                            val continuationIndent = if (trimmedPrefix.startsWith("else")) {
                                baseIndent
                            } else {
                                baseIndent + "    "
                            }
                            val firstPart = prefix.trimEnd()
                            val restPart = line.substring(i).trimStart()
                            val formattedRest = formatLineForMobile(continuationIndent + restPart)
                            return "$firstPart\n$formattedRest"
                        }
                    }
                }
            }

            i++
        }

        return line
    }
}
