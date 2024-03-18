package com.inc.pmu

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import com.inc.pmu.models.*
import org.json.JSONObject

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [24], manifest = Config.NONE)
class TestGameLoop {

    /**
     * Integration test for the game loop
     */
    @Test
    fun testClassicGameLoop() {

        val p1: Player = Player("player 1");
        val p2: Player = Player("player 2");
        val players : List<Player> = listOf(p1, p2);

        // Host game from the perspective of player 1
        val hostGame: HostGame = HostGame(players);
        val hostGameJSON : JSONObject = hostGame.toJson();
        val game : Game = Game.fromJson(hostGameJSON);

        // Equivalent to the game from the perspective of player 2
        assertEquals(hostGame.players.size, game.players.size);
        assertEquals(hostGame.players, game.players);
        assertEquals(hostGame.board, game.board);

        val card : Card = hostGame.drawCard()
        hostGame.cardDrawn(card)
        assertNotEquals(hostGame.board, game.board);
        // Let's suppose a CARD_DRAWN event is sent to the other player
        game.cardDrawn(card)
        assertEquals(hostGame.board, game.board);

        // Player 1 cancels the round
        game.roundCancelled(p1.puuid)
        assertNotEquals(hostGame.board, game.board);
        // Let's suppose a ROUND_CANCELLED event is sent to the other player
        hostGame.roundCancelled(p1.puuid)
        assertEquals(hostGame.board, game.board);

        // Nothing to do.
        hostGame.drawCard()
        hostGame.cardDrawn(card)
        game.cardDrawn(card)
        assertEquals(hostGame.board, game.board);
    }
}