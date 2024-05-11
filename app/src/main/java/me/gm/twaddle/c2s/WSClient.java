package me.gm.twaddle.c2s;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WSClient extends WebSocketClient {

    /*
    1 - Confirmed last request
     */
    String TAG = "WebSocket";

    private final Map<String, Consumer<ServerHandshake>> onOpenHandlers = new HashMap<>();
    private final Map<String, Consumer<String>> onMessageHandlers = new HashMap<>();
    private final Map<String, Consumer<Exception>> onErrorHandlers = new HashMap<>();
    private final Map<String, Consumer<CloseEvent>> onCloseHandlers = new HashMap<>();

    public WSClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i(TAG, "Connected to server");
        Log.i(TAG, String.format("[%d] %s", handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage()));
        List<Consumer<ServerHandshake>> iterLs = new ArrayList<>(onOpenHandlers.values());
        for (Consumer<ServerHandshake> handler : iterLs) {
            handler.accept(handshakedata);
        }
    }

    @Override
    public void onMessage(String message) {
        Log.i(TAG, "Received data from server! Look forward for the next log entry...");
        Log.i(TAG, message);
        List<Consumer<String>> iterLs = new ArrayList<>(onMessageHandlers.values());
        for (Consumer<String> handler : iterLs) {
            handler.accept(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i(TAG, "Connection closed");
        CloseEvent ce = new CloseEvent(code, reason, remote);
        List<Consumer<CloseEvent>> iterLs = new ArrayList<>(onCloseHandlers.values());
        for (Consumer<CloseEvent> handler : iterLs) {
            handler.accept(ce);
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, String.valueOf(ex));
        List<Consumer<Exception>> iterLs = new ArrayList<>(onErrorHandlers.values());
        for (Consumer<Exception> handler : iterLs) {
            handler.accept(ex);
        }
    }

    public void addOpenHandler(String name, Consumer<ServerHandshake> fn) {
        onOpenHandlers.put(name ,fn);
    }

    public void addMessageHandler(String name, Consumer<String> fn) {
        onMessageHandlers.put(name, fn);
    }

    public void addCloseHandler(String name, Consumer<CloseEvent> fn) {
        onCloseHandlers.put(name, fn);
    }

    public void addErrorHandler(String name, Consumer<Exception> fn) {
        onErrorHandlers.put(name, fn);
    }

    public void removeOpenHandler(String name){
        onOpenHandlers.remove(name);
    }

    public void removeMessageHandler(String name){
        onMessageHandlers.remove(name);
    }

    public void removeCloseHandler(String name){
        onCloseHandlers.remove(name);
    }

    public void removeErrorHandler(String name){
        onErrorHandlers.remove(name);
    }

    public void sendPayload(Payload pl) {
        this.send(pl.serialize().toString().getBytes(StandardCharsets.UTF_8));
    }

    public static class CloseEvent {
        int code;
        String reason;
        boolean remote;

        public CloseEvent(int code, String reason, boolean remote) {
            this.code = code;
            this.reason = reason;
            this.remote = remote;
        }
    }
}
