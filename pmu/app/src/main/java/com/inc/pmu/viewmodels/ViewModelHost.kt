package com.inc.pmu.viewmodels

import android.util.Log
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.inc.pmu.BuildConfig
import com.inc.pmu.Global
import com.inc.pmu.models.Player

class ViewModelHost() : ViewModelPMU() {
    private var playersEndpointIds = mutableListOf<String>()
    private var players = mutableListOf<String>()

    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    override fun onConnectionResultOK(endpointId: String) {
        playersEndpointIds.add(endpointId)
        Log.d(Global.TAG, playersEndpointIds.toString())
    }

    override fun startDiscovering(connectionsClient: ConnectionsClient) {
        throw UnsupportedOperationException("Host cannot discover")
    }


    override fun startHosting(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
        Log.d(TAG, "Start advertising...")
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(
            localUsername, // 2
            BuildConfig.APPLICATION_ID, // 3
            connectionLifecycleCallback, // 4
            advertisingOptions // 5
        ).addOnSuccessListener {
            this.players.add(localUsername)
            Log.d(TAG, "Advertising...")
        }.addOnFailureListener {
            // 7
            Log.d(TAG, "Unable to start advertising")
        }
    }

    override fun onPayloadReceived(endpointId: String, paquet: String) {
        game.addPlayer(Player(endpointId,paquet))
        var playerList = game.players.values
        var playerNameList = mutableListOf<String>()
        for (p in playerList){
            playerNameList.add(p.playerName)
        }
        Log.d(Global.TAG, "Liste des joueurs : $playerNameList")
        broadcast(Payload.fromBytes(playerNameList.toString().toByteArray()))
    }

    override fun broadcast(payload: Payload){
        for (epi in playersEndpointIds){
            connectionsClient.sendPayload(epi, payload)
        }
    }

    override fun isHost(): Boolean {
        return true
    }
}