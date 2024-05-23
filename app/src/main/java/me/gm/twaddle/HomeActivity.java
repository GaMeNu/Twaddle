package me.gm.twaddle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import me.gm.twaddle.c2s.Payload;
import me.gm.twaddle.c2s.WSAPI;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HOME";

    private RecyclerView directs_chats;

    private boolean offlineMode;

    private String displayName;
    private String tag;
    private FirebaseAuth mAuth;
    private WSAPI wsApi;

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

        String wsUri = getIntent().getStringExtra("ws_uri");


        Log.i(TAG, wsUri);
        wsApi = new WSAPI(wsUri);
        wsApi.getClient().addErrorHandler("oe_homeActivity1", e -> {
            Log.i(TAG, "Successfully added Handler");
            if (e instanceof IllegalArgumentException){
                new AlertDialog.Builder(HomeActivity.this)
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

        wsApi.getClient().addOpenHandler("oo_home_setConnected", serverHandshake -> {
            isConnected = true;
            wsApi.getClient().removeOpenHandler("oo_home_setConnected");

            if (getIntent().getBooleanExtra("register_new", false)){


                SharedPreferences sp = getSharedPreferences("authCreds", MODE_PRIVATE);
                Payload res = null;
                wsApi.reqs().registerUser(
                                getIntent().getStringExtra("uid"),
                                sp.getString("tag", ""),
                                sp.getString("username", "")
                ).onResponse(this::setAuthCreds).send();
            }

            wsApi.reqs().loginUser(
                    mAuth.getUid()
            ).onResponse(pl -> {
                if (getSharedPreferences("authCreds", MODE_PRIVATE).getInt("user_id", 0) == 0){
                    setAuthCreds(pl);
                }
            }).send();


            WSInstanceManager.getUserData()
                    .userID(getSharedPreferences("authCreds", MODE_PRIVATE).getInt("user_id", 0))
                    .firebaseID(mAuth.getUid())
                    .username(displayName)
                    .userTag(tag);
        });

        WSInstanceManager.setInstance(wsApi);

        wsApi.connect();

        homeFragment = new HomeFragment();
        directMessagesFragment = new DirectMessagesFragment();
        serversFragment = new ServersFragment();
        settingsFragment = new SettingsFragment();

        bottomNavigationView = findViewById(R.id.home_bottomNavMenu);

        offlineMode = getIntent().getBooleanExtra("offlineMode", false);

        if (offlineMode){
            retrieveCachedDetails();
        } else {
            updateDetailsFromServer();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.home_container,homeFragment).commit();
        
        bottomNavigationView.setOnItemSelectedListener(this::onNavbarItemSelect);

        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                HomeActivity.this.handleOnBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(backPressedCallback);
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

    private void setAuthCreds(Payload pl){
        int userID = 0;
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

    private void logoutUser(){
        SharedPreferences.Editor editor = getSharedPreferences("authCreds", Context.MODE_PRIVATE).edit();
        editor.remove("email").remove("password");
        editor.apply();
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }
}