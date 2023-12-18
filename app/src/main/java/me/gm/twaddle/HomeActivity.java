package me.gm.twaddle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ctx = this.getApplicationContext();

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                HomeActivity.this.handleOnBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    public void handleOnBackPressed(){
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Are you sure you want to quit?")
                .setPositiveButton("Yes", this::onQuitDialogPositive)
                .setNegativeButton("No",this::onQuitDialogNegative)
                .show();

    }

    private void onQuitDialogNegative(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }

    private void onQuitDialogPositive(DialogInterface dialogInterface, int i) {
        finishAffinity();
    }
}