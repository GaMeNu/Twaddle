package me.gm.twaddle.obj;

import org.json.JSONObject;

public interface ToJSONObject {

    /**
     * Convert the object to a JSONObject form
     * @return
     */
    JSONObject serialize();
}
