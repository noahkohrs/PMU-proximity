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

class ViewModelClient : ViewModelPMU() {

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

    override fun onPlayerDisconnected(endpointId: String) {
        for (l in listeners)
            l.onConnectionLost()
    }

    override fun startDiscovering(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
        stopConnection()
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

    override fun stopConnection() {
        Log.d(TAG, "Stop discovering...")
        connectionsClient.disconnectFromEndpoint(serverId)
        connectionsClient.stopDiscovery()
    }

    override fun broadcast(payload: Payload){
        connectionsClient.sendPayload(serverId, payload)
    }

    override fun onPayloadReceived(endpointId: String, paquet: JSONObject) {
        val sender = paquet.get(Sender.SENDER)
        if (sender == Sender.HOST){
            val params: JSONObject = paquet.get(Param.PARAMS) as JSONObject
            when(paquet.get(Action.ACTION)){

                Action.PLAYER_PUUID -> {
                    val puuid = params.get(Param.PUUID) as String
                    handlePlayerPuuid(puuid)
                }

                Action.PLAYER_LIST -> {
                    val arr : JSONArray = params.getJSONArray(Param.PLAYER_LIST)
                    val r : Array<String> = Array(arr.length()) {_ -> ""}
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

    override fun isHost(): Boolean {
        return false
    }

    override fun handlePlayerUsername(endpointId: String, name: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handlePlayerPuuid(puuid: String) {
        localId = puuid
    }

    override fun handlePlayerList(playerList: Array<String>) {
        var n = 1
        for (p in playerList){
            Log.d(Global.TAG, "Player $n : $p")
            n++
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
        for (p in game.players.values) {
            if (p.bet.number != -1) {
                Log.d(Global.TAG, p.playerName + " : " + p.bet.number + " sur le " + p.bet.suit)
            }
        }
        for (l in listeners)
            l.onBetValidated(bet.suit, game.players.values)
    }

    override fun handleStartGame() {
        for (l in listeners)
            l.onGameStarted()
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
        Log.d(Global.TAG, "${this.game.players.get(puuid)} veut faire reculer le ${this.game.currentCard}")
    }

    override fun handlePushUpsDone(puuid: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleStartVote(puuid: String) {
        for (l in listeners)
            l.onStartVote(puuid)
    }

    override fun handleVote(puuid: String, vote: Boolean) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleVoteResult(puuid: String, result: Boolean) {
        for (l in listeners)
            l.onVoteFinished(puuid, result)
    }

    override fun startBet() {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun bet(number: Int, suit: Suit) {
        val b = Bet(number, suit)
        val json = PayloadMaker
            .createPayloadRequest(Action.BET, Sender.PLAYER)
            .addParam(Param.BET, b)
            .addParam(Param.PUUID, localId)
        connectionsClient.sendPayload(serverId, json.toPayload())
    }

    override fun vote(choice: Boolean) {
        val votePayload = PayloadMaker
            .createPayloadRequest(Action.VOTE, Sender.PLAYER)
            .addParam(Param.PUUID, localId)
            .addParam(Param.VOTE_RESULT, choice)
            .toPayload()
        broadcast(votePayload)
    }

    override fun doPushUps() {
        val doPushUpsPayload = PayloadMaker
            .createPayloadRequest(Action.ASK_DO_PUSH_UPS, Sender.PLAYER)
            .addParam(Param.PUUID, localId)
            .toPayload()
        broadcast(doPushUpsPayload)
    }

    override fun drawCard() {
        throw UnsupportedOperationException("A client can't draw a card")
    }

    override fun startGame() {
        throw UnsupportedOperationException("A client can't start a game")
    }

    override fun pushUpsDone() {
        val pushupDonePayload = PayloadMaker
            .createPayloadRequest(Action.CONFIRM_PUSH_UPS, Sender.PLAYER)
            .addParam(Param.PUUID, localId)
            .toPayload()
        broadcast(pushupDonePayload)
    }
    


}