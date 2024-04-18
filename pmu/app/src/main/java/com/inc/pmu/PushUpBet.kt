package com.inc.pmu

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class PushUpBet : Fragment(R.layout.pushup_bet_page) {

    private lateinit var vmGame: ViewModelPMU

    companion object {
        fun newInstance() = PushUpBet()
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG.TAG, "PushUpsBet")

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        var playButton : Button = requireView().findViewById(R.id.betButton)
        var plusButton : Button = requireView().findViewById(R.id.plus)
        var minusButton : Button = requireView().findViewById(R.id.minus)
        var counter : TextView = requireView().findViewById(R.id.counter)


        var betListener = object : ViewModelListener() {
            override fun onBetValidated(suit: Suit, players: MutableCollection<Player>) {
                Log.d(TAG.TAG, "OnBetValidated")

                var nSuitBet = 0
                for (player in players) {
                    if (player.bet.number != -1 && player.bet.suit == suit) {
                        nSuitBet++
                    }
                }


                if (nSuitBet > players.size/4) {
                    vmGame.suitUnavailable(suit)
                }
            }
        }

        playButton.setOnClickListener {
            vmGame.removeListener(betListener)
            vmGame.counter = counter.text.toString().toInt()
            val fragment = BetChoice.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        plusButton.setOnClickListener {
            var c = counter.text.toString().toInt()
            var newValue = c+1
            counter.setText(newValue.toString())
        }

        minusButton.setOnClickListener {
            var c = counter.text.toString().toInt()
            if (c > 1) {
                var newValue = c - 1
                counter.setText(newValue.toString())
            }
        }

        vmGame.addListener(betListener)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }
}