package me.gm.twaddle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.gm.twaddle.c2s.RespPayload;

public class DisplayChat{

    long chatID;
    String name;

    int unreads;

    long lastMessage;

    String lastMsgPreview;

    long timeLastMsg;

    public DisplayChat(long chatID, String name, int unreads, long lastMessage, String lastMsgPreview, long timeLastMsg) {
        this.chatID = chatID;
        this.name = name;
        this.unreads = unreads;
        this.lastMessage = lastMessage;
        this.lastMsgPreview = lastMsgPreview;
        this.timeLastMsg = timeLastMsg;
    }

    public String getLastMsgPreview() {
        return lastMsgPreview;
    }

    public DisplayChat setLastMsgPreview(String lastMsgPreview) {
        this.lastMsgPreview = lastMsgPreview;
        return this;
    }

    public long getChatID() {
        return chatID;
    }

    public DisplayChat setChatID(long chatID) {
        this.chatID = chatID;
        return this;
    }

    public String getName() {
        return name;
    }

    public DisplayChat setName(String name) {
        this.name = name;
        return this;
    }

    public int getUnreads() {
        return unreads;
    }

    public DisplayChat setUnreads(int unreads) {
        this.unreads = unreads;
        return this;
    }

    public long getLastMessage() {
        return lastMessage;
    }

    public DisplayChat setLastMessage(long lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public long getTimeLastMsg() {
        return timeLastMsg;
    }

    public DisplayChat setTimeLastMsg(long timeLastMsg) {
        this.timeLastMsg = timeLastMsg;
        return this;
    }

    public static DisplayChat fromJSONObject(JSONObject obj){
        DisplayChat res;
        try {
            res = new DisplayChat(
                    obj.getLong("chat_id"),
                    obj.getString("name"),
                    obj.getInt("unreads"),
                    obj.getLong("last_message"),
                    obj.getString("last_msg_preview"),
                    obj.getLong("time_last_msg")
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public static List<DisplayChat> fromPayload(RespPayload pl){
        JSONArray chats;
        ArrayList<DisplayChat> res = new ArrayList<>();
        try {
            chats = pl.getData().getJSONObject("data").getJSONArray("chats");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < chats.length(); i++){
            try {
                res.add(DisplayChat.fromJSONObject(chats.getJSONObject(i)));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

}
