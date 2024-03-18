package com.inc.pmu

import com.inc.pmu.models.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [24], manifest = Config.NONE)
class TestBoard {
    @Test
    fun initBoardTest() {
        val deck : List<Card> = IGame.getFullSchuffledDeck();

        assertEquals(deck.size, 52)

        var board : Board = Board(deck)
        for (suit in Suit.entries) {
            assertEquals(board.riderPos[suit], 0)
        }
        assertEquals(board.sideCardsDiscoverIndex, 0)
        for (suit in Suit.entries) {
            board.moveRiderForward(suit)
        }
        assertEquals(board.sideCardsDiscoverIndex, 1)
    }

    @Test
    fun testBoardJsonisation() {
val deck : List<Card> = IGame.getFullSchuffledDeck();
        var board : Board = Board(deck)
        val json = board.toJson()
        val boardRemade = Board.fromJson(json)
        assertEquals(board, boardRemade)
    }
}