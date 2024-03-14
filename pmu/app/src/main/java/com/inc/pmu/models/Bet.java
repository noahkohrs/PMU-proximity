package com.inc.pmu.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Bet {
    public int number ;
    public Suit suit ;

    public Bet(int number, Suit suit) {
        super();
        this.number = number;
        this.suit = suit;
    }

    public Bet(JSONObject betRep) {
        int number;
        Suit suit;
        try {
            number = betRep.getInt("number");
            suit = Suit.valueOf(betRep.getString("suit"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
}
