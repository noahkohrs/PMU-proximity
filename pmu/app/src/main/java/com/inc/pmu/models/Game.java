package com.inc.pmu.models;

import java.util.*;

public class Game {
    
    public Board board;
    public Map<String, Player> players; // key: puuid, value: player
    public List<Card> deck = new ArrayList<Card>();
    public static final List<Card> cardLeftAfterBoardSetup = new ArrayList<Card>();
    

    public Game() {
        List<Card> deck = getFullSchuffledDeck();
        board = new Board(deck);
        cardLeftAfterBoardSetup.addAll(deck);
        players = new HashMap<String, Player>();
    }

    public void addPlayer(Player player) {
        players.put(player.puuid, player);
    }

    public void removePlayer(String puuid) {
        players.remove(puuid);
    }

    /**
     * @return The card that have been drawn
     */
    public Card drawCard() {
        if (deck.size() == 0) {
            deck.addAll(cardLeftAfterBoardSetup);
        }
        return deck.remove(0);
    }    
    /**
     * @return a full deck of cards
     */
    public static List<Card> getFullSchuffledDeck() {
        List<Card> fullDeck = new ArrayList<Card>();
        for (Suit suit : Suit.values()) {
            for (int i = Card.MIN_NUMBER; i <= Card.MAX_NUMBER; i++) {
                fullDeck.add(new Card(suit, i));
            }
        }
        Collections.shuffle(fullDeck);
        return fullDeck;
    }
}
