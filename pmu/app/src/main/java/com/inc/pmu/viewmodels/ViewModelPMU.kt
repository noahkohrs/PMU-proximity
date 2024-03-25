package com.inc.pmu.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.inc.pmu.Global
import com.inc.pmu.models.Bet
import com.inc.pmu.models.Card
import com.inc.pmu.models.Game
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit
import org.json.JSONObject
import java.util.UUID

abstract class ViewModelPMU : ViewModel() {
    var serverId: String = ""
    var localUsername: String = "Default"
    var localPuuid = ""
    val listeners = mutableListOf<ViewModelListener>()
    val localId : String = UUID.randomUUID().toString()
    lateinit var connectionsClient : ConnectionsClient
    lateinit var game : Game
    var counter = 0

    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    /**
     * Set the connectionClient to connectionClient Start discovering for other devices
     * @param connectionsClient the connectionClient to use
     */
    abstract fun startDiscovering(connectionsClient: ConnectionsClient)


    /**
     * Set the connectionClient to connectionClient Start hosting for other devices
     * @param connectionsClient the connectionClient to use
     */
    abstract fun startHosting(connectionsClient: ConnectionsClient)

    protected val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "onEndpointFound")
            Log.d(TAG, "Requesting connection...")
            connectionsClient.requestConnection(
                localUsername,
                endpointId,
                connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "Successfully requested a connection")
            }.addOnFailureListener {
                Log.d(TAG, "Failed to request the connection")
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "onEndpointLost")
        }
    }

    abstract fun onConnectionResultOK(endpointId: String)

    protected val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "onConnectionInitiated")

            Log.d(TAG, "Accepting connection...")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
            Log.d(Global.TAG, "onConnectionResult")

            when (resolution.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(Global.TAG, "ConnectionsStatusCodes.STATUS_OK")

                    connectionsClient.stopDiscovery()
                    onConnectionResultOK(endpointId)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d(Global.TAG, "ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.d(Global.TAG, "ConnectionsStatusCodes.STATUS_ERROR")
                }
                else -> {
                    Log.d(Global.TAG, "Unknown status code ${resolution.status.statusCode}")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "onDisconnected")
        }
    }

    abstract fun onPayloadReceived(endpointId: String, paquet: JSONObject)

    protected val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                payload.asBytes()?.let {
                    val message = String(it)
                    val json = JSONObject(message)
                    onPayloadReceived(endpointId,json)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    abstract fun broadcast(payload: Payload)

    // Handeling Paquets Related
    abstract fun handlePlayerUsername(endpointId: String, name: String)
    abstract fun handlePlayerPuuid(puuid: String)
    abstract fun handlePlayerList(playerList: Array<String>)
    abstract fun handleStartBet()
    abstract fun handleBet(puuid: String, bet: Bet)
    abstract fun handleBetValid(puuid: String, bet: Bet)
    abstract fun handleCreateGame(game:Game)
    abstract fun handleDrawCard(card:Card)
    abstract fun handleAskDoPushUps(puuid: String)
    abstract fun handleDoPushUps(puuid: String)
    abstract fun handleStartVote(puuid: String)
    abstract fun handleVote(puuid: String, vote: Boolean)
    abstract fun handleVoteResult(puuid: String, result: Boolean)

    // Related to casting actions on the game
    abstract fun startBet()
    abstract fun bet(number: Int, suit: Suit)
    abstract fun vote(choice: Boolean)
    abstract fun doPushUps()
    abstract fun pushUpsDone()

    // Related to casting actions on the game
    abstract fun startBet()
    abstract fun bet(number: Int, suit: Suit)
    abstract fun vote(choice: Boolean)
    abstract fun doPushUps()
    abstract fun pushUpsDone()

    // Related to casting actions on the game
    abstract fun startBet()
    abstract fun bet(number: Int, suit: Suit)
    abstract fun vote(choice: Boolean)
    abstract fun doPushUps()
    abstract fun pushUpsDone()


    fun addListener(listener: ViewModelListener){
        listeners.add(listener)
    }
    fun removeListener(listener: ViewModelListener){
        listeners.remove(listener)
    }




}