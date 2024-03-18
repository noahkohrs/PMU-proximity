package com.inc.pmu.models;

import org.json.JSONObject;

public interface Jsonisable {
    public JSONObject toJson();
    public static Jsonisable fromJson(JSONObject json) {
        throw new RuntimeException("This method should be overwritten by the class implementing Jsonisable");
    }
}
