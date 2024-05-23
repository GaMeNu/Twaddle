package me.gm.twaddle;

import org.json.JSONException;
import org.json.JSONObject;

import me.gm.twaddle.c2s.ToJSONObject;

public class SendableMessage implements ToJSONObject {
    long chatID;
    String content;

    public long getChatID() {
        return chatID;
    }

    public SendableMessage setChatID(long chatID) {
        this.chatID = chatID;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SendableMessage setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public JSONObject serialize() {
        try {
            return new JSONObject()
                    .put("chat_id", chatID)
                    .put("content", content);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
