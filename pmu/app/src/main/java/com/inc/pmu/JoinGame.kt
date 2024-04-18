package com.inc.pmu

import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.inc.pmu.viewmodels.ViewModelBeforeNetwork
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class JoinGame : Fragment(R.layout.join_page) {

    lateinit var homePageButton: Button

    private lateinit var vmUserData: ViewModelBeforeNetwork
    private lateinit var vmGame: ViewModelPMU

    companion object {
        fun newInstance() = JoinGame()
    }

    override fun onStart() {
        super.onStart()
        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
        val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(requireActivity().applicationContext)
        vmGame.startDiscovering(connectionsClient)
        Log.d(Global.TAG, "${vmGame.localUsername} starts searching...")

        homePageButton = requireView().findViewById(R.id.homePage)

        homePageButton.setOnClickListener {
            val fragment = HomePage.newInstance()
            vmGame.stopConnection()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onConnectionEstablished(state: String) {
                    vmGame.removeAllListeners()
                    Log.d(Global.TAG, "Connection established")
                    var fragment: Fragment = WaitingForPlayer.newInstance()
                    if (state == "betting") {
                        fragment = PushUpBet.newInstance()
                    } else if (state == "in-game") {
                        fragment = GameBoard.newInstance()
                    }
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
                }
            }
        )
    }
}