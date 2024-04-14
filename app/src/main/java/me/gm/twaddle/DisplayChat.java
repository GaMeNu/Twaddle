package me.gm.twaddle;

public class DisplayChat{

    long chatID;
    String name;

    int unreads;

    long lastMessage;
    long timeLastMsg;

    public DisplayChat(long chatID, String name, int unreads, long lastMessage, long timeLastMsg) {
        this.chatID = chatID;
        this.name = name;
        this.unreads = unreads;
        this.lastMessage = lastMessage;
        this.timeLastMsg = timeLastMsg;
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
}
