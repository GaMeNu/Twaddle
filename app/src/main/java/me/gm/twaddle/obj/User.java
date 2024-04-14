package me.gm.twaddle.obj;

/**
 * This class describes a single Twaddle user.
 */
public class User {
    long userID;
    String firebaseUID;
    String displayName;
    String userTag;

    public User(long userID, String firebaseUID, String displayName, String userTag) {
        this.userID = userID;
        this.firebaseUID = firebaseUID;
        this.displayName = displayName;
        this.userTag = userTag;
    }

    public long getUserID() {
        return userID;
    }

    public User setUserID(long userID) {
        this.userID = userID;
        return this;
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public User setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public User setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getUserTag() {
        return userTag;
    }

    public User setUserTag(String userTag) {
        this.userTag = userTag;
        return this;
    }
}