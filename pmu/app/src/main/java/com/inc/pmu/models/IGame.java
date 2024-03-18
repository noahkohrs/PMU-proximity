package com.inc.pmu.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IGame {
    public void addPlayer(Player player);
    public void removePlayer(String puuid);
    public void cardDrawn(Card card);

    /**
     * @return a schuffled deck of 52 cards.
     */
    public static List<Card> getFullSchuffledDeck() {
        List<Card> fullDeck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (int i = Card.MIN_NUMBER; i <= Card.MAX_NUMBER; i++) {
                fullDeck.add(new Card(suit, i));
            }
        }
        Collections.shuffle(fullDeck);
        return fullDeck;
    }
    public void roundCancelled(String puuid);
}
