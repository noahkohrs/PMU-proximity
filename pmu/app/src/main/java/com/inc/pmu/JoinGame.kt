package com.inc.pmu

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.inc.pmu.viewmodels.ViewModelBeforeNetwork
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class JoinGame : Fragment(R.layout.join_page) {

    lateinit var homePage: Button

    private lateinit var vmUserData: ViewModelBeforeNetwork
    private lateinit var vmGame: ViewModelPMU

    companion object {
        fun newInstance() = JoinGame()
    }

    override fun onStart() {
        super.onStart()

        vmUserData = ViewModelProvider(requireActivity())[ViewModelBeforeNetwork::class.java]
        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory(ViewModelPMUFactory.Mode.CLIENT))[ViewModelPMU::class.java]
        vmGame.localUsername = vmUserData.getUsername()
        val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(requireActivity().applicationContext)
        vmGame.startDiscovering(connectionsClient)
        Log.d(Global.TAG, "${vmGame.localUsername} starts searching...")

        homePage = requireView().findViewById(R.id.homePage)

        homePage.setOnClickListener {
            val fragment = HomePage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}