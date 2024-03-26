package com.inc.pmu

import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class TeamsPage : Fragment(R.layout.teams_page) {

    private lateinit var vmGame: ViewModelPMU
    companion object {
        fun newInstance() = TeamsPage()
    }
    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        var playButton : Button = requireView().findViewById(R.id.jouerButton)

        playButton.setOnClickListener {
            vmGame.startGame()
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