package me.gm.twaddle.c2s;

import org.json.JSONException;
import org.json.JSONObject;

public class RespPayload extends Payload {
    boolean success;

    public RespPayload(int op, boolean success, JSONObject data){
        super(op, data);
        this.success = success;
    }

    public static RespPayload fromResponse(JSONObject response){
        RespPayload res;
        try {
            res = new RespPayload(response.getInt("op"), response.getBoolean("s"), response.getJSONObject("data"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
