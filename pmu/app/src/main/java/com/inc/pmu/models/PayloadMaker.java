package com.inc.pmu.models;

import com.google.android.gms.nearby.connection.Payload;

import org.json.JSONObject;

public class PayloadMaker implements Jsonisable {

    JSONObject json = new JSONObject();

    private PayloadMaker(String action, String source) {
        try {
            json.put("action", action);
            json.put("source", source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PayloadMaker createPayloadRequest(String action, String source) {
        return new PayloadMaker(action, source);
    }
    public PayloadMaker addParam(String key, String value) {
        try {
            json.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    public PayloadMaker addParam(String key, Jsonisable value) {
        try {
            json.put(key, value.toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, int value) {
        try {
            json.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, boolean value) {
        try {
            json.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public PayloadMaker addParam(String key, JSONObject value) {
        try {
            json.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        return json;
    }

    public Payload toPayload() {
        return Payload.fromBytes(json.toString().getBytes());
    }
}
