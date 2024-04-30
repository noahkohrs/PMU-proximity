package com.inc.pmu.models;

import android.util.Log;

import com.google.android.gms.nearby.connection.Payload;
import com.inc.pmu.TAG;
import com.inc.pmu.viewmodels.Action;
import com.inc.pmu.viewmodels.Param;
import com.inc.pmu.viewmodels.Sender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PayloadMaker implements Jsonisable {

    private final String action;
    private final String source;
    JSONObject params = new JSONObject();

    private PayloadMaker(String action, String source) {
        try {
            this.action = action;
            this.source = source;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PayloadMaker createPayload(Action action, String source) {
        return new PayloadMaker(action.name(), source);
    }
    public PayloadMaker addParam(String key, String value) {
        try {
            params.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    public PayloadMaker addParam(String key, Jsonisable value) {
        try {
            params.put(key, value.toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, int value) {
        try {
            params.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, boolean value) {
        try {
            params.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, JSONObject value) {
        try {
            params.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, String[] strings) {
        JSONArray list = new JSONArray();
        try {
            for (String i : strings) {
                list.put(i);
            }
            params.put(key, list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, Bet bet) {
        try {
            params.put(key, bet.toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, Player p) {
        try {
            params.put(key, p.toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, Card card) {
        try {
            params.put(key, card.toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        try {
            json.put("action", action);
            json.put(Sender.SENDER, source);
            json.put(Param.PARAMS, params);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return json;

    }

    public Payload toPayload() {
        Log.d(TAG.NETWORK, action + " payload created");
        return Payload.fromBytes(toJson().toString().getBytes());
    }
}
