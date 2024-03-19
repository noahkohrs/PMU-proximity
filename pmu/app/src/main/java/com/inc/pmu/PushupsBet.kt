package com.inc.pmu

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class PushupsBet : Fragment(R.layout.pushup_bet_page) {

    private lateinit var vmGame: ViewModelPMU
    companion object {
        fun newInstance() = PushupsBet()
    }
    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        var playButton : Button = requireView().findViewById(R.id.jouerButton)

        playButton.setOnClickListener {
            val fragment = TeamsPage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}