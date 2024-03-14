package com.inc.pmu.models;

import android.util.Log;

import com.inc.pmu.viewmodels.ViewModelClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Player {

    public String puuid ;
    public String playerName ;
    public Player(
            String puuid, String name
    ) {
        super();
        this.puuid = puuid;
        this.playerName = name;
    }

    public JSONObject toJSon() {
        JSONObject main = new JSONObject();
        try {
            main.put("puuid", puuid);
            main.put("name", playerName);
        } catch (JSONException e) {
            Log.d(Global.TAG, "JSON Exception /!\\ Should not happen");
        }
        return main;
    }
    public byte[] toBytes() {
        JSONObject player = toJSon();
        return player.toString().getBytes(StandardCharsets.UTF_8);
    }
}
