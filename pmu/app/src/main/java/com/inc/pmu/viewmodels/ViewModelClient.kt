package com.inc.pmu.viewmodels

import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.inc.pmu.BetChoice
import com.inc.pmu.BuildConfig
import com.inc.pmu.Global
import com.inc.pmu.R
import com.inc.pmu.WaitingPage

class ViewModelClient() : ViewModelPMU() {
    private var serverId =  ""

    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    override fun onConnectionResultOK(endpointId: String) {
        serverId = endpointId
        connectionsClient.sendPayload(serverId, Payload.fromBytes(localUsername.toByteArray()))
        for (listener in listeners){
            listener.onConnectionEstablished()
        }
    }

    override fun startDiscovering(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
        Log.d(TAG, "Start discovering...")
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()

        connectionsClient.startDiscovery(
            BuildConfig.APPLICATION_ID,
            endpointDiscoveryCallback,
            discoveryOptions
        ).addOnSuccessListener {
            Log.d(TAG, "Discovering...")
        }.addOnFailureListener {
            Log.d(TAG, "Unable to start discovering")
        }
    }

    override fun startHosting(connectionsClient: ConnectionsClient) {
        throw UnsupportedOperationException("Client cannot host")
    }

    override fun broadcast(payload: Payload){
        throw UnsupportedOperationException("Client cannot broadcast")
    }

    override fun onPayloadReceived(endpointId: String, paquet: String) {
        if (paquet.equals("Un JSON qui ordonne de passer aux bets")){
            Log.d(Global.TAG, "On passe aux bets !")
            /*val fragment = BetChoice.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()*/
        }else{
            Log.d(Global.TAG, "Liste des joueurs : $paquet")
        }
    }

}