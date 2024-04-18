package com.inc.pmu.viewmodels

import android.provider.Settings
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

class ViewModelClient : ViewModelPMU() {

    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    override fun onConnectionResultOK(endpointId: String) {
        serverId = endpointId
        localPuuid = Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "Local PUUID = $localPuuid")
        val json = PayloadMaker
            .createPayload(Action.PLAYER_PROFILE, Sender.PLAYER)
            .addParam(Param.PLAYER_USERNAME,localUsername)
            .addParam(Param.PUUID, localPuuid)
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



    override fun isHost(): Boolean {
        return false
    }

    override fun handlePlayerProfile(endpointId: String, name: String, puuid: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleConnexionEstablished(state: String) {
        for (l in listeners)
            l.onConnectionEstablished(state)
        for (l in listeners)
            l.onPlayerListUpdate(game.players.keys.toTypedArray())
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

    override fun handleGamePacket(game: Game) {
        this.game = game
        for (l in listeners)
            l.onBoardUpdate()
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
        for (l in listeners) {
            l.onCardDrawn(card)
            l.onBoardUpdate()
        }

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
        if (result)
            game.roundCancelled(puuid)
        for (l in listeners) {
            l.onVoteFinished(puuid, result)
            l.onBoardUpdate()
        }
    }

    override fun handleGameEnds(winner: String) {
        for (l in listeners)
            l.onGameEnds(winner)
    }

    override fun handleGivePushUps(count: Int, target: String) {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun handleEndPushUps(count: Int) {
        for (l in listeners)
            l.onEndPushUps(count)
    }

    override fun startBet() {
        throw UnsupportedOperationException("Not a client action")
    }

    override fun bet(number: Int, suit: Suit) {
        val b = Bet(number, suit)
        val json = PayloadMaker
            .createPayload(Action.BET, Sender.PLAYER)
            .addParam(Param.BET, b)
            .addParam(Param.PUUID, localPuuid)
        connectionsClient.sendPayload(serverId, json.toPayload())
    }

    override fun vote(choice: Boolean) {
        val votePayload = PayloadMaker
            .createPayload(Action.VOTE, Sender.PLAYER)
            .addParam(Param.PUUID, localPuuid)
            .addParam(Param.VOTE_RESULT, choice)
            .toPayload()
        broadcast(votePayload)
    }

    override fun doPushUps() {
        val doPushUpsPayload = PayloadMaker
            .createPayload(Action.ASK_DO_PUSH_UPS, Sender.PLAYER)
            .addParam(Param.PUUID, localPuuid)
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
            .createPayload(Action.CONFIRM_PUSH_UPS, Sender.PLAYER)
            .addParam(Param.PUUID, localPuuid)
            .toPayload()
        broadcast(pushupDonePayload)
    }

    override fun gameEnds(winner: String) {
        throw UnsupportedOperationException("A client can't end the game")
    }

    override fun checkWin(): Boolean {
        throw UnsupportedOperationException("A client can't check win")
    }

    override fun givePushUps(target: String) {
        val payload = PayloadMaker
            .createPayload(Action.GIVE_PUSHUPS, Sender.PLAYER)
            .addParam(Param.NB_GIVE_PUSHUPS, game.players.get(localPuuid)!!.bet.number)
            .addParam(Param.TGT_GIVE_PUSHUPS, target)
            .toPayload()
        broadcast(payload)
    }

    override fun endPushUps() {
        throw UnsupportedOperationException("A client can't distribute end push ups")
    }



}