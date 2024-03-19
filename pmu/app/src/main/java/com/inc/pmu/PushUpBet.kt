package com.inc.pmu

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class PushUpBet : Fragment(R.layout.pushup_bet_page) {

    companion object {
        fun newInstance() = PushUpBet()
    }
    override fun onStart() {
        super.onStart()
        Log.d(Global.TAG, "PushUpsBet")

        var playButton : Button = requireView().findViewById(R.id.betButton)
        var plusButton : Button = requireView().findViewById(R.id.plus)
        var minusButton : Button = requireView().findViewById(R.id.minus)
        var counter : TextView = requireView().findViewById(R.id.counter)


        playButton.setOnClickListener {
            val fragment = WaitingPage.newInstance()
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


    }
}