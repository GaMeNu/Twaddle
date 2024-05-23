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

    /**
     * Add a new onOpen handler that will run when a connection is opened.
     * Can be removed with {@code WSClient.removeOpenHandler}
     * @param id Handler ID
     * @param fn Handler function
     */
    public void addOpenHandler(String id, Consumer<ServerHandshake> fn) {
        onOpenHandlers.put(id ,fn);
    }

    /**
     * Add a new onMessage handler that will run when a message is received.
     * Can be removed with {@code WSClient.removeMessageHandler}
     * @param id Handler ID
     * @param fn Handler function
     */
    public void addMessageHandler(String id, Consumer<String> fn) {
        onMessageHandlers.put(id, fn);
    }

    /**
     * Add a new onClose handler that will run when a connection is closed.
     * Can be removed with {@code WSClient.removeCloseHandler}
     * @param id Handler ID
     * @param fn Handler function
     */
    public void addCloseHandler(String id, Consumer<CloseEvent> fn) {
        onCloseHandlers.put(id, fn);
    }

    /**
     * Add a new onError handler that will run when an error occurs.
     * Can be removed with {@code WSClient.removeErrorHandler}
     * @param id Handler ID
     * @param fn Handler function
     */
    public void addErrorHandler(String id, Consumer<Exception> fn) {
        onErrorHandlers.put(id, fn);
    }

    /**
     * Remove an existing onOpen handler.
     * Handlers can be removed with {@code WSClient.addOpenHandler}
     * @param id Handler ID
     */
    public void removeOpenHandler(String id){
        onOpenHandlers.remove(id);
    }

    /**
     * Remove an existing onMessage handler.
     * Handlers can be removed with {@code WSClient.addMessageHandler}
     * @param id Handler ID
     */
    public void removeMessageHandler(String id){
        onMessageHandlers.remove(id);
    }

    /**
     * Remove an existing onClose handler.
     * Handlers can be removed with {@code WSClient.addCloseHandler}
     * @param id Handler ID
     */
    public void removeCloseHandler(String id){
        onCloseHandlers.remove(id);
    }

    /**
     * Remove an existing onError handler.
     * Handlers can be removed with {@code WSClient.addErrorHandler}
     * @param id Handler ID
     */
    public void removeErrorHandler(String id){
        onErrorHandlers.remove(id);
    }

    /**
     * Send a {@link Payload} message to the WebSocket Server
     * @param pl Payload to send
     */
    public void sendPayload(Payload pl) {
        this.send(pl.serialize().toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * This embodies a WebSocket close event
     */
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
