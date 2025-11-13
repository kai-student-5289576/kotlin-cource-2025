package com.example.kai_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kai_kotlin.ui.theme.KaikotlinTheme

class CalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaikotlinTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    val expr = remember { mutableStateOf("") }
    val output = remember { mutableStateOf("") }

    fun push(symbol: String) {
        when (symbol) {
            "C" -> {
                expr.value = ""
                output.value = ""
            }
            "DEL" -> {
                if (expr.value.isNotEmpty()) expr.value = expr.value.dropLast(1)
            }
            "=" -> {
                if (expr.value.isNotBlank()) {
                    val res = try {
                        evaluateExpression(expr.value)
                    } catch (_: Exception) {
                        null
                    }
                    output.value = if (res == null) "Error" else formatResult(res)
                }
            }
            else -> {
                expr.value += symbol
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)) {
            Text(
                text = expr.value.ifBlank { "0" },
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                text = output.value,
                color = Color(0xFF4CAF50),
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }

        // Buttons grid
        val rows = listOf(
            listOf("C", "(", ")", "DEL"),
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*") ,
            listOf("1", "2", "3", "-") ,
            listOf("0", ".", "=", "+")
        )

        for (r in rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (label in r) {
                    Button(
                        onClick = { push(label) },
                        modifier = Modifier
                            .weight(1f)
                            .size(height = 56.dp, width = 0.dp) // width weight applied by parent
                    ) {
                        Text(text = label, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

// Utility: format doubles nicely (show integer without .0 when applicable)
private fun formatResult(value: Double): String {
    return if (value.isFinite() && value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        // limit to 10 significant digits
        String.format("%s", value)
    }
}

// Safe evaluator: tokenization -> shunting-yard -> RPN evaluation
private fun evaluateExpression(input: String): Double? {
    val tokens = tokenize(input) ?: return null
    val rpn = shuntingYard(tokens) ?: return null
    return evalRPN(rpn)
}

private sealed class Token {
    data class Number(val value: Double) : Token()
    data class Op(val symbol: String) : Token()
    data class Paren(val symbol: String) : Token()
}

private fun tokenize(s: String): List<Token>? {
    val out = mutableListOf<Token>()
    var i = 0
    val str = s.replace(" ", "")
    while (i < str.length) {
        val c = str[i]
        when {
            c.isDigit() || c == '.' -> {
                val start = i
                i++
                while (i < str.length && (str[i].isDigit() || str[i] == '.')) i++
                val num = str.substring(start, i).toDoubleOrNull() ?: return null
                out.add(Token.Number(num))
                continue
            }
            c == '+' || c == '-' || c == '*' || c == '/' || c == '^' -> {
                // handle unary minus: if at start or after '(' or another operator
                if (c == '-' ) {
                    val prev = if (out.isEmpty()) null else out.last()
                    if (prev == null || prev is Token.Op || (prev is Token.Paren && prev.symbol == "(")) {
                        // unary minus: parse number after it if present
                        // attempt to parse following number
                        var j = i + 1
                        if (j < str.length && (str[j].isDigit() || str[j] == '.')) {
                            val start = j
                            j++
                            while (j < str.length && (str[j].isDigit() || str[j] == '.')) j++
                            val num = str.substring(start, j).toDoubleOrNull() ?: return null
                            out.add(Token.Number(-num))
                            i = j
                            continue
                        } else {
                            // treat as unary minus operator (we'll represent as 'u-')
                            out.add(Token.Op("u-"))
                            i++
                            continue
                        }
                    }
                }
                out.add(Token.Op(c.toString()))
            }
            c == '(' || c == ')' -> out.add(Token.Paren(c.toString()))
            else -> return null // invalid character
        }
        i++
    }
    return out
}

private fun shuntingYard(tokens: List<Token>): List<Token>? {
    val output = mutableListOf<Token>()
    val ops = ArrayDeque<Token.Op>()

    fun prec(op: String): Int = when (op) {
        "u-" -> 4
        "^" -> 3
        "*", "/" -> 2
        "+", "-" -> 1
        else -> 0
    }

    fun isRightAssoc(op: String) = (op == "^" || op == "u-")

    for (t in tokens) {
        when (t) {
            is Token.Number -> output.add(t)
            is Token.Op -> {
                while (ops.isNotEmpty()) {
                    val top = ops.last()
                    if ((isRightAssoc(t.symbol) && prec(t.symbol) < prec(top.symbol)) || (!isRightAssoc(t.symbol) && prec(t.symbol) <= prec(top.symbol))) {
                        output.add(ops.removeLast())
                    } else break
                }
                ops.add(t)
            }
            is Token.Paren -> {
                if (t.symbol == "(") ops.add(Token.Op("("))
                else if (t.symbol == ")") {
                    var found = false
                    while (ops.isNotEmpty()) {
                        val op = ops.removeLast()
                        if (op.symbol == "(") { found = true; break }
                        output.add(op)
                    }
                    if (!found) return null // mismatched parens
                }
            }
        }
    }

    while (ops.isNotEmpty()) {
        val op = ops.removeLast()
        if (op.symbol == "(" || op.symbol == ")") return null
        output.add(op)
    }

    return output
}

private fun evalRPN(rpn: List<Token>): Double? {
    val stack = ArrayDeque<Double>()
    for (t in rpn) {
        when (t) {
            is Token.Number -> stack.addLast(t.value)
            is Token.Op -> {
                when (t.symbol) {
                    "u-" -> {
                        if (stack.isEmpty()) return null
                        val a = stack.removeLast()
                        stack.addLast(-a)
                    }
                    "+" -> {
                        if (stack.size < 2) return null
                        val b = stack.removeLast(); val a = stack.removeLast()
                        stack.addLast(a + b)
                    }
                    "-" -> {
                        if (stack.size < 2) return null
                        val b = stack.removeLast(); val a = stack.removeLast()
                        stack.addLast(a - b)
                    }
                    "*" -> {
                        if (stack.size < 2) return null
                        val b = stack.removeLast(); val a = stack.removeLast()
                        stack.addLast(a * b)
                    }
                    "/" -> {
                        if (stack.size < 2) return null
                        val b = stack.removeLast(); val a = stack.removeLast()
                        if (b == 0.0) return null
                        stack.addLast(a / b)
                    }
                    "^" -> {
                        if (stack.size < 2) return null
                        val b = stack.removeLast(); val a = stack.removeLast()
                        stack.addLast(Math.pow(a, b))
                    }
                    else -> return null
                }
            }
            else -> return null
        }
    }
    return if (stack.size == 1) stack.last() else null
}
