package com.inc.pmu.models;

import org.json.JSONObject;

public class Card implements Jsonisable {
    public static final int MIN_NUMBER = 1 ;
    public static final int MAX_NUMBER = 13 ;

    public final Suit suit ;
    public final int number;

    public Card(Suit suit, int number) {
        this.suit = suit;
        this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Card card = (Card) obj;
        return card.suit == this.suit && card.number == this.number;
    }


    @Override
    public JSONObject toJson() {
        JSONObject card = new JSONObject();
        try {
            card.put("suit", suit.name());
            card.put("number", number);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return card;
    }

    public static Card fromJson(JSONObject json) {
        Suit suit;
        int number;
        try {
            suit = Suit.valueOf(json.getString("suit"));
            number = json.getInt("number");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Card(suit, number);
    }
}
