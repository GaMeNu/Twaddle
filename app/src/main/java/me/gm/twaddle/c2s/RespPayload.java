package me.gm.twaddle.c2s;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RespPayload extends Payload {
    boolean success;

    public RespPayload(int op, boolean success, JSONObject data){
        super(op, data);
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }

    public static RespPayload fromString(String s) throws JSONException {
        JSONObject pl;
        try {
            pl = new JSONObject(new JSONTokener(s));
        } catch (JSONException e) {
            Log.e("PAYLOAD", "JSON Error:", e);
            throw e;
        }
        return new RespPayload(
                pl.getInt("op"),
                pl.getJSONObject("data").getBoolean("s"),
                pl.getJSONObject("data")
        );
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
