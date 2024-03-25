package com.inc.pmu.viewmodels

import kotlin.collections.*
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
import com.inc.pmu.models.Card
import com.inc.pmu.models.Game
import com.inc.pmu.models.PayloadMaker
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit
import org.json.JSONArray
import org.json.JSONObject

class ViewModelClient() : ViewModelPMU() {

    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    override fun onConnectionResultOK(endpointId: String) {
        for (l in listeners)
            l.onConnectionEstablished()
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
                Action.BET -> {
                    val b = params.get(Param.BET)
                    val bet: Bet = Bet.fromJson(paquet)
                    val id: String = params.getString(Param.PUUID)
                    handleBet(id, bet)
                }
                Action.PLAYER_LIST -> {
                    val arr : JSONArray = params.getJSONArray(Param.PLAYER_LIST)
                    val r : Array<String> = Array(arr.length()) {i -> ""}
                    for (i in r.indices) {
                        r[i] = arr.get(i) as String
                    }
                    handlePlayerList(r)
                }
                Action.START_BET -> {
                    handleStartBet()
                }
            }
        }
    }

    override fun handlePlayerUsername(name: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handlePlayerList(playerList: Array<String>) {
        for (p in playerList){
            Log.d(Global.TAG, "Joueur 1 : " + p)
        }

        for (l in listeners)
            l.onPlayerListUpdate(playerList)
    }

    override fun handleStartBet() {
        for (l in listeners)
            l.onBetStart()
    }

    override fun handleBet(puuid: String, bet: Bet) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleBetValid(puuid: String, bet: Bet) {
        game.players[puuid]?.setBet(bet)
        /*for (l in listeners)
            l.onBetValidated()*/
    }

    override fun handleCreateGame(game: Game) {
        this.game = game
        for (l in listeners)
            l.onGameCreated()
    }

    override fun handleDrawCard(card: Card) {
        game.cardDrawn(card)
        for (l in listeners)
            l.onCardDrawn(card)
    }

    override fun handleAskDoPushUps(puuid: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleDoPushUps(puuid: String) {
        for (l in listeners)
            l.onPlayerDoingPushUps(puuid)
    }

    override fun handleStartVote(puuid: String) {
        for (l in listeners)
            l.onStartVote()
    }

    override fun handleVote(puuid: String, vote: Boolean) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun startBet() {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun bet(number: Int, suit: Suit) {
        val b: Bet = Bet(number, suit)
        val json = PayloadMaker.createPayloadRequest(Action.BET, Sender.PLAYER).addParam(
            Param.BET, b).addParam(Param.PUUID, localId)
        connectionsClient.sendPayload(serverId, json.toPayload())
    }

    override fun vote(choice: Boolean) {
        TODO("Not yet implemented")
    }

    override fun doPushUps() {
        TODO("Not yet implemented")
    }

    override fun pushUpsDone() {
        TODO("Not yet implemented")
    }

    override fun handleVoteResult(result: Boolean) {
        TODO("Not yet implemented")
    }

}