package me.gm.twaddle;

import me.gm.twaddle.c2s.WSAPI;
import me.gm.twaddle.obj.User;

public class WSInstanceManager {

    /**
     * Container class for the data of a logged in user.
     */
    public static class UserData {
        private int userID;
        private String firebaseID;
        private String username;
        private String userTag;

        public UserData(int userID, String firebaseID, String username, String userTag) {
            this.userID = userID;
            this.firebaseID = firebaseID;
            this.username = username;
            this.userTag = userTag;
        }

        public UserData(){
            this.userID = 0;
            this.firebaseID = null;
            this.username = null;
            this.userTag = null;
        }

        public int userID() {
            return userID;
        }

        public String firebaseID() {
            return firebaseID;
        }

        public String username() {
            return username;
        }

        public String userTag() {
            return userTag;
        }

        public UserData userID(int userID) {
            this.userID = userID;
            return this;
        }

        public UserData firebaseID(String firebaseID) {
            this.firebaseID = firebaseID;
            return this;
        }

        public UserData username(String username) {
            this.username = username;
            return this;
        }

        public UserData userTag(String userTag) {
            this.userTag = userTag;
            return this;
        }

        public User toUser(){
            return new User(userID, firebaseID, username, userTag());
        }

        public static UserData fromUser(User user){
            UserData ud = new UserData(
                    (int) user.getUserID(),
                    user.getFirebaseUID(),
                    user.getDisplayName(),
                    user.getUserTag()
            );

            WSInstanceManager.setUserData(ud);

            return ud;
        }
    }

    private static WSAPI wsapiInstance;
    private static UserData userData;

    public static WSAPI getInstance() {
        return wsapiInstance;
    }

    public static WSAPI createInstance(String uri){
        wsapiInstance = new WSAPI(uri);
        return wsapiInstance;
    }

    public static void setInstance(WSAPI wsapiInstance) {
        WSInstanceManager.wsapiInstance = wsapiInstance;
    }

    public static void newUserData(){
        userData = new UserData();
    }

    public static void setUserData(UserData ud){
        userData = ud;
    }

    public static UserData getUserData(){
        return userData;
    }
}
