package com.inc.pmu

import android.util.Log
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import com.inc.pmu.models.Game
import com.inc.pmu.models.PayloadMaker
import com.inc.pmu.models.Player
import com.inc.pmu.viewmodels.Action
import com.inc.pmu.viewmodels.Param
import com.inc.pmu.viewmodels.Sender
import com.inc.pmu.viewmodels.ViewModelBeforeNetwork
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class WaitingForPlayer : Fragment(R.layout.waiting_for_player) {

    private lateinit var homePageButton: Button
    private lateinit var launchButton: Button

    private lateinit var vmUserData: ViewModelBeforeNetwork
    private lateinit var vmGame: ViewModelPMU

    companion object {
        fun newInstance() = WaitingForPlayer()
    }

    override fun onStart() {
        super.onStart()
        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        homePageButton = requireView().findViewById(R.id.quitButton)
        launchButton = requireView().findViewById(R.id.lauchButton)

        homePageButton.setOnClickListener {
            val fragment = HomePage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

       launchButton.setOnClickListener {
           vmGame.startBet()
            val fragment = PushUpBet.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onBetStart() {
                    Log.d(Global.TAG, "Start bet !")
                    val fragment = PushUpBet.newInstance()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
                }
            }
        )

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}