package com.inc.pmu.viewmodels

import android.content.Context
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
import com.inc.pmu.models.Bet
import com.inc.pmu.models.Card
import com.inc.pmu.models.Game
import com.inc.pmu.models.Suit
import org.json.JSONArray
import org.json.JSONObject

abstract class ViewModelPMU : ViewModel() {
    var serverId: String = ""
    var localUsername: String = "Default"
    val listeners = mutableSetOf<ViewModelListener>()
    var localPuuid = ""
    lateinit var connectionsClient : ConnectionsClient
    var counter = 0
    lateinit var game : Game
    private var betsUnavailable : ArrayList<Suit> = arrayListOf()
    var context: Context? = null

    private companion object {
        const val TAG = com.inc.pmu.TAG.TAG
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

    abstract fun stopConnection()

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
            Log.d(com.inc.pmu.TAG.TAG, "onConnectionResult")

            when (resolution.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(com.inc.pmu.TAG.TAG, "ConnectionsStatusCodes.STATUS_OK")

                    connectionsClient.stopDiscovery()
                    onConnectionResultOK(endpointId)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d(com.inc.pmu.TAG.TAG, "ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.d(com.inc.pmu.TAG.TAG, "ConnectionsStatusCodes.STATUS_ERROR")
                }
                else -> {
                    Log.d(com.inc.pmu.TAG.TAG, "Unknown status code ${resolution.status.statusCode}")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            onPlayerDisconnected(endpointId)
            Log.d(TAG, "onDisconnected")
        }
    }

    protected abstract fun onPlayerDisconnected(endpointId: String)

    fun onPayloadReceived(endpointId: String, packet: JSONObject) {
        val params: JSONObject = packet.get(Param.PARAMS) as JSONObject
        val actionStr = packet.get("action") as String
        Log.d(com.inc.pmu.TAG.TAG, "Received action: $actionStr")
        val action = Action.valueOf(actionStr)
        when (action){
            Action.CONNEXION_ESTABLISHED -> {
                val state = params.get(Param.GAME_STATE) as String
                handleConnexionEstablished(state)
            }

            Action.PLAYER_LIST -> {
                val arr: JSONArray = params.getJSONArray(Param.PLAYER_LIST)
                val r: Array<String> = Array(arr.length()) { _ -> "" }
                for (i in r.indices) {
                    r[i] = arr.get(i) as String
                }
                handlePlayerList(r)
            }

            Action.START_BET -> {
                val gameObj = params.get(Param.GAME) as JSONObject
                val receivedGame = Game.fromJson(gameObj)
                handleStartBet(receivedGame)
            }

            Action.BET_VALID -> {
                val id = params.get(Param.PUUID) as String
                val betObj = params.get(Param.BET) as JSONObject
                val bet = Bet.fromJson(betObj)
                handleBetValid(id, bet)
            }

            Action.START_GAME -> {
                handleStartGame()
            }

            Action.DRAW_CARD -> {
                val cardObj = params.get(Param.CARD) as JSONObject
                val card = Card.fromJson(cardObj)
                handleDrawCard(card)
            }

            Action.DO_PUSH_UPS -> {
                val id = params.get(Param.PUUID) as String
                handleDoPushUps(id)
            }

            Action.START_VOTE -> {
                val id = params.get(Param.PUUID) as String
                handleStartVote(id)
            }

            Action.VOTE_RESULTS -> {
                val id = params.get(Param.PUUID) as String
                val res = params.get(Param.VOTE_RESULT) as Boolean
                handleVoteResult(id, res)
            }

            Action.GAME_END -> {
                val suit = params.get(Param.GAME_END) as String
                handleGameEnds(suit)
            }

            Action.END_PUSHUPS -> {
                val count = params.get(Param.END_PUSHUPS) as Int
                handleEndPushUps(count)
            }

            Action.PLAYER_PROFILE -> {
                val name: String = params.get(Param.PLAYER_USERNAME) as String
                val puuid: String = params.get(Param.PUUID) as String
                handlePlayerProfile(endpointId, name, puuid)
            }

            Action.BET -> {
                val puuid = params.get(Param.PUUID) as String
                val jsonBet: JSONObject = params.get(Param.BET) as JSONObject
                val bet: Bet = Bet.fromJson(jsonBet)
                handleBet(puuid, bet)
            }

            Action.ASK_DO_PUSH_UPS -> {
                val puuid: String = params.get(Param.PUUID) as String
                handleAskDoPushUps(puuid)
            }

            Action.CONFIRM_PUSH_UPS -> {
                val puuid: String = params.get(Param.PUUID) as String
                handlePushUpsDone(puuid)
            }

            Action.VOTE -> {
                val puuid: String = params.get(Param.PUUID) as String
                val vote: Boolean = params.get(Param.VOTE_RESULT) as Boolean
                handleVote(puuid, vote)
            }
            Action.GAME_PACKET -> {
                val gameObj = params.get(Param.GAME) as JSONObject
                val receivedGame = Game.fromJson(gameObj)
                handleGamePacket(receivedGame)
            }

            Action.GIVE_PUSHUPS -> TODO()
        }
    }

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
    protected abstract fun handlePlayerProfile(endpointId: String, name: String, puuid: String)
    protected abstract fun handleConnexionEstablished(state: String)
    protected abstract fun handlePlayerList(playerList: Array<String>)
    protected abstract fun handleStartBet(game : Game)
    protected abstract fun handleGamePacket(game : Game)
    protected abstract fun handleBet(puuid: String, bet: Bet)
    protected abstract fun handleBetValid(puuid: String, bet: Bet)
    protected abstract fun handleStartGame()
    protected abstract fun handleDrawCard(card:Card)
    protected abstract fun handleAskDoPushUps(puuid: String)
    protected abstract fun handleDoPushUps(puuid: String)
    protected abstract fun handlePushUpsDone(puuid: String)
    protected abstract fun handleStartVote(puuid: String)
    protected abstract fun handleVote(puuid: String, vote: Boolean)
    protected abstract fun handleVoteResult(puuid: String, result: Boolean)
    protected abstract fun handleGameEnds(winner: String)
    protected abstract fun handleGivePushUps(count: Int, target: String)
    protected abstract fun handleEndPushUps(count: Int)


    // Related to casting actions on the game
    abstract fun startBet()
    abstract fun bet(number: Int, suit: Suit)
    abstract fun startGame()
    abstract fun vote(choice: Boolean)
    abstract fun doPushUps()
    abstract fun drawCard()
    abstract fun pushUpsDone()
    abstract fun gameEnds(winner: String)
    abstract fun checkWin(): Boolean
    abstract fun givePushUps(target: String)
    abstract fun endPushUps()

    fun addListener(listener: ViewModelListener){
        listeners.add(listener)
    }
    fun removeListener(listener: ViewModelListener){
        listeners.remove(listener)
    }

    fun removeAllListeners() {
        listeners.clear()
    }
    fun suitUnavailable(suit : Suit) {
        betsUnavailable.add(suit)
    }
    fun getSuitsUnavailable(): ArrayList<Suit> {
        return betsUnavailable
    }

    abstract fun isHost(): Boolean
}