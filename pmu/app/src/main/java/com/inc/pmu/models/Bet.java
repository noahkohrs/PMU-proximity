package com.inc.pmu.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Bet implements Jsonisable{
    public int number ;
    public Suit suit ;

    public Bet(int number, Suit suit) {
        super();
        this.number = number;
        this.suit = suit;
    }

    public JSONObject toJSon() {
        JSONObject bet = new JSONObject();
        try {
            bet.put("number", number);
            bet.put("suit", suit.name());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return bet;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    public static Bet fromJson(JSONObject json) {
        int number;
        Suit suit;
        try {
            number = json.getInt("number");
            suit = Suit.valueOf(json.getString("suit"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new Bet(number, suit);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Bet)) {
            return false;
        }
        Bet bet = (Bet) obj;
        return bet.number == this.number && bet.suit == this.suit;
    }
}
