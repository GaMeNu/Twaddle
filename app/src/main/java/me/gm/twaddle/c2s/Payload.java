package me.gm.twaddle.c2s;

import android.util.Log;

import org.json.*;

import me.gm.twaddle.obj.ToJSONObject;

/**
 * This class represents an object that has been sent to or recieved from the WebSocket server.
 */
public class Payload implements ToJSONObject {
    private short opcode;
    private JSONObject data;

    /**
     * This Enum stores all of the available ServerSide events
     */
    public enum Events {
        CREATE_USER("CREATE_USER"),
        LOGIN_USER("LOGIN_USER"),
        LOAD_USER_CHATS("LOAD_USER_CHATS"),
        CREATE_USER_CHAT("CREATE_USER_CHAT"),
        REQUEST_MESSAGE_BATCH("REQUEST_MESSAGE_BATCH"),
        SEND_CHAT_MESSAGE("SEND_CHAT_MESSAGE"),
        LOAD_SINGLE_CHAT("LOAD_SINGLE_CHAT"),
        MARK_AS_READ("MARK_AS_READ"),
        UPDATE_DETAILS("UPDATE_DETAILS");

        private final String name;

        Events(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * All used and usable opcodes.
     */
    public static class Opcodes {
        public static final short SERVER_EVENT = 1;
        public static final short NEW_MESSAGE = 2;
    }

    /**
     * Empty constructor
     */
    public Payload(){}

    /**
     * Constructor with opcodes
     * @param opcode Opcode
     */
    public Payload(short opcode) {
        this.opcode = opcode;
    }

    /**
     * Full constructor
     * @param opcode opcode
     * @param data data to send to the server
     */
    public Payload(short opcode, JSONObject data) {
        this.opcode = opcode;
        this.data = data;
    }

    /**
     * Data getter
     * @return data
     */
    public JSONObject getData(){
        return this.data;
    }

    /**
     * Data setter
     * @return data
     */
    public short getOpcode() {
        return opcode;
    }

    /**
     * Full constructor, casts int to short
     * @param opcode opcode
     * @param data data
     */
    public Payload(int opcode, JSONObject data) {
        this.opcode = (short) opcode;
        this.data = data;
    }

    /**
     * Event constructor. This constructs a payload that is prepared to be sent for an event.
     * @param eventType an event type from the available server event types
     * @param eventData event data to be sent alongside the event type
     * @return new prepared Payload
     * @throws JSONException
     */
    public static Payload event(Events eventType, JSONObject eventData) throws JSONException {
        return new Payload(1,
                new JSONObject()
                .put("event", eventType.getName())
                .put("data", eventData)
        );
    }

    /**
     * Parses a string to a JSON Object, then converts it to a Payload
     * @param s String to parse
     * @return parsed payload
     * @throws JSONException
     */
    public static Payload fromString(String s) throws JSONException {
        JSONObject pl;
        try {
            pl = new JSONObject(new JSONTokener(s));
        } catch (JSONException e) {
            Log.e("PAYLOAD", "JSON Error:", e);
            throw e;
        }
        return new Payload(pl.getInt("op"), pl.getJSONObject("data"));
    }

    /**
     * Set the opcode
     * @param opcode
     * @return
     */
    public Payload setOpcode(short opcode){
        this.opcode = opcode;
        return this;
    }

    /**
     * Set the data object
     * @param data
     * @return
     */
    public Payload setData(JSONObject data){
        this.data = data;
        return this;
    }

    /**
     * @inheritDoc
     *
     * JSON STRUCTURE:
     * <code lang="json"><pre>
     *     {
     *         "op": ...,
     *         "data": {...}
     *     }
     * </pre></code>
     */
    @Override
    public JSONObject serialize() {
        JSONObject res = new JSONObject();
        try {
            res.put("op", opcode);
            res.put("data", data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

}
