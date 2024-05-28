package me.gm.twaddle.c2s;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import me.gm.twaddle.activities.LauncherActivity;
import me.gm.twaddle.R;
import me.gm.twaddle.obj.SendableMessage;
import me.gm.twaddle.obj.User;

/**
 * This is the primary class for interacting with the WebServer.
 */
public class WSAPI {

    private static final String TAG = "WebSocket API";

    WSClient wsClient;
    Context ctx;

    URI uri;

    Queue<Payload> responseQueue;

    /**
     * Instantiate a new API instance.
     * This should not be used! Use {@link WSInstanceManager} instead, so as to not overload the WebServer with connections.
     * @param uri WebSocket URI
     */
    public WSAPI(String uri, Context ctx){
        this.uri = URI.create(uri);
        this.wsClient = new WSClient(this.uri);
        this.responseQueue = new LinkedBlockingQueue<>();
        wsClient.addMessageHandler("om_wsapiNewest", s -> {
            try {
                responseQueue.add(Payload.fromString(s));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        this.ctx = ctx;

        wsClient.addCloseHandler("oc_restart", closeEvent -> restartApp());
        wsClient.addErrorHandler("oe_restart", e -> {
            if (e instanceof WebsocketNotConnectedException) restartApp();
        });

    }

    private void restartApp() {
        ((Activity) ctx).runOnUiThread(() -> {
            new AlertDialog.Builder(ctx, R.style.Theme_AlertDialog)
                    .setTitle("Connection to the WebServer disrupted")
                    .setMessage("The app will proceed to restart.")
                    .setPositiveButton("Okay", (dialogInterface, i) -> {
                        Intent intent = new Intent(((Activity)ctx).getApplicationContext(), LauncherActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity) ctx).startActivity(intent);
                    })
                    .show();
        });
    }


    /**
     * Wrapper for {@link WSClient#addOpenHandler(String, Consumer)}
     * @see WSClient#addOpenHandler(String, Consumer)
     */
    public WSAPI addOpenHandler(String id, Consumer<ServerHandshake> fn){
        getClient().addOpenHandler(id, fn);
        return this;
    }

    /**
     * Wrapper for {@link WSClient#addCloseHandler(String, Consumer)}
     * @see WSClient#addCloseHandler(String, Consumer)
     */
    public WSAPI addCloseHandler(String id, Consumer<WSClient.CloseEvent> fn){
        getClient().addCloseHandler(id, fn);
        return this;
    }

    /**
     * Wrapper for {@link WSClient#addMessageHandler(String, Consumer)}
     * @see WSClient#addMessageHandler(String, Consumer)
     */
    public WSAPI addMessageHandler(String id, Consumer<String> fn){
        getClient().addMessageHandler(id, fn);
        return this;
    }

    /**
     * Wrapper for {@link WSClient#addErrorHandler(String, Consumer)}
     * @see WSClient#addErrorHandler(String, Consumer)
     */
    public WSAPI addErrorHandler(String id, Consumer<Exception> fn){
        getClient().addErrorHandler(id, fn);
        return this;
    }

    /**
     * Wrapper for {@link WSClient#removeOpenHandler(String)}
     * @see WSClient#removeOpenHandler(String)
     */
    public WSAPI removeOpenHandler(String id){
        getClient().removeOpenHandler(id);
        return this;
    }

    /**
     * Wrapper for {@link WSClient#removeCloseHandler(String)}
     * @see WSClient#removeCloseHandler(String)
     */
    public WSAPI removeCloseHandler(String id){
        getClient().removeCloseHandler(id);
        return this;
    }

    /**
     * Wrapper for {@link WSClient#removeMessageHandler(String)}
     * @see WSClient#removeMessageHandler(String)
     */
    public WSAPI removeMessageHandler(String id){
        getClient().removeMessageHandler(id);
        return this;
    }

    /**
     * Wrapper for {@link WSClient#removeErrorHandler(String)}
     * @see WSClient#removeErrorHandler(String)
     */
    public WSAPI removeErrorHandler(String id){
        getClient().removeErrorHandler(id);
        return this;
    }

    /**
     * Adds a special type of a message handler.
     * This handler will automatically convert the message to a {@link Payload}
     * and run it
     *
     * @see Payload
     * @see WSAPI#addOpenHandler(String, Consumer)
     */
    public WSAPI addPayloadHandler(String id, Consumer<Payload> fn){
        getClient().addMessageHandler(id, s -> {
            Payload pl;
            try {
                pl = Payload.fromString(s);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            fn.accept(pl);
        });
        return this;
    }

    public Context getContext() {
        return ctx;
    }

    public WSAPI setContext(Context ctx) {
        this.ctx = ctx;
        return this;
    }

    /**
     * Create a new Requests object.<br>
     * This object acts like a builder.<br>
     * Specify the request, the onResponse consumer, and use Requests.send() to send the request.
     *
     * @return ...the new Requests object. What else?
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
        try {
            wsClient.sendPayload(payload);
        } catch (WebsocketNotConnectedException e){
            restartApp();
        }
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

        /**
         * Load all of the user's chats.
         * @param userID user ID to load chats of. This should probably be updated to a newer version that doesn't take in a user ID.
         *               As users can fake their own IDs, and visit others' messages.
         * @return this
         */
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

        /**
         * Load all messages in a single chat. Should probably be updated to load in batches, to optimize loading times.
         * @param chatID chat ID to load.
         * @return this
         */
        public Requests loadSingleChat(long chatID){
            try {
                JSONObject data = new JSONObject()
                        .put("chat_id", chatID);
                pendingPayload = Payload.event(Payload.Events.LOAD_SINGLE_CHAT, data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        /**
         * Send a chat message in a chat.
         * @param msg Message to send.
         * @return this
         */
        public Requests sendChatMsg(SendableMessage msg){
            JSONObject data = msg.serialize();


            try {
                pendingPayload = Payload.event(Payload.Events.SEND_CHAT_MESSAGE, data);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        /**
         * Mark a single chat as read
         * @param chatID chat to mark as read
         * @return this
         */
        public Requests markAsRead(long chatID){
            try {
                JSONObject data = new JSONObject().put("chat_id", chatID);
                pendingPayload = Payload.event(Payload.Events.MARK_AS_READ, data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        /**
         * Update a user's details.
         * @param user container for new details
         * @return this
         */
        public Requests updateDetails(User user){
            JSONObject data = user.serialize();

            try {
                pendingPayload = Payload.event(Payload.Events.UPDATE_DETAILS, data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return this;
        }
    }
}
