package edu.iu.luddy.midtermproject

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var outputDisplay: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var settingsIcon: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    private var currentInput = ""
    private var operator = ""
    private var firstOperand = 0.0
    private var isNewOperation = true

    companion object {
        const val PREFS_NAME = "CalculatorPrefs"
        const val THEME_KEY = "theme"
        const val FONT_SIZE_KEY = "fontSize"
        const val COLOR_KEY = "color"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        outputDisplay = findViewById(R.id.outputDisplay)
        mainLayout = findViewById(R.id.mainLayout)
        settingsIcon = findViewById(R.id.settingsIcon)

        setupNumberButtons()
        setupOperatorButtons()
        setupSpecialButtons()

        settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        applySettings()
        resetOutput()
    }

    override fun onResume() {
        super.onResume()
        applySettings()
    }

    override fun onStop() {
        super.onStop()
        resetOutput()
    }

    private fun applySettings() {
        val theme = sharedPreferences.getString(THEME_KEY, "Light")
        if (theme == "Dark") {
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"))
            findViewById<TextView>(R.id.appTitle).setTextColor(Color.WHITE)
        } else {
            mainLayout.setBackgroundColor(Color.WHITE)
            findViewById<TextView>(R.id.appTitle).setTextColor(Color.parseColor("#999999"))
        }

        val fontSize = sharedPreferences.getString(FONT_SIZE_KEY, "Medium")
        outputDisplay.textSize = when (fontSize) {
            "Small" -> 36f
            "Medium" -> 48f
            "Large" -> 60f
            else -> 48f
        }

        val color = sharedPreferences.getString(COLOR_KEY, "Black")
        outputDisplay.setTextColor(when (color) {
            "Black" -> Color.BLACK
            "Blue" -> Color.BLUE
            "Red" -> Color.RED
            "Green" -> Color.parseColor("#006400")
            else -> Color.BLACK
        })
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberButtons.forEachIndexed { index, buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                onNumberClick(index.toString())
            }
        }
    }

    private fun setupOperatorButtons() {
        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperatorClick("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { onOperatorClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorClick("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorClick("÷") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsClick() }
    }

    private fun setupSpecialButtons() {
        findViewById<Button>(R.id.btnClear).setOnClickListener { showClearConfirmation() }
        findViewById<Button>(R.id.btnDecimal).setOnClickListener { onDecimalClick() }
        findViewById<Button>(R.id.btnSqrt).setOnClickListener { onSqrtClick() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspaceClick() }
    }

    private fun onNumberClick(number: String) {
        if (isNewOperation) {
            currentInput = ""
            isNewOperation = false
        }

        val digitCount = currentInput.replace(".", "").length
        if (digitCount >= 5) {
            return
        }

        currentInput += number
        updateDisplay(currentInput)
    }

    private fun onOperatorClick(op: String) {
        if (currentInput.isEmpty() && firstOperand == 0.0) return

        if (currentInput.isNotEmpty()) {
            if (operator.isNotEmpty()) {
                onEqualsClick()
            } else {
                firstOperand = currentInput.toDouble()
            }
        }

        operator = op
        currentInput = ""
    }

    private fun onEqualsClick() {
        if (currentInput.isEmpty() || operator.isEmpty()) return

        val secondOperand = currentInput.toDouble()
        val result = when (operator) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "×" -> firstOperand * secondOperand
            "÷" -> if (secondOperand != 0.0) firstOperand / secondOperand else 0.0
            else -> 0.0
        }

        val roundedResult = String.format("%.5f", result).toDouble()
        currentInput = formatResult(roundedResult)
        updateDisplay(currentInput)

        firstOperand = roundedResult
        operator = ""
        isNewOperation = true
    }

    private fun onDecimalClick() {
        if (isNewOperation) {
            currentInput = "0"
            isNewOperation = false
        }

        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) {
                currentInput = "0"
            }
            currentInput += "."
            updateDisplay(currentInput)
        }
    }

    private fun onSqrtClick() {
        if (currentInput.isEmpty()) return

        val value = currentInput.toDouble()
        if (value < 0) {
            updateDisplay("Error")
            return
        }

        val result = sqrt(value)
        val roundedResult = String.format("%.5f", result).toDouble()
        currentInput = formatResult(roundedResult)
        updateDisplay(currentInput)
        firstOperand = roundedResult
        isNewOperation = true
    }

    private fun onBackspaceClick() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            if (currentInput.isEmpty()) {
                currentInput = "0"
            }
            updateDisplay(currentInput)
        }
    }

    private fun showClearConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Clear Calculator")
            .setMessage("Are you sure you want to reset the calculator?")
            .setPositiveButton("Yes") { _, _ ->
                resetOutput()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetOutput() {
        currentInput = ""
        operator = ""
        firstOperand = 0.0
        isNewOperation = true
        updateDisplay("0")
    }

    private fun updateDisplay(value: String) {
        outputDisplay.text = value
    }

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            value.toString().trimEnd('0').trimEnd('.')
        }
    }
}