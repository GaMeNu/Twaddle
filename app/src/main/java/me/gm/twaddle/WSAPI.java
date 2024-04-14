package me.gm.twaddle;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import me.gm.twaddle.c2s.Payload;
import me.gm.twaddle.c2s.WSClient;

public class WSAPI {

    private static final String TAG = "WebSocket API";

    WSClient wsClient;

    Payload newestPayload;

    Requests reqs;

    CountDownLatch notificationLatch;

    public WSAPI(String uri){
        this.wsClient = new WSClient(URI.create(uri));
        this.wsClient.addMessageHandler("om_WSAPI", this::onMessage);

        reqs = new Requests();
    }

    public WSClient getClient(){
        return wsClient;
    }

    public void connect(){
        wsClient.connect();
    }

    private void onMessage(String s) {
        try {
            Payload payloadRes = Payload.fromString(s);
            newestPayload = payloadRes;
            if (notificationLatch != null){
                notificationLatch.countDown();
            }
        } catch (JSONException ignored) {}

    }



    public void sendRequest(Payload requestPayload){
        wsClient.sendPayload(requestPayload);
    }

    private Payload awaitConfirmation(){
        notificationLatch = new CountDownLatch(1);
        try {
            notificationLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Interrupted while waiting for message");
        }
        Payload ret = newestPayload;
        newestPayload = null;
        return newestPayload;
    }


    public class Requests {

        //TODO: check

        public Payload registerUser(String firebaseUID, String userTag, String username){

            JSONObject res = new JSONObject();
            JSONObject data = new JSONObject();
            try {
                data.put("firebase_uid", firebaseUID)
                        .put("usertag", userTag)
                        .put("username", username);
                res.put("event", "CREATE_USER")
                        .put("data", data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            WSAPI.this.sendRequest(new Payload(1, res));
            return awaitConfirmation();
        }
    }
}
