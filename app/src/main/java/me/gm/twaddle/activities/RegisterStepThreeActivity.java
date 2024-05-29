package me.gm.twaddle.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import me.gm.twaddle.R;
import me.gm.twaddle.c2s.Payload;
import me.gm.twaddle.c2s.RespPayload;
import me.gm.twaddle.c2s.WSAPI;
import me.gm.twaddle.c2s.WSInstanceManager;

public class RegisterStepThreeActivity extends BaseAppCompatActivity {

    private TextView errTitle;
    private TextView errDesc;

    private Button btnBack;
    private Button btnConfirm;

    private EditText etDisplayName;
    private EditText etTag;

    private String defaultUsername;
    private String wsUri;
    private WSAPI wsApi;

    private String dispName;
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);

        errTitle = findViewById(R.id.tv_errTitle_registerNames);
        errDesc = findViewById(R.id.tv_errDesc_registerNames);

        wsApi = new WSAPI(getIntent().getStringExtra("ws_uri"), this);

        wsApi.connect();

        btnConfirm = findViewById(R.id.btn_register_namesConfirm);

        etDisplayName = findViewById(R.id.et_register_displayName);
        etTag = findViewById(R.id.et_register_tag);

        btnConfirm.setOnClickListener(this::onClick_confirm);

        Intent credentials = getIntent();
        //defaultUsername = (credentials.getStringExtra("email").split("@")[0]);
        String email = (credentials.getStringExtra("email"));
        String[] s = email.split("@");
        defaultUsername = s[0];
        etDisplayName.setText(defaultUsername);
        etTag.setText(defaultUsername);

        // etDisplayName.setText(credentials.getStringExtra("email"));
    }

    private void onClick_confirm(View view) {
        if (view.getId() == btnConfirm.getId()){
            dispName = etDisplayName.getText().toString();
            tag = etTag.getText().toString();
            saveUsernames(dispName, tag);
            registerWS();

        }
    }

    private void onClick_back(View view) {
        if (view.getId() != btnBack.getId()){
            saveUsernames(defaultUsername, defaultUsername);
            setResult(RESULT_OK);
            finish();
        }
    }

    private void saveUsernames(String username, String tag){
        SharedPreferences sp = getSharedPreferences("authCreds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("tag", tag);
        editor.apply();
    }

    private void registerWS(){
        SharedPreferences sp = getSharedPreferences("authCreds", MODE_PRIVATE);
        Payload res = null;
        wsApi.reqs().registerUser(
                FirebaseAuth.getInstance().getUid(), tag, dispName
        ).onResponse(this::setAuthCreds).send();
    }

    private void setAuthCreds(RespPayload pl){
        int userID = 0;

        if (!pl.isSuccessful()){
            runOnUiThread(() -> {
                Toast.makeText(this, "ServerSide error.", Toast.LENGTH_SHORT).show();
            });
        }

        String userTag, userName, firebaseID;

        Log.i("LOGIN3", pl.getData().toString());

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
            Log.e("LOGIN3", "The server has failed me! We must abort the app!");
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
        runOnUiThread(() -> {
            setResult(RESULT_OK);
            finish();
        });

    }




}