package me.gm.twaddle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

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
        tts = new TextToSpeech(getApplicationContext(), status -> {
            tts.setLanguage(Locale.ENGLISH);
            if (status != TextToSpeech.ERROR)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        });
        return START_STICKY;
    }
}
