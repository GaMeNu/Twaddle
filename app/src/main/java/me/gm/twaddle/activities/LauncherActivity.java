package me.gm.twaddle.activities;

import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import me.gm.twaddle.R;
import me.gm.twaddle.c2s.WSInstanceManager;
import me.gm.twaddle.c2s.WSClient;

/**
 * This class defines the Launcher Activity that is the first that starts when the app is launched.
 * <br><br>
 * <h2>Class call order:</h2>
 * <pre>
 *      1. Instantiate class vars: FBAuth, User Credentials.
 *      2. Ask the user for the WebSocket URI. If confirmed, proceed.
 *     If denied, exit the app.
 *      3. Create a new WebSocket Client, and give it an onOpen handler
 *     (proceed), and an onException handler (Step -1).
 *      4. When connected to the WebSocket, close the WebSocket and
 *     remove the handlers. Proceed.
 *      5. Instantiate a new UserData instance. This will be filled in
 *     the HomeActivity.
 *      6. Check whether we have login credentials saved. If we do,
 *     proceed. If not, start the Login Activity with the ws_uri as
 *     an extra.
 *      7. If email or password are empty, go to step -2. Else,
 *     proceed.
 *      8. Attempt to log in via FBAuth. If successful, launch the Home
 *     Activity with the ws_uri, Else, go to step -3.
 * </pre>
 * <br>
 * <pre>
 *      -1. Throw an error dialog to the user. On
 *     confirmation/dismissal, exit the app.
 *      -2. Credentials related error. Delegate to LoginActivity with
 *     the available credentials and the startForLogin flag.
 *      -3. FBAuth error. Check error type. If Network Error, go to
 *     step -1. Else, go to step -2.
 * </pre>
 */
public class LauncherActivity extends BaseAppCompatActivity {

    String email, password;
    FirebaseAuth mAuth;
    EditText etWsUri;
    String wsUri;
    private WSClient wsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // createNotificationChannel()

        // Too lazy to design a night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        String email, password;

        // Set up FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sp;
        sp = getSharedPreferences("authCreds", Context.MODE_PRIVATE);
        email = sp.getString("email", null);
        password = sp.getString("password", null);

        this.email = email;
        this.password = password;

        etWsUri = new EditText(this, null, R.style.EditText_Input, R.style.EditText_Input);

        new AlertDialog.Builder(LauncherActivity.this, R.style.Theme_AlertDialog)
                .setTitle("Please enter the server's WebSocket URI")
                .setView(etWsUri)
                .setPositiveButton("Confirm", this::onWSDialogPositive)
                .setNegativeButton("Cancel",this::onWSDialogNegative)
                .setCancelable(false)
                .show();

    }

    private void onWSDialogPositive(DialogInterface dialogInterface, int i) {
        wsUri = etWsUri.getText().toString().trim();
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

        WSInstanceManager.newUserData();

        // Check if we have saved credentials

        if (email != null && password != null){
            loginUser(email, password);
        } else {
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("ws_uri", wsUri));
        }
    }

    private void onWSDialogNegative(DialogInterface dialogInterface, int i) {
        handleOfflineMode();
    }

    private void handleOfflineMode() {
        new AlertDialog.Builder(LauncherActivity.this, R.style.Theme_AlertDialog)
                .setTitle("Error: No Internet/WebSocket")
                .setMessage(
                        "The app requires an active internet connection.\n" +
                        "It also requires a WebSocket connection to the Twaddle Server.\n" +
                        "Please try again once you have both available.")
                .setPositiveButton("Okay", (dialogInterface, i) -> finishAffinity())
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
            Log.e("LAUNCHER", "Network Error:",
                    e);
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(com.google.android.gms.base.R.string.common_google_play_services_notification_channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationChannel.DEFAULT_CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}