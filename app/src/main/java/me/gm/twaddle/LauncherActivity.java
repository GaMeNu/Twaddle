package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.logging.Logger;

public class LauncherActivity extends AppCompatActivity {

    String email, password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        String email, password;

        SharedPreferences sp;
        mAuth = FirebaseAuth.getInstance();

        sp = getSharedPreferences("authCreds", Context.MODE_PRIVATE);
        email = sp.getString("email", null);
        password = sp.getString("password", null);

        this.email = email;
        this.password = password;

        // Check if there's internet
        if (!NetworkUtils.hasActiveInternetConnection(LauncherActivity.this)){
            handleOfflineMode();
            return;
        }


        if (email != null && password != null){
            loginUser(email, password);

        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void handleOfflineMode() {
        if (this.email == null || this.password == null){
            startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
            finish();
            return;
        }

        startActivity(
                new Intent(LauncherActivity.this, HomeActivity.class)
                .putExtra("offlineMode", true)
        );
        finish();

    }

    public void loginUser(String email, String password){
        if (email.isEmpty() || password.isEmpty()){
            startActivityForLogin(email, password);
            return;
        }

        this.email = email;
        this.password = password;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this::loginUser_success)
                .addOnFailureListener(this::loginUser_failure);

    }

    private void loginUser_failure(Exception e) {
        if (e instanceof FirebaseNetworkException){
            handleOfflineMode();
        } else {
            startActivityForLogin(email, password);
            finish();
        }
    }

    private void loginUser_success(AuthResult authResult) {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void startActivityForLogin(String email, String password){
        startActivity(new Intent(this, LoginActivity.class)
                .putExtra("startForLogin", true)
                .putExtra("email", email)
                .putExtra("password", password));
    }
}