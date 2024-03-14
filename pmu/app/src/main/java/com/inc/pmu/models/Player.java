package com.inc.pmu.models;

import android.util.Log;

import com.inc.pmu.viewmodels.ViewModelClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Player {

    public static final Bet defaultBet = new Bet(1, Suit.HEARTS);

    public final String puuid ;
    public final String playerName ;
    public Bet bet = defaultBet;

    public Player(
            String puuid, String name
    ) {
        super();
        this.puuid = puuid;
        this.playerName = name;
    }

    public Player(JSONObject playerRep) {
        String puuid;
        String name;
        try {
            puuid = playerRep.getString("puuid");
            name = playerRep.getString("name");
            bet = new Bet(playerRep.getJSONObject("bet"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        this.puuid = puuid;
        this.playerName = name;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public void setBet(int number, Suit suit) {
        this.bet = new Bet(number, suit);
    }

    public JSONObject toJSon() {
        JSONObject player = new JSONObject();
        try {

            player.put("puuid", puuid);
            player.put("name", playerName);
            player.put("bet", bet.toJSon());
        } catch (JSONException e) {
            Log.d(Global.TAG, "JSON Exception /!\\ Should not happen");
        }
        return player;
    }
    public byte[] toBytes() {
        JSONObject player = toJSon();
        return player.toString().getBytes(StandardCharsets.UTF_8);
    }
}
