package edu.iu.luddy.midtermproject

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeRadioGroup: RadioGroup
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton
    private lateinit var fontSizeSpinner: Spinner
    private lateinit var colorSpinner: Spinner
    private lateinit var btnSaveSettings: androidx.appcompat.widget.AppCompatButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rootLayout: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)

        rootLayout = findViewById(R.id.settingsScrollView)
        themeRadioGroup = findViewById(R.id.themeRadioGroup)
        radioLight = findViewById(R.id.radioLight)
        radioDark = findViewById(R.id.radioDark)
        fontSizeSpinner = findViewById(R.id.fontSizeSpinner)
        colorSpinner = findViewById(R.id.colorSpinner)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)

        setupSpinners()
        loadSettings()
        applyCurrentTheme()

        btnSaveSettings.setOnClickListener {
            saveSettings()
            finish()
        }

        themeRadioGroup.setOnCheckedChangeListener { _, _ ->
            applyCurrentTheme()
        }
    }

    private fun applyCurrentTheme() {
        if (radioDark.isChecked) {
            rootLayout.setBackgroundColor(Color.parseColor("#2C2C2C"))
            findViewById<LinearLayout>(R.id.settingsLinearLayout).setBackgroundColor(Color.parseColor("#2C2C2C"))

            val textViews = listOf<TextView>(
                findViewById(R.id.themeLabel),
                findViewById(R.id.fontSizeLabel),
                findViewById(R.id.colorLabel)
            )
            textViews.forEach { it.setTextColor(Color.WHITE) }

            radioLight.setTextColor(Color.WHITE)
            radioDark.setTextColor(Color.WHITE)
        } else {
            rootLayout.setBackgroundColor(Color.WHITE)
            findViewById<LinearLayout>(R.id.settingsLinearLayout).setBackgroundColor(Color.WHITE)

            val textViews = listOf<TextView>(
                findViewById(R.id.themeLabel),
                findViewById(R.id.fontSizeLabel),
                findViewById(R.id.colorLabel)
            )
            textViews.forEach { it.setTextColor(Color.BLACK) }

            radioLight.setTextColor(Color.BLACK)
            radioDark.setTextColor(Color.BLACK)
        }
    }

    private fun setupSpinners() {
        val fontSizes = arrayOf("Small", "Medium", "Large")
        val fontAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontSizes)
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSizeSpinner.adapter = fontAdapter

        val colors = arrayOf("Black", "Blue", "Red", "Green")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter = colorAdapter
    }

    private fun loadSettings() {
        val theme = sharedPreferences.getString(MainActivity.THEME_KEY, "Light")
        if (theme == "Dark") {
            radioDark.isChecked = true
        } else {
            radioLight.isChecked = true
        }

        val fontSize = sharedPreferences.getString(MainActivity.FONT_SIZE_KEY, "Medium")
        val fontSizePosition = when (fontSize) {
            "Small" -> 0
            "Medium" -> 1
            "Large" -> 2
            else -> 1
        }
        fontSizeSpinner.setSelection(fontSizePosition)

        val color = sharedPreferences.getString(MainActivity.COLOR_KEY, "Black")
        val colorPosition = when (color) {
            "Black" -> 0
            "Blue" -> 1
            "Red" -> 2
            "Green" -> 3
            else -> 0
        }
        colorSpinner.setSelection(colorPosition)
    }

    private fun saveSettings() {
        val editor = sharedPreferences.edit()

        val selectedTheme = if (radioLight.isChecked) "Light" else "Dark"
        editor.putString(MainActivity.THEME_KEY, selectedTheme)

        val selectedFontSize = fontSizeSpinner.selectedItem.toString()
        editor.putString(MainActivity.FONT_SIZE_KEY, selectedFontSize)

        val selectedColor = colorSpinner.selectedItem.toString()
        editor.putString(MainActivity.COLOR_KEY, selectedColor)

        editor.apply()

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
    }
}