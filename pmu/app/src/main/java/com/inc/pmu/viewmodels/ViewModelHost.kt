package com.inc.pmu.viewmodels

import android.os.CountDownTimer
import android.util.Log
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.inc.pmu.BuildConfig
import com.inc.pmu.Const
import com.inc.pmu.Global
import com.inc.pmu.models.Bet
import com.inc.pmu.models.Board
import com.inc.pmu.models.Card
import com.inc.pmu.models.Game
import com.inc.pmu.models.HostGame
import com.inc.pmu.models.PayloadMaker
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit
import com.inc.pmu.models.Validator
import org.json.JSONObject
import java.util.UUID
import kotlin.math.abs

class ViewModelHost() : ViewModelPMU() {
    private var playersEndpointIds = HashMap<String, String>()
    private val automata: VMStateMachine = VMStateMachine()
    private var validator: Validator = Validator.doneValidator() // Just to init
    private var winners = mutableListOf<Player>()
    private var cptWinners = 0
    private companion object {
        const val TAG = Global.TAG
        val STRATEGY = Strategy.P2P_STAR
    }

    override fun onConnectionResultOK(endpointId: String) {
        playersEndpointIds[endpointId] = UUID.randomUUID().toString()
        Log.d(Global.TAG, playersEndpointIds.toString())
    }

    override fun onPlayerDisconnected(endpointId: String) {
        Log.d(Global.TAG, "Player disconnected")
        game.players.remove(playersEndpointIds[endpointId])
        playersEndpointIds.remove(endpointId)

        val playerList = game.players.values
        val playerNameList = mutableListOf<String>()
        for (p in playerList){
            playerNameList.add(p.playerName)
        }
        for (l in listeners)
            l.onPlayerListUpdate(playerNameList.toTypedArray())

        val playerListPayload = PayloadMaker
            .createPayloadRequest(Action.PLAYER_LIST, Sender.HOST)
            .addParam(Param.PLAYER_LIST, playerNameList.toTypedArray())
            .toPayload()
        broadcast(playerListPayload)

        Log.d(Global.TAG, playersEndpointIds.toString())
    }

    override fun startDiscovering(connectionsClient: ConnectionsClient) {
        throw UnsupportedOperationException("Host cannot discover")
    }


    override fun startHosting(connectionsClient: ConnectionsClient) {
        this.connectionsClient = connectionsClient
        stopConnection()
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

    override fun stopConnection() {
        for (epi in playersEndpointIds.keys){
            connectionsClient.disconnectFromEndpoint(epi)
        }
        connectionsClient.stopAdvertising()
    }

    override fun broadcast(payload: Payload){
        for (epi in playersEndpointIds.keys){
            connectionsClient.sendPayload(epi, payload)
        }
    }

    override fun isHost(): Boolean {
        return true
    }

    override fun handlePlayerUsername(endpointId: String,name: String) {
        if (!automata.isGameSetup) {
            return
        }
        val newPlayer: Player = Player(playersEndpointIds[endpointId], name)
        val puuidPayload = PayloadMaker
            .createPayloadRequest(Action.PLAYER_PUUID, Sender.HOST)
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
        if (!automata.isGameSetup) {
            return
        }
        val player = game.players.get(puuid)
        if (player != null){
            player.setBet(bet)
        }

        for (p in game.players.values){
            if (p.bet.number != -1){
                Log.d(Global.TAG, p.playerName + " : " + p.bet.number + " sur le " + p.bet.suit)
            }
        }
        for (l in listeners)
            l.onBetValidated(bet.suit, game.players.values)

        val info = PayloadMaker
            .createPayloadRequest(Action.BET_VALID, Sender.HOST)
            .addParam(Param.PUUID, puuid)
            .addParam(Param.BET, bet)
            .toPayload()

        broadcast(info)
    }

    override fun handleBetValid(puuid: String, bet: Bet) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handleStartGame() {
        throw UnsupportedOperationException("Not an host action")
    }



    override fun handleAskDoPushUps(puuid: String) {
        if (!automata.isCardDrawn) {
            return
        }
        automata.notifyAskForPushUps()

        // Make a new validator
        validator = Validator(puuid, game.players.keys)

        // Time limit for the players to vote
        //val timer = java.util.Timer()
        //timer.schedule(object : java.util.TimerTask() {
        //    override fun run() {
        //        if (validator.hasEveryoneVoted()) {
        //            return
        //        }
        //        voteEnded()
        //    }
        //}, Const.MAX_TIME_TO_VOTE)

        val json = PayloadMaker
            .createPayloadRequest(Action.DO_PUSH_UPS, Sender.HOST)
            .addParam(Param.PUUID,puuid)
        broadcast(json.toPayload())
        for (l in listeners)
            l.onPlayerDoingPushUps(puuid);
    }

    override fun handlePlayerList(playerList: Array<String>) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handleStartBet(game : Game) {
        throw UnsupportedOperationException("Not an host action")
    }
    override fun handleDrawCard(card: Card) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handleDoPushUps(puuid: String) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handlePushUpsDone(puuid: String) {
        if (!(automata.isDoingPushUps && validator.votedPlayerPuuid == puuid)) {
            return
        }
        automata.notifyConfirmPushUps()

        val startVotePayload = PayloadMaker
            .createPayloadRequest(Action.START_VOTE, Sender.HOST)
            .addParam(Param.PUUID, puuid)
            .toPayload()
        broadcast(startVotePayload)

        for (l in listeners)
            l.onStartVote(puuid)
    }

    override fun handleStartVote(puuid: String) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handleVote(puuid: String, vote: Boolean) {
        if (!automata.isValidatingPlayer)
            return

        validator.vote(puuid, vote);
        if (validator.hasEveryoneVoted()) {
            voteEnded()
        }
    }

    private fun voteEnded() {
        val result = validator.result
        val voteResultPayload = PayloadMaker
            .createPayloadRequest(Action.VOTE_RESULTS, Sender.HOST)
            .addParam(Param.VOTE_RESULT, result)
            .addParam(Param.PUUID, validator.votedPlayerPuuid)
            .toPayload()

        broadcast(voteResultPayload)
        if (result) {
            game.roundCancelled(validator.votedPlayerPuuid)
            for (l in listeners)
                l.onBoardUpdate()
        }
        for (l in listeners)
            l.onVoteFinished(validator.votedPlayerPuuid, result)
        if (result) {
            automata.notifyVoteSuccess()
        } else {
            automata.notifyVoteFail()
        }
    }

    override fun handleVoteResult(puuid: String, result: Boolean) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handleGameEnds(winner: String) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun handleGivePushUps(count: Int, target: String) {
        for (p in game.players.values){
            if (game.players[localId]!!.bet.suit.name == target){
                p.setBet(p.bet.number+count,p.bet.suit)
            }
        }
        cptWinners += 1
        if (cptWinners >= winners.size){
            EndPushUps()
        }
    }

    override fun handleEndPushUps(count: Int) {
        throw UnsupportedOperationException("Not an host action")
    }

    override fun startBet() {
        val info = PayloadMaker
            .createPayloadRequest(Action.START_BET, Sender.HOST)
            .addParam(Param.GAME, game)
            .toPayload()
        broadcast(info)
        for (l in listeners)
            l.onBetStart()
    }

    override fun bet(number: Int, suit: Suit) {
        val b = Bet(number, suit)
        handleBet(localId, b)
    }

    override fun startGame() {
        if (!automata.isGameSetup)
            return
        automata.notifyStartGame()

        val info = PayloadMaker
            .createPayloadRequest(Action.START_GAME, Sender.HOST)
            .toPayload()
        broadcast(info)
        for (l in listeners)
            l.onGameStarted()
    }

    override fun vote(choice: Boolean) {
        handleVote(localId, choice)
    }

    override fun doPushUps() {
        handleAskDoPushUps(localId)
    }

    override fun drawCard() {
        if (!(automata.isWaitingForDrawing || automata.isCardDrawn)) {
            Log.d(Global.TAG, "Trying to draw in the wrong state")
            return
        }
        automata.notifyDrawCard()

        val hostGame: HostGame = game as HostGame
        val card: Card = hostGame.drawCard()
        game.cardDrawn(card)

        val payload = PayloadMaker
            .createPayloadRequest(Action.DRAW_CARD, Sender.HOST)
            .addParam(Param.CARD, card)
            .toPayload()
        broadcast(payload)

        for (l in listeners) {
            l.onCardDrawn(card)
            l.onBoardUpdate()
        }
    }

    override fun pushUpsDone() {
        handlePushUpsDone(localId)
    }

    override fun gameEnds(winner: String) {
        for (p in game.players.values) {
            if (p.bet.suit.name == winner) {
                winners.add(p)
            } else {
                val bet = p.bet
                val ranking = game.board.riderPos[bet.suit]
                p.setBet(bet.number * abs(ranking!! - (Board.LENGTH + 1)), bet.suit)
                Log.d(Global.TAG, "player : ${p.playerName}, bet : ${bet.number}, classement : ${abs(ranking!! - (Board.LENGTH + 1))}")
            }
        }

        val payload = PayloadMaker
            .createPayloadRequest(Action.GAME_END, Sender.HOST)
            .addParam(Param.GAME_END, winner)
            .toPayload()
        broadcast(payload)

        for (l in listeners)
            l.onGameEnds(winner)

        if (winners.isEmpty()){
            val timer = object: CountDownTimer(5000, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    //affiche les secondes sur le deck transparent
                }
                override fun onFinish() {
                    EndPushUps()
                }
            }
            timer.start()
        }
    }

    override fun checkWin(): Boolean {
        var winner: String? = null
        for (suit in Suit.entries){
            if (game.board.riderPos[suit] == Board.LENGTH + 1){
                winner = suit.name
            }
        }
        if (winner != null){
            gameEnds(winner)
            return true
        }
        return false
    }

    override fun givePushUps(target: String) {
        handleGivePushUps(game.players[localId]!!.bet.number,target)
    }

    override fun EndPushUps() {
        for (suit in game.board.riderPos.keys){
            Log.d(Global.TAG, suit.name + " : " +game.board.riderPos[suit].toString())
        }
        for (endPoint in playersEndpointIds.keys){
            val id = playersEndpointIds[endPoint]
            val p = game.players[id]
            if (p !in winners){
                val count = p!!.bet.number
                val payload = PayloadMaker
                    .createPayloadRequest(Action.END_PUSHUPS, Sender.HOST)
                    .addParam(Param.END_PUSHUPS, count)
                    .toPayload()
                connectionsClient.sendPayload(endPoint, payload)
            }
        }

        val p = game.players[localId]
        if (p !in winners){
            val count = p!!.bet.number
            for (l in listeners)
                l.onEndPushUps(count)
        }
    }
}