package me.gm.twaddle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this.getApplicationContext();

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MainActivity.this.handleOnBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    public void handleOnBackPressed(){
        new AlertDialog.Builder(ctx).setTitle("Are you sure you want to quit?").setPositiveButton("Yes", this::onQuitDialogPositive).setNegativeButton("No",this::onQuitDialogNegative);
    }

    private void onQuitDialogNegative(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }

    private void onQuitDialogPositive(DialogInterface dialogInterface, int i) {
        finishAffinity();
    }


}