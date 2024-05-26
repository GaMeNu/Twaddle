package me.gm.twaddle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Locale;

public class TTSService extends Service {

    TextToSpeech tts;
    String text;

    public TTSService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = intent.getStringExtra("tts");
        Log.i("TTS", text);
        tts = new TextToSpeech(getApplicationContext(), status -> {
            Log.i("TTS", "Status: " + status);
            if (status == TextToSpeech.SUCCESS){
                tts.setLanguage(Locale.ENGLISH);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                Log.i("TTS", text);
            }
        });
        return START_STICKY;
    }
}
