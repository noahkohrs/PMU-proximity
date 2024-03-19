package com.inc.pmu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class WaitingPage : Fragment(R.layout.waiting_page) {

    private lateinit var vmGame: ViewModelPMU
    companion object {
        fun newInstance() = WaitingPage()
    }
    override fun onStart() {
        super.onStart()
        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
    }
}