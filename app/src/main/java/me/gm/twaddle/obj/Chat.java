package me.gm.twaddle.obj;

/**
 * This class describes a single Twaddle Chat
 */
public class Chat {
    long chatID;
    String name;
    long creationTimestamp;

    int[] usersIDs;

    boolean isGroupChat;

    public Chat(long chatID, String name, long creationTimestamp) {
        this.chatID = chatID;
        this.name = name;
        this.creationTimestamp = creationTimestamp;
    }

    public long getChatID() {
        return chatID;
    }

    public Chat setChatID(long chatID) {
        this.chatID = chatID;
        return this;
    }

    public String getName() {
        return name;
    }

    public Chat setName(String name) {
        this.name = name;
        return this;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public Chat setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }
}
