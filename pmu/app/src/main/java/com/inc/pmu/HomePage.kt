package com.inc.pmu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.inc.pmu.models.Game
import com.inc.pmu.models.HostGame
import com.inc.pmu.models.Player
import com.inc.pmu.viewmodels.ViewModelBeforeNetwork
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class HomePage : Fragment(R.layout.home_page) {

    private lateinit var createButton: Button
    private lateinit var joinButton: Button

    private lateinit var vmUserData: ViewModelBeforeNetwork
    private lateinit var vmGame: ViewModelPMU
    companion object {
        fun newInstance() = HomePage()
    }

    override fun onStart() {
        super.onStart()

        vmUserData = ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java]


        createButton = requireView().findViewById(R.id.createButton)
        joinButton = requireView().findViewById(R.id.joinButton)

        createButton.setOnClickListener {
            vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory(ViewModelPMUFactory.Mode.HOST))[ViewModelPMU::class.java]
            var player = Player(vmUserData.getUsername())
            vmGame.game = HostGame(player)
            vmGame.localId = player.puuid
            vmGame.localUsername = vmUserData.getUsername()
            val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(requireActivity().applicationContext)
            vmGame.startHosting(connectionsClient)
            Log.d(Global.TAG, "${vmGame.localUsername} starts hosting...")
            val fragment = WaitingForPlayer.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        joinButton.setOnClickListener {
            val fragment = JoinGame.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack("HomePage", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        //TODO create player
    }
}