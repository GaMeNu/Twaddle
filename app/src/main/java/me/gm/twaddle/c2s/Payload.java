package me.gm.twaddle.c2s;

import android.util.Log;

import org.json.*;

public class Payload implements ToJSONObject {
    private short opcode;
    private JSONObject data;

    public enum Events {
        CREATE_USER("CREATE_USER"),
        LOGIN_USER("LOGIN_USER"),
        LOAD_USER_CHATS("LOAD_USER_CHATS"),
        CREATE_USER_CHAT("CREATE_USER_CHAT"),
        REQUEST_MESSAGE_BATCH("REQUEST_MESSAGE_BATCH"),
        SEND_MESSAGE("SEND_MESSAGE")
        ;

        private final String name;

        Events(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public Payload(){};

    public Payload(short opcode) {
        this.opcode = opcode;
    }

    public Payload(short opcode, JSONObject data) {
        this.opcode = opcode;
        this.data = data;
    }

    public JSONObject getData(){
        return this.data;
    }

    public Payload(int opcode, JSONObject data) {
        this.opcode = (short) opcode;
        this.data = data;
    }

    public static Payload event(Events eventType, JSONObject eventData) throws JSONException {
        return new Payload(1,
                new JSONObject()
                .put("event", eventType.getName())
                .put("data", eventData)
        );
    }

    public static Payload fromString(String s) throws JSONException {
        JSONObject pl;
        try {
            pl = new JSONObject(new JSONTokener(s));
        } catch (JSONException e) {
            Log.e("PAYLOAD", "JSON Error:", e);
            throw e;
        }
        return new Payload(pl.getInt("op"), pl.getJSONObject("data"));
    }

    public Payload setOpcode(short opcode){
        this.opcode = opcode;
        return this;
    }

    public Payload setData(JSONObject data){
        this.data = data;
        return this;
    }

    @Override
    public JSONObject serialize() {
        JSONObject res = new JSONObject();
        try {
            res.put("op", opcode);
            res.put("data", data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

}
