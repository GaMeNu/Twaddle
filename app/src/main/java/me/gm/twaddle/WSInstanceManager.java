package me.gm.twaddle;

import me.gm.twaddle.c2s.WSAPI;

public class WSInstanceManager {
    private static WSAPI wsapiInstance;

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
}
