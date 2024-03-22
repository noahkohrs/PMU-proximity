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
import com.inc.pmu.models.Bet
import com.inc.pmu.models.PayloadMaker
import org.json.JSONObject

class ViewModelClient() : ViewModelPMU() {
    private var serverId =  ""

    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    override fun onConnectionResultOK(endpointId: String) {
        serverId = endpointId
        val json = PayloadMaker.createPayloadRequest(Action.PLAYER_USERNAME, Sender.PLAYER).addParam(Param.PLAYER_USERNAME,localUsername)
        connectionsClient.sendPayload(serverId, json.toPayload())
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

    override fun onPayloadReceived(endpointId: String, paquet: JSONObject) {
        val sender = paquet.get(Sender.SENDER)
        if (sender == Sender.HOST){
            val params: JSONObject = paquet.get(Param.PARAMS) as JSONObject
            when(paquet.get(Action.ACTION)){
                Action.PLAYER_USERNAME -> {
                    val name: String = params.get(Param.PLAYER_USERNAME) as String
                    handlePlayerUsername(name)
                }
                Action.BET -> {
                    val b = params.get(Param.BET)
                    val bet: Bet = Bet.fromJson(paquet)
                }
            }
        }
    }

    override fun handlePlayerUsername(name: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleBet(puuid: String, bet: Bet) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleAskDoPushUps(puuid: String) {
        throw UnsupportedOperationException("Not a client action")
    }

}