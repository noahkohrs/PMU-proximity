package com.inc.pmu

import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class ResultsPage : Fragment(R.layout.results_page) {

    private lateinit var vmGame: ViewModelPMU
    private lateinit var  pushUpsByPlayer: TextView

    companion object {
        fun newInstance() = GameBoard()
    }

    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        pushUpsByPlayer = requireView().findViewById(R.id.pushUpsByPlayer)

        for (p in vmGame.game.players) {
            pushUpsByPlayer.append("${p} a effectué ${p.value}\n")
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }
}