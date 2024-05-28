package me.gm.twaddle.etc;

import android.app.Activity;
import android.app.Application;

public class TwaddleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    private Activity mCurrentActivity;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }
}
