package me.gm.twaddle.obj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class describes a single Twaddle Message
 * Messages must have a chat and author assigned to them
 */
public class Message {
    long messageID;
    long chatID;
    long authorID;
    long timeSent;
    String content;

    public Message(long messageID, long chatID, long authorID, long timeSent, String content) {
        this.messageID = messageID;
        this.chatID = chatID;
        this.authorID = authorID;
        this.timeSent = timeSent;
        this.content = content;
    }

    @Deprecated
    public static Message fromID(long messageID){
        return new Message(messageID, 1, 1, 1, "Placeholder Message");
    }

    public static Message fromJSONObject(JSONObject obj){
        try {
            return new Message(
                    obj.getLong("message_id"),
                    obj.getLong("chat_id"),
                    obj.getLong("author_id"),
                    obj.getLong("time_sent"),
                    obj.getString("content")
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public long getMessageID() {
        return messageID;
    }

    public Message setMessageID(long messageID) {
        this.messageID = messageID;
        return this;
    }

    public long getChatID() {
        return chatID;
    }

    public Message setChatID(long chatID) {
        this.chatID = chatID;
        return this;
    }

    public long getAuthorID() {
        return authorID;
    }

    public Message setAuthorID(long authorID) {
        this.authorID = authorID;
        return this;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public Message setTimeSent(long timeSent) {
        this.timeSent = timeSent;
        return this;
    }


    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }
}
