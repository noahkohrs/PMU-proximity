package com.inc.pmu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    lateinit var pseudoField: EditText
    lateinit var playButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pseudo_choice)

        pseudoField = findViewById(R.id.pseudoInput)
        playButton = findViewById(R.id.playButton)
    }
    override fun onStart() {
        super.onStart()

        if (pseudoField.text.toString() != "") {
            playButton.isClickable = true
        }
        else {
            playButton.isClickable = false
        }

        pseudoField.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (pseudoField.text.toString() == "") {
                    playButton.setBackgroundColor(resources.getColor(R.color.unavailable))
                    playButton.isClickable = false
                }
                else {
                    playButton.setBackgroundColor(resources.getColor(R.color.white))
                    playButton.isClickable = true
                }
            }

        })
    }

    fun onClickPlayButton(view: View) {
        val intent = Intent(this,HomePage::class.java)
        startActivities(arrayOf(intent))
    }
}