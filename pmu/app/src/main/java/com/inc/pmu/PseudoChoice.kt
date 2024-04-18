package com.inc.pmu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.viewmodels.ViewModelBeforeNetwork

class PseudoChoice : Fragment(R.layout.pseudo_choice) {

    private lateinit var pseudoField: EditText
    private lateinit var playButton: Button

    private lateinit var vmUserData : ViewModelBeforeNetwork

    companion object {
        fun newInstance() = PseudoChoice()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG.TAG, "WaitingPage onDestroyView")
    }

    override fun onStart() {
        super.onStart()

        vmUserData = ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java]

        pseudoField = requireView().findViewById(R.id.pseudoInput)
        playButton = requireView().findViewById(R.id.playButton)

        playButton.setOnClickListener {
            val fragment = HomePage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("HomePage")
                .commit()
        }

        if (pseudoField.text.toString() != "") {
            playButton.isClickable = true
            playButton.setBackgroundColor(resources.getColor(R.color.white))
        }
        else {
            playButton.isClickable = false
            playButton.setBackgroundColor(resources.getColor(R.color.unavailable))
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
                } else {
                    playButton.setBackgroundColor(resources.getColor(R.color.white))
                    vmUserData.setUsername(pseudoField.text.toString())
                    playButton.isClickable = true
                }
            }
        })
    }
}