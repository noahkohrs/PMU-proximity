package com.inc.pmu.models;

import org.json.JSONObject;

import java.util.*;

public class Board implements Jsonisable {

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
            deck.remove(new Card(suit, 1));
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
        if (sideCardsDiscoverIndex < LENGTH) {
            sideCardsDiscoverIndex++;
            moveRiderForward(sideCards[sideCardsDiscoverIndex - 1][0].suit);
            moveRiderBackward(sideCards[sideCardsDiscoverIndex - 1][1].suit);
        }

    }

    /**
     * Move the rider forward by one.
     * @param suit the suit of the rider to move
     */
    public void moveRiderForward(Suit suit) {
        int pos = riderPos.get(suit);
        if (pos <= LENGTH) {
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

    @Override
    public JSONObject toJson() {
        JSONObject board = new JSONObject();
        try {
            JSONObject riders = new JSONObject();
            for (Suit suit : Suit.values()) {
                riders.put(suit.name(), riderPos.get(suit));
            }
            board.put("riders", riders);

            JSONObject sideCards = new JSONObject();
            for (int i = 0; i < LENGTH; i++) {
                JSONObject sideCard = new JSONObject();
                sideCard.put("left", this.sideCards[i][0].toJson());
                sideCard.put("right", this.sideCards[i][1].toJson());
                sideCards.put(String.valueOf(i), sideCard);
            }
            board.put("sideCards", sideCards);
            board.put("sideCardsDiscoverIndex", sideCardsDiscoverIndex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return board;
    }

    private Board(Map<Suit, Integer> riderPos, Card[][] sideCards, int sideCardsDiscoverIndex) {
        this.riderPos = riderPos;
        this.sideCards = sideCards;
        this.sideCardsDiscoverIndex = sideCardsDiscoverIndex;
    }

    /**
     * Create a Board from a JSON object.
     * Should be called by the client to create a Board from the host's JSON object.
     * @param json
     * @return
     */
    public static Board fromJson(JSONObject json) {
        Map<Suit, Integer> riders = new HashMap<Suit, Integer>();
        Card[][] sideCards = new Card[LENGTH][2];
        int sideCardsDiscoverIndex;
        try {
            JSONObject ridersJson = json.getJSONObject("riders");
            for (Suit suit : Suit.values()) {
                riders.put(suit, ridersJson.getInt(suit.name()));
            }

            JSONObject sideCardsJson = json.getJSONObject("sideCards");
            for (int i = 0; i < LENGTH; i++) {
                JSONObject sideCard = sideCardsJson.getJSONObject(String.valueOf(i));
                sideCards[i][0] = Card.fromJson(sideCard.getJSONObject("left"));
                sideCards[i][1] = Card.fromJson(sideCard.getJSONObject("right"));
            }
            sideCardsDiscoverIndex = json.getInt("sideCardsDiscoverIndex");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Board(riders, sideCards, sideCardsDiscoverIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Board)) {
            return false;
        }
        Board board = (Board) obj;
        return  board.riderPos.equals(this.riderPos) &&
                Arrays.deepEquals(board.sideCards, this.sideCards) &&
                board.sideCardsDiscoverIndex == this.sideCardsDiscoverIndex;
    }
}
