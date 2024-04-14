package me.gm.twaddle.c2s;

import org.json.*;

public class Payload implements ToJSONObject {
    private short opcode;
    private JSONObject data;

    public Payload(){};

    public Payload(short opcode) {
        this.opcode = opcode;
    }

    public Payload(short opcode, JSONObject data) {
        this.opcode = opcode;
        this.data = data;
    }

    public Payload(int opcode, JSONObject data) {
        this.opcode = (short) opcode;
        this.data = data;
    }

    public static Payload fromString(String s) throws JSONException {
        JSONObject pl = new JSONObject(s);
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
