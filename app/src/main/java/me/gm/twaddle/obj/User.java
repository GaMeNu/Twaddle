package me.gm.twaddle.obj;

import org.json.JSONException;
import org.json.JSONObject;

import me.gm.twaddle.c2s.ToJSONObject;

/**
 * This class describes a single Twaddle user.
 */
public class User implements ToJSONObject {
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

    public static User fromJSONObject(JSONObject obj){
        try {
            return new User(
                    obj.getLong("user_id"),
                    obj.getString("firebase_id"),
                    obj.getString("user_name"),
                    obj.getString("user_tag")
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public JSONObject serialize() {
        try {
            return new JSONObject()
                    .put("user_id", userID)
                    .put("firebase_id", firebaseUID)
                    .put("user_tag", userTag)
                    .put("user_name", displayName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}