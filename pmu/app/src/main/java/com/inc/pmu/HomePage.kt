package com.inc.pmu

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
    private var username = ""
    companion object {
        fun newInstance() = HomePage()
    }

    override fun onStart() {
        super.onStart()
        try {
            vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
            vmGame.stopConnection()
        } catch (e: IllegalArgumentException) {
            Log.d(Global.TAG, "No ViewModel to stop")
        }
        vmUserData = ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java]
        username = vmUserData.getUsername()
        // Destroy the view model given by ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java] if it exists



        createButton = requireView().findViewById(R.id.createButton)
        joinButton = requireView().findViewById(R.id.joinButton)

        createButton.setOnClickListener {
            requireActivity().viewModelStore.clear()
            vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory(ViewModelPMUFactory.Mode.HOST))[ViewModelPMU::class.java]
            ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java].setUsername(username)
            var player = Player(vmUserData.getUsername())
            vmGame.game = HostGame(player)
            vmGame.localPuuid = player.puuid
            vmGame.localUsername = vmUserData.getUsername()
            vmGame.context = requireActivity().applicationContext
            val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(requireActivity().applicationContext)
            vmGame.startHosting(connectionsClient)
            Log.d(Global.TAG, "${vmGame.localUsername} starts hosting...")
            val fragment = WaitingForPlayer.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        joinButton.setOnClickListener {
            requireActivity().viewModelStore.clear()
            vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory(ViewModelPMUFactory.Mode.CLIENT))[ViewModelPMU::class.java]
            ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java].setUsername(username)
            vmGame.localUsername = vmUserData.getUsername()
            vmGame.context = requireActivity().applicationContext
            val fragment = JoinGame.newInstance() // Seems to be wrong to create the activity everytime
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
    }
}