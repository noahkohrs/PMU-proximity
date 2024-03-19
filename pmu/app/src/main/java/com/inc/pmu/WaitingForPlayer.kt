package com.inc.pmu

import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

class WaitingForPlayer : Fragment(R.layout.waiting_for_player) {

    private lateinit var homePageButton: Button
    private lateinit var launchButton: Button

    companion object {
        fun newInstance() = WaitingForPlayer()
    }

    override fun onStart() {
        super.onStart()

        homePageButton = requireView().findViewById(R.id.quitButton)
        launchButton = requireView().findViewById(R.id.lauchButton)

        homePageButton.setOnClickListener {
            val fragment = HomePage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

       launchButton.setOnClickListener {
            val fragment = BetChoice.newInstance()
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