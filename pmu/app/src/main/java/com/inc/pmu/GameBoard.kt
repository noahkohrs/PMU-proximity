package com.inc.pmu

import android.widget.Button
import androidx.fragment.app.Fragment
import com.inc.pmu.viewmodels.ViewModelPMU

class GameBoard : Fragment(R.layout.game_page) {

    private lateinit var vmGame: ViewModelPMU

    private lateinit var deckButton : Button

    companion object {
        fun newInstance() = GameBoard()
    }

    override fun onStart() {
        super.onStart()

        deckButton = requireActivity().findViewById(R.id.deck)


        deckButton.setOnClickListener {
            
        }
    }
}