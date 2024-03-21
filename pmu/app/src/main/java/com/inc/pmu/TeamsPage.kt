package com.inc.pmu

import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

class TeamsPage : Fragment(R.layout.teams_page) {

    companion object {
        fun newInstance() = TeamsPage()
    }
    override fun onStart() {
        super.onStart()

        var playButton : Button = requireView().findViewById(R.id.jouerButton)

        playButton.setOnClickListener {
            val fragment = PushUpBet.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}