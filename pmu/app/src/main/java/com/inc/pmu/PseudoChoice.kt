package com.inc.pmu

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class PseudoChoice : Fragment(R.layout.pseudo_choice) {

    lateinit var pseudoField: EditText
    lateinit var playButton: Button

    companion object {
        fun newInstance() = PseudoChoice()
    }

    override fun onStart() {
        super.onStart()

        pseudoField = requireView().findViewById(R.id.pseudoInput)
        playButton = requireView().findViewById(R.id.playButton)

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
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}