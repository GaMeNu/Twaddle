package me.gm.twaddle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private boolean offlineMode;

    private String displayName;
    private String tag;
    private FirebaseAuth mAuth;

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment;
    DirectMessagesFragment directMessagesFragment;
    ServersFragment serversFragment;
    SettingsFragment settingsFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();


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
                /* TODO: implement Settings activity here and remove the fragment)
                 * Settings feel more settings-y to me as a seperate activity.
                 */
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

    private void logoutUser(){
        SharedPreferences.Editor editor = getSharedPreferences("authCreds", Context.MODE_PRIVATE).edit();
        editor.remove("email").remove("password");
        editor.apply();
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }
}