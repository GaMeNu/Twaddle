package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.channels.Channel;

import me.gm.twaddle.c2s.WSClient;

public class LauncherActivity extends AppCompatActivity {

    String email, password;
    FirebaseAuth mAuth;
    EditText etWsUri;
    String wsUri;
    boolean webSocketAvailable;
    private WSClient wsClient;

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

//        // Check if there's internet
//        if (!NetworkUtils.hasActiveInternetConnection(LauncherActivity.this)){
//            handleOfflineMode();
//            return;
//        }

        etWsUri = new EditText(LauncherActivity.this);

        new AlertDialog.Builder(LauncherActivity.this)
                .setTitle("Please enter the server's WebSocket URI")
                .setView(etWsUri)
                .setPositiveButton("Confirm", this::onWSDialogPositive)
                .setNegativeButton("Cancel",this::onWSDialogNegative)
                .setCancelable(false)
                .show();

    }

    private void onWSDialogPositive(DialogInterface dialogInterface, int i) {
        wsUri = etWsUri.getText().toString();
        wsClient = new WSClient(URI.create(wsUri));

        wsClient.addOpenHandler("oo_launcher", this::onWSConnect);
        wsClient.addErrorHandler("oe_launcher", this::onWSException);
        wsClient.connect();

    }

    private void onWSException(Exception e) {
        Log.e("LAUNCHER", "Exception when attempting to connect to WS", e);
        handleOfflineMode();
    }

    private void onWSConnect(ServerHandshake serverHandshake) {
        wsClient.close();

        // Check if we have saved credentials

        if (email != null && password != null){
            loginUser(email, password);
        } else {
            startActivity(new Intent(this, LoginActivity.class).putExtra("ws_uri", wsUri));
        }
    }

    private void onWSDialogNegative(DialogInterface dialogInterface, int i) {
        finishAffinity();
    }

    private void handleOfflineMode() {
        new AlertDialog.Builder(LauncherActivity.this)
                .setTitle("Error: No Internet/WebSocket")
                .setMessage(
                        "The app requires an active internet connection.\n" +
                        "It also requires a WebSocket connection to the Twaddle Server.\n" +
                        "Please try again once you have both available.")
                .setPositiveButton("Okay", (dialogInterface, i) -> {
                    finishAffinity();
                })
                .show();

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

        if (wsClient.isOpen()){
            wsClient.close();
        }

        startActivity(new Intent(this, HomeActivity.class).putExtra("ws_uri", wsUri));
        finish();
    }

    private void startActivityForLogin(String email, String password){
        startActivity(new Intent(this, LoginActivity.class)
                .putExtra("startForLogin", true)
                .putExtra("email", email)
                .putExtra("password", password)
                .putExtra("ws_uri", wsUri));
    }
}