package me.gm.twaddle.obj;

import android.view.View;

public class SettingsItem {
    String setting;
    int drawableID;
    View.OnClickListener onClickListener;

    public SettingsItem(String setting, int drawableID, View.OnClickListener onClickListener) {
        this.setting = setting;
        this.drawableID = drawableID;
        this.onClickListener = onClickListener;
    }

    public String name() {
        return setting;
    }

    public SettingsItem name(String setting) {
        this.setting = setting;
        return this;
    }

    public int drawableID() {
        return drawableID;
    }

    public SettingsItem drawableID(int drawable) {
        this.drawableID = drawable;
        return this;
    }

    public View.OnClickListener onClickListener() {
        return onClickListener;
    }

    public SettingsItem onClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }
}
