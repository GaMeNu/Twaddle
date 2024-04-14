package me.gm.twaddle.obj;

/**
 * This class adds extra specific info about each User per Chat
 */
public class ChatUser {
    long chatID;
    long userID;
    long joinTime;
    long lastReadMessage;

    public ChatUser(long chatID, long userID, long joinTime, long lastReadMessage) {
        this.chatID = chatID;
        this.userID = userID;
        this.joinTime = joinTime;
        this.lastReadMessage = lastReadMessage;
    }

    public long getChatID() {
        return chatID;
    }

    public ChatUser setChatID(long chatID) {
        this.chatID = chatID;
        return this;
    }

    public long getUserID() {
        return userID;
    }

    public ChatUser setUserID(long userID) {
        this.userID = userID;
        return this;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public ChatUser setJoinTime(long joinTime) {
        this.joinTime = joinTime;
        return this;
    }

    public long getLastReadMessage() {
        return lastReadMessage;
    }

    public ChatUser setLastReadMessage(long lastReadMessage) {
        this.lastReadMessage = lastReadMessage;
        return this;
    }
}
