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
import com.inc.pmu.viewmodels.ViewModelBeforeNetwork
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

        vmUserData = ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java]
        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory(ViewModelPMUFactory.Mode.HOST))[ViewModelPMU::class.java]
        vmGame.game = Game(mutableListOf(Player(vmUserData.getUsername())))
        vmGame.localUsername = vmUserData.getUsername()
        val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(requireActivity().applicationContext)
        vmGame.startHosting(connectionsClient)
        Log.d(Global.TAG, "${vmGame.localUsername} starts hosting...")

        homePageButton = requireView().findViewById(R.id.quitButton)
        launchButton = requireView().findViewById(R.id.lauchButton)

        homePageButton.setOnClickListener {
            val fragment = HomePage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

       launchButton.setOnClickListener {
           val fragment = BetChoice.newInstance()
           requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}