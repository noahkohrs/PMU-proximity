package com.inc.pmu.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Should only be used by the host.
 */
public class HostGame extends Game {
    public List<Card> deck = new ArrayList<Card>();
    public static final List<Card> cardLeftAfterBoardSetup = new ArrayList<Card>();

    public HostGame(List<Player> players) {
        super(players);
        deck.addAll(IGame.getFullSchuffledDeck());
        board = new Board(deck);
        cardLeftAfterBoardSetup.addAll(deck); // Should never be modified
    }
    /**
     * @return The card that have been drawn.
     * Should only be used by the owner.
     */
    public Card drawCard() {
        if (deck.size() == 0) {
            deck.addAll(cardLeftAfterBoardSetup);
        }
        return deck.remove(0);
    }
}
