package me.gm.twaddle.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import me.gm.twaddle.c2s.RespPayload;
import me.gm.twaddle.fragments.DirectMessagesFragment;
import me.gm.twaddle.fragments.HomeFragment;
import me.gm.twaddle.R;
import me.gm.twaddle.fragments.ServersFragment;
import me.gm.twaddle.fragments.SettingsFragment;
import me.gm.twaddle.c2s.WSInstanceManager;
import me.gm.twaddle.c2s.WSAPI;

public class HomeActivity extends BaseAppCompatActivity {

    private final String TAG = "HOME";

    private RecyclerView directs_chats;

    private boolean offlineMode;

    private String displayName;
    private String tag;
    private FirebaseAuth mAuth;
    private WSAPI wsApi;
    private String wsUri;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onActivityResult
    );


    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment;
    DirectMessagesFragment directMessagesFragment;
    ServersFragment serversFragment;
    SettingsFragment settingsFragment;

    boolean receivedFirstPayload = false;

    boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        // Get URI from intent
        wsUri = getIntent().getStringExtra("ws_uri");

        // Create WSAPI
        wsApi = new WSAPI(wsUri, this);

        // Add Error handler (if can't connect)
        wsApi.getClient().addErrorHandler("oe_homeActivity1", e -> {
            Log.i(TAG, "Successfully added Handler");
            if (e instanceof IllegalArgumentException){
                new AlertDialog.Builder(HomeActivity.this, R.style.Theme_AlertDialog)
                    .setTitle("Error: No Internet/WebSocket")
                    .setMessage(
                            "The app requires an active internet connection.\n" +
                            "It also requires a WebSocket connection to the Twaddle Server.\n" +
                            "Please try again once you have both available.")
                    .setPositiveButton("Okay", (dialogInterface, i) -> finishAffinity())
                    .show();
            } else {
                Log.e(TAG, "WebSocket Error:", e);
            }
        });

        // Add open handler
        wsApi.getClient().addOpenHandler("oo_home_setConnected", this::wsOnConnect);

        // Set public instance and connect
        WSInstanceManager.setInstance(wsApi);
        wsApi.connect();

        // Instantiate fragments
        homeFragment = new HomeFragment();
        directMessagesFragment = new DirectMessagesFragment();
        serversFragment = new ServersFragment();
        settingsFragment = new SettingsFragment();

        bottomNavigationView = findViewById(R.id.home_bottomNavMenu);

        // This should always be false.
        offlineMode = getIntent().getBooleanExtra("offlineMode", false);

        if (offlineMode){
            retrieveCachedDetails();
        } else {
            updateDetailsFromServer();
        }

        // Inflate the homeFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.home_container,homeFragment).commit();

        // Set the navbar change listener
        bottomNavigationView.setOnItemSelectedListener(this::onNavbarItemSelect);

        // Override default back button behavior to show exit app dialog
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                HomeActivity.this.handleOnBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(backPressedCallback);

    }

    private void wsOnConnect(ServerHandshake serverHandshake) {
        isConnected = true;
        wsApi.getClient().removeOpenHandler("oo_home_setConnected");

        wsApi.reqs().loginUser(
                mAuth.getUid()
        ).onResponse(this::setAuthCreds).send();

        WSInstanceManager.getUserData()
                .userID(getSharedPreferences("authCreds", MODE_PRIVATE).getInt("user_id", 0))
                .firebaseID(mAuth.getUid())
                .username(displayName)
                .userTag(tag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getSupportActionBar().isShowing()) getSupportActionBar().show();
    }

    public WSAPI getWsApi() {
        return wsApi;
    }

    private boolean onNavbarItemSelect(MenuItem menuItem) {

        // TODO: animation when switching fragments?

        switch (menuItem.getItemId()){
            case R.id.navBar_home:
                replaceFragment(homeFragment);
                return true;
            case R.id.navBar_directs:
                replaceFragment(directMessagesFragment);
                return true;
            case R.id.navBar_servers:
                replaceFragment(serversFragment);
                return true;
            case R.id.navBar_settings:
                replaceFragment(settingsFragment);
                return true;
        }

        return false;
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.home_container, fragment).commit();
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
        new AlertDialog.Builder(HomeActivity.this, R.style.Theme_AlertDialog)
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

    private void setAuthCreds(RespPayload pl){
        int userID = 0;
        if (!pl.isSuccessful()){
            noAuth();
            return;
        }
        String userTag, userName, firebaseID;

        Log.i("HOME", pl.getData().toString());
        try {
            JSONObject userData = pl.getData().getJSONObject("data");

            userID = userData.getInt("user_id");
            firebaseID = userData.getString("firebase_id");
            userTag = userData.getString("user_tag");
            userName = userData.getString("user_name");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        if (userID == 0){
            Log.e("HOME", "The server has failed me! We must abort the app!");
            finishAffinity();
        }

        getSharedPreferences("authCreds", MODE_PRIVATE).edit()
                .putInt("user_id", userID)
                .putString("tag", userTag)
                .putString("username", userName)
                .apply();

        WSInstanceManager.getUserData()
                .userID(userID)
                .firebaseID(firebaseID)
                .userTag(userTag)
                .username(userName);

    }

    private void noAuth(){
        Intent intent = new Intent(HomeActivity.this, RegisterStepThreeActivity.class);
        intent.putExtra("email", "@example.com")
                .putExtra("ws_uri", wsUri);

        activityResultLauncher.launch(intent);
    }

    private void onActivityResult(ActivityResult result) {
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