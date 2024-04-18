package com.inc.pmu.models;

import android.util.Log;

import com.inc.pmu.TAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Jsonisable, Serializable {

    public static final Bet defaultBet = new Bet(-1, Suit.HEARTS);
    public final String puuid ;
    public final String playerName ;
    public Bet bet = defaultBet;

    public int currentPushUps = 1;

    public Player(String puuid, String name) {
        super();
        this.puuid = puuid;
        this.playerName = name;
    }

    public Player(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    private Player(String puuid, String name, Bet bet) {
        this.puuid = puuid;
        this.playerName = name;
        this.bet = bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public void setBet(int number, Suit suit) {
        this.bet = new Bet(number, suit);
    }

    @Override
    public JSONObject toJson() {
        JSONObject player = new JSONObject();
        try {

            player.put("puuid", puuid);
            player.put("name", playerName);
            player.put("bet", bet.toJson());
        } catch (JSONException e) {
            Log.d(TAG.TAG, "JSON Exception /!\\ Should not happen");
        }
        return player;
    }


    public static Player fromJson(JSONObject json) {
        String puuid;
        String name;
        Bet bet;
        try {
            puuid = json.getString("puuid");
            name = json.getString("name");
            bet = Bet.fromJson(json.getJSONObject("bet"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new Player(puuid, name, bet);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        Player p = (Player) o;
        return p.puuid.equals(this.puuid);
    }
}
