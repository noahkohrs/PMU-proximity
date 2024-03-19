package com.inc.pmu

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class TeamsPage : Fragment(R.layout.teams_page) {

    companion object {
        fun newInstance() = TeamsPage()
    }
    override fun onStart() {
        super.onStart()

        var playButton : Button = requireView().findViewById(R.id.jouerButton)

        playButton.setOnClickListener {
            val fragment = PushupsBet.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}