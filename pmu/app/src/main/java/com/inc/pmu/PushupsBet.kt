package com.inc.pmu

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class PushupsBet : Fragment(R.layout.pushup_bet_page) {

    companion object {
        fun newInstance() = PushupsBet()
    }
    override fun onStart() {
        super.onStart()

        var playButton : Button = requireView().findViewById(R.id.jouerButton)

        playButton.setOnClickListener {
            val fragment = TeamsPage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}