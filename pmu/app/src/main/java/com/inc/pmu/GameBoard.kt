package com.inc.pmu

import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class GameBoard : Fragment(R.layout.game_page) {

    private lateinit var vmGame: ViewModelPMU

    private lateinit var deckButton : ImageButton

    companion object {
        fun newInstance() = GameBoard()
    }

    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        deckButton = requireView().findViewById(R.id.deck)

        deckButton.setOnClickListener {
            vmGame.drawCard()
        }

        deckButton.isClickable = true

    }
}