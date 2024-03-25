package com.inc.pmu.viewmodels

import android.util.Log
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.inc.pmu.BuildConfig
import com.inc.pmu.Global
import com.inc.pmu.models.Bet
import com.inc.pmu.models.Card
import com.inc.pmu.models.Game
import com.inc.pmu.models.PayloadMaker
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit
import org.json.JSONObject

class ViewModelHost() : ViewModelPMU() {
    private var playersEndpointIds = mutableListOf<String>()

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
            //this.players.add(localUsername)
            Log.d(TAG, "Advertising...")
        }.addOnFailureListener {
            // 7
            Log.d(TAG, "Unable to start advertising")
        }
    }

    override fun onPayloadReceived(endpointId: String, paquet: JSONObject) {
        Log.d(Global.TAG, paquet.toString())
        val sender = paquet.get(Sender.SENDER) as String
        if (sender == Sender.PLAYER){
            val params: JSONObject = paquet.get(Param.PARAMS) as JSONObject
            when(paquet.get(Action.ACTION)){

                Action.PLAYER_USERNAME -> {
                    val name: String = params.get(Param.PLAYER_USERNAME) as String
                    handlePlayerUsername(endpointId, name)
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
                }
            }
        }
    }

    override fun broadcast(payload: Payload){
        for (epi in playersEndpointIds){
            connectionsClient.sendPayload(epi, payload)
        }
    }

    override fun handlePlayerUsername(endpointId: String,name: String) {
        val newPlayer: Player = Player(name)

        val puuidPayload = PayloadMaker
            .createPayloadRequest(Action.PLAYER_PUUID, Sender.SENDER)
            .addParam(Param.PUUID, newPlayer.puuid)
            .toPayload()

        connectionsClient.sendPayload(endpointId, puuidPayload)

        game.addPlayer(newPlayer)
        val playerList = game.players.values
        val playerNameList = mutableListOf<String>()
        for (p in playerList){
            playerNameList.add(p.playerName)
        }
        Log.d(Global.TAG, playerNameList.toString())
        val jsonList = PayloadMaker.createPayloadRequest(Action.PLAYER_LIST, Sender.HOST).addParam(Param.PLAYER_LIST,playerNameList.toTypedArray())
        broadcast(jsonList.toPayload())
        for (l in listeners)
            l.onPlayerListUpdate(playerNameList.toTypedArray())
    }

    override fun handlePlayerPuuid(puuid: String) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handleBet(puuid: String, bet: Bet) {
        val player = game.players[puuid]
        player?.setBet(bet)
        for (l in listeners)
            l.onBetValidated(bet.suit, game.players.values)

        val info = PayloadMaker
            .createPayloadRequest(Action.BET_VALID, Sender.HOST)
            .addParam(Param.BET, bet)
            .toPayload()

        broadcast(info)
    }

    override fun handleBetValid(puuid: String, bet: Bet) {
        TODO("Not yet implemented")
    }


    override fun handleAskDoPushUps(puuid: String) {
        val json = PayloadMaker.createPayloadRequest(Action.DO_PUSH_UPS, Sender.HOST).addParam(Param.PUUID,puuid)
        broadcast(json.toPayload())
    }

    override fun handlePlayerList(playerList: Array<String>) {
        TODO("Not yet implemented")
    }

    override fun handleStartBet() {
        TODO("Not yet implemented")
    }

    override fun handleCreateGame(game: Game) {
        TODO("Not yet implemented")
    }

    override fun handleDrawCard(card: Card) {
        TODO("Not yet implemented")
    }

    override fun handleDoPushUps(puuid: String) {
        TODO("Not yet implemented")
    }

    override fun handleStartVote(puuid: String) {
        TODO("Not yet implemented")
    }

    override fun handleVote(puuid: String, vote: Boolean) {
        TODO("Not yet implemented")
    }

    override fun handleVoteResult(puuid: String, result: Boolean) {
        TODO("Not yet implemented")
    }

    override fun startBet() {
        val info = PayloadMaker
            .createPayloadRequest(Action.START_BET, Sender.HOST)
            .toPayload()
        broadcast(info)
    }

    override fun bet(number: Int, suit: Suit) {
        val b = Bet(number, suit)
        handleBet(localPuuid, b)
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
        return true
    }

}