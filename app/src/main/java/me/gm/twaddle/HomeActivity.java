package me.gm.twaddle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

import java.util.logging.Logger;

public class HomeActivity extends AppCompatActivity {

    private boolean offlineMode;

    private String displayName;
    private String tag;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        offlineMode = getIntent().getBooleanExtra("offlineMode", false);

        if (offlineMode){
            retrieveCachedDetails();
        } else {
            updateDetailsFromServer();
        }

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                HomeActivity.this.handleOnBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    private void updateDetailsFromServer() {
        // TODO: implement actual detail retrieval after DB implemented
        retrieveCachedDetails();
    }

    private void retrieveCachedDetails() {
        SharedPreferences sp = getSharedPreferences("authCreds", Context.MODE_PRIVATE);

        this.displayName = sp.getString("username", null);
        this.tag = sp.getString("tag", null);

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

    private void logoutUser(){
        SharedPreferences.Editor editor = getSharedPreferences("authCreds", Context.MODE_PRIVATE).edit();
        editor.remove("email").remove("password");
        editor.apply();
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }
}