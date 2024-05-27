package me.gm.twaddle;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseAppCompatActivity extends AppCompatActivity {
    private TwaddleApplication application;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        application = (TwaddleApplication)getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (application == null) application = (TwaddleApplication)getApplicationContext();
        application.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = application.getCurrentActivity();
        if (this.equals(currActivity))
            application.setCurrentActivity(null);
    }
}
