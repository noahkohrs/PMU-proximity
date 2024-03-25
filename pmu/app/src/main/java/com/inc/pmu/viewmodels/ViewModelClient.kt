package com.inc.pmu.viewmodels

import kotlin.collections.*
import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.inc.pmu.BuildConfig
import com.inc.pmu.Global
import com.inc.pmu.models.Bet
import com.inc.pmu.models.Card
import com.inc.pmu.models.Game
import com.inc.pmu.models.PayloadMaker
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
                Action.PLAYER_LIST -> {
                    val arr : JSONArray = params.getJSONArray(Param.PLAYER_LIST)
                    val r : Array<String> = Array(arr.length()) {i -> ""}
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
                else -> throw UnsupportedOperationException("Not a client action")
            }
        }
    }

    override fun handlePlayerUsername(endpointId: String, name: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handlePlayerPuuid(puuid: String) {
        localPuuid = puuid
    }

    override fun handlePlayerList(playerList: Array<String>) {
        for (p in playerList){
            Log.d(Global.TAG, "Joueur 1 : " + p)
        }

        for (l in listeners)
            l.onPlayerListUpdate(playerList)
    }

    override fun handleStartBet(game: Game) {
        this.game = game
        for (l in listeners)
            l.onBetStart()
    }

    override fun handleBet(puuid: String, bet: Bet) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleBetValid(puuid: String, bet: Bet) {
        game.players[puuid]?.setBet(bet)
        for (l in listeners)
            l.onBetValidated(bet.suit, game.players.values)
    }

    override fun handleStartGame() {
        TODO("Not yet implemented")
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

    override fun handleVoteResult(puuid: String, result: Boolean) {
        TODO("Not yet implemented")
    }

    override fun startBet() {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun bet(number: Int, suit: Suit) {
        val b: Bet = Bet(number, suit)
        val json = PayloadMaker
            .createPayloadRequest(Action.BET, Sender.PLAYER)
            .addParam(Param.BET, b)
            .addParam(Param.PUUID, localId)
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

    override fun isHost(): Boolean {
        return false
    }

}