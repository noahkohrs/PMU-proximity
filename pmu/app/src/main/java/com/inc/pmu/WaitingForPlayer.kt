package com.inc.pmu

import android.util.Log
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
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

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
        if (vmGame.isHost()) {
            val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(requireActivity().applicationContext)
            vmGame.startHosting(connectionsClient)
            Log.d(Global.TAG, "${vmGame.localUsername} starts hosting...")
            if (vmGame.game.players.size > 1) {
                launchButton.setBackgroundColor(resources.getColor(R.color.selectOrValidate))
            }
        }
        else {
            launchButton.isClickable = false
            launchButton.setBackgroundColor(resources.getColor(R.color.unavailable))
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onPlayerListUpdate(playerList: Array<out String>?) {
                    if (playerList != null) {
                        var textPlayer =  requireView().findViewById<TextView>(R.id.playerList).text
                        for (player in playerList) {
                            textPlayer = textPlayer.toString() + player + '\n'
                        }
                    }
                }
            }
        )

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