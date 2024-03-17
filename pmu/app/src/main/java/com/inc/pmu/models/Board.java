package com.inc.pmu.models;

import java.util.*;

public class Board {

    public static final int LENGTH = 5;
    public Map<Suit, Integer> riderPos;
    public Card[][] sideCards = new Card[LENGTH][2];
    public int sideCardsDiscoverIndex = 0;

    /**
     * Constructor for the Board class that initializes the deck, riderPos, and sideCards.
     * Should only be called by the host.
     */
    public Board(List<Card> deck) {
        riderPos = new HashMap<Suit, Integer>();
        for (Suit suit : Suit.values()) {
            riderPos.put(suit, 0);
            deck.remove(new Card(suit, 11));
        }

        for (int i = 0; i < LENGTH; i++) {
            sideCards[i][0] = deck.remove(0);
            sideCards[i][1] = deck.remove(0);
        }
    }

    /**
     * Try to enable the side cards.
     */
    public void updateSideCards() {
        //  If every rider are one card above the sideCards, then update the sideCards
        // boolean allRidersAbove = true;
        for (Suit suit : Suit.values()) {
            if (riderPos.get(suit) <= sideCardsDiscoverIndex) {
                // allRidersAbove = false;
                return;
            }
        }
        sideCardsDiscoverIndex++;
        moveRiderForward(sideCards[sideCardsDiscoverIndex-1][0].suit);
        moveRiderBackward(sideCards[sideCardsDiscoverIndex-1][1].suit);

    }

    /**
     * Move the rider forward by one.
     * @param suit the suit of the rider to move
     */
    public void moveRiderForward(Suit suit) {
        int pos = riderPos.get(suit);
        if (pos < LENGTH) {
            riderPos.put(suit, pos + 1);
        }
        this.updateSideCards();
    }

    /**
     * Move the rider backward by one.
     * @param suit the suit of the rider to move
     */
    public void moveRiderBackward(Suit suit) {
        int pos = riderPos.get(suit);
        if (pos > 0) {
            riderPos.put(suit, pos - 1);
        }
    }
}
