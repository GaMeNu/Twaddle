package me.gm.twaddle.c2s;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import me.gm.twaddle.SendableMessage;

/**
 * This is the primary class for interacting with the WebServer.
 */
public class WSAPI {

    private static final String TAG = "WebSocket API";

    WSClient wsClient;

    Queue<Payload> responseQueue;

    /**
     * Instantiate a new API instance.
     * This should not be used! Use {@link me.gm.twaddle.WSInstanceManager} instead, so as to not overload the WebServer with connections.
     * @param uri WebSocket URI
     */
    public WSAPI(String uri){
        this.wsClient = new WSClient(URI.create(uri));
        this.responseQueue = new LinkedBlockingQueue<>();
        wsClient.addMessageHandler("om_wsapiNewest", s -> {
            try {
                responseQueue.add(Payload.fromString(s));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * Create a new Requests object
     * @return ...the new Requests object.
     */
    public Requests reqs(){
        return new Requests();

    }

    /**
     * Get the internal WSClient.
     * Useful for adding handlers.
     * @return The internal WSClient instance
     */
    public WSClient getClient(){
        return wsClient;
    }

    /**
     * Connect the WSClient to the WebServer.
     */
    public void connect(){
        wsClient.connect();
    }

    /**
     * Send a single Payload object to the webserver
     * @param payload The payload to send.
     */
    public void sendPayload(Payload payload){
        wsClient.sendPayload(payload);
    }

    private static int reqsCounter = 0;

    /**
     * This class has functions that generate Payloads, and send them to the Gateway.
     * Each Payload is basically a fancy JSON Object
     * that gets sent to the Gateway and tells it to do something.
     *
     */
    public class Requests {

        private Payload pendingPayload;


        /**
         * Specify what to do after the request gets a reponse
         * @param fn Function to run
         * @return self
         */
        public Requests onResponse(Consumer<RespPayload> fn){

            String id = "om_reqsAfter_" + reqsCounter;
            reqsCounter++;

            WSAPI.this.getClient().addMessageHandler(id, s -> {
                WSAPI.this.getClient().removeMessageHandler(id);
                RespPayload pl;


                try {
                    pl = RespPayload.fromString(s);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                fn.accept(pl);
            });

            return this;
        }

        public void send(){
            Log.i(TAG, "Requstin' from da server. Request Payload:\n" + pendingPayload.serialize().toString());
            WSAPI.this.sendPayload(pendingPayload);
            pendingPayload = null;
        }



        /**
         * Generates a registration payload
         * that tells the GW to register a new user to the DB
         * @param firebaseUID uid to reg
         * @param userTag usertag to reg
         * @param username username to reg
         */
        public Requests registerUser(String firebaseUID, String userTag, String username){

            JSONObject data = new JSONObject();
            try {
                data.put("firebase_uid", firebaseUID)
                        .put("usertag", userTag)
                        .put("username", username);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            try {
                pendingPayload = Payload.event(Payload.Events.CREATE_USER, data);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        /**
         * Login Payload. Request the WebServer for user details.
         * @param firebaseID
         */
        public Requests loginUser(String firebaseID){
            try {
                JSONObject data = new JSONObject()
                        .put("firebase_id", firebaseID);

                pendingPayload = Payload.event(Payload.Events.LOGIN_USER, data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        /**
         * Ask the webserver to create a new chat
         * @param userID
         * @param recv_tag
         */
        public Requests createChat(int userID, String recv_tag){
            try {
                JSONObject data = new JSONObject()
                        .put("orig_user_id", userID)
                        .put("recv_user_tag", recv_tag);

                pendingPayload = Payload.event(Payload.Events.CREATE_USER_CHAT, data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public Requests loadChats(int userID){
            try {
                JSONObject data = new JSONObject()
                        .put("user_id", userID);

                pendingPayload = Payload.event(Payload.Events.LOAD_USER_CHATS, data);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public Requests sendTextMsg(SendableMessage msg){
            JSONObject data = msg.serialize();

            try {
                pendingPayload = Payload.event(Payload.Events.SEND_MESSAGE, data);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return this;
        }
    }
}
