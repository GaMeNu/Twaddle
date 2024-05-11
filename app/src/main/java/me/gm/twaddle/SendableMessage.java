package me.gm.twaddle;

import org.json.JSONException;
import org.json.JSONObject;

import me.gm.twaddle.c2s.ToJSONObject;

public class SendableMessage implements ToJSONObject {
    long chatID;
    long authorID;
    long timeSent;
    String content;

    public long getChatID() {
        return chatID;
    }

    public SendableMessage setChatID(long chatID) {
        this.chatID = chatID;
        return this;
    }

    public long getAuthorID() {
        return authorID;
    }

    public SendableMessage setAuthorID(long authorID) {
        this.authorID = authorID;
        return this;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public SendableMessage setTimeSent(long timeSent) {
        this.timeSent = timeSent;
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
                    .put("author_id", authorID)
                    .put("time_sent", timeSent)
                    .put("content", content);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
