package com.inc.pmu

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit

class BetChoice : Fragment(R.layout.bet_choice) {

    var player : Player? = null
    var suit : Suit? = null


    companion object {
        fun newInstance() = BetChoice()
    }

    override fun onStart() {
        super.onStart()
        Log.d(Global.TAG, "OnStart method")
        var buttonH : Button = requireView().findViewById(R.id.coeurButton)
        var buttonS : Button = requireView().findViewById(R.id.piqueButton)
        var buttonC : Button = requireView().findViewById(R.id.trefleButton)
        var buttonD : Button = requireView().findViewById(R.id.carreauButton)
        var buttonPlay : Button = requireView().findViewById(R.id.jouerButton)
        //val playerIntent = intent
        //player = playerIntent.getSerializableExtra("Player") as Player

        buttonH.setOnClickListener {
            buttonH.setBackgroundColor(Color.YELLOW)
            buttonS.setBackgroundColor(Color.WHITE)
            buttonC.setBackgroundColor(Color.WHITE)
            buttonD.setBackgroundColor(Color.WHITE)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suit = Suit.HEARTS
        }

        buttonS.setOnClickListener {
            buttonH.setBackgroundColor(Color.WHITE)
            buttonS.setBackgroundColor(Color.YELLOW)
            buttonC.setBackgroundColor(Color.WHITE)
            buttonD.setBackgroundColor(Color.WHITE)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suit = Suit.SPADES
        }

        buttonC.setOnClickListener {
            buttonH.setBackgroundColor(Color.WHITE)
            buttonS.setBackgroundColor(Color.WHITE)
            buttonC.setBackgroundColor(Color.YELLOW)
            buttonD.setBackgroundColor(Color.WHITE)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suit = Suit.CLUBS
        }

        buttonD.setOnClickListener {
            buttonH.setBackgroundColor(Color.WHITE)
            buttonS.setBackgroundColor(Color.WHITE)
            buttonC.setBackgroundColor(Color.WHITE)
            buttonD.setBackgroundColor(Color.YELLOW)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suit = Suit.DIAMONDS
        }

        buttonPlay.setOnClickListener {
            val fragment = PseudoChoice.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        buttonPlay.isClickable = false
    }


}