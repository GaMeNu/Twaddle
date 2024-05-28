package me.gm.twaddle.c2s;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class represents a server event response.
 */
public class RespPayload extends Payload {
    boolean success;

    /**
     * Adds the success boolean value to the Payload
     * @param op
     * @param success
     * @param data
     */
    public RespPayload(int op, boolean success, JSONObject data){
        super(op, data);
        this.success = success;
    }

    /**
     * Whether the server event was successful
     * @return
     */
    public boolean isSuccessful() {
        return success;
    }

    /**
     * Parses a string and converts it to a Response Payload
     * @param s String to parse
     * @return RespPayload object
     * @throws JSONException if parsing failed
     */
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

    /**
     * Takes a JSONObject and converts it to a RespPayload
     * @param response JSONObject to convert
     * @return RespPayload object
     */
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
