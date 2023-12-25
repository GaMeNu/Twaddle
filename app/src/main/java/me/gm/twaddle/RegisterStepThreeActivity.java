package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterStepThreeActivity extends AppCompatActivity {

    private TextView errTitle;
    private TextView errDesc;

    private Button btnBack;
    private Button btnConfirm;

    private EditText etDisplayName;
    private EditText etTag;

    private String defaultUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);

        errTitle = findViewById(R.id.tv_errTitle_registerNames);
        errDesc = findViewById(R.id.tv_errDesc_registerNames);

        btnBack = findViewById(R.id.btn_register_namesBack);
        btnConfirm = findViewById(R.id.btn_register_namesConfirm);

        etDisplayName = findViewById(R.id.et_register_displayName);
        etTag = findViewById(R.id.et_register_tag);

        btnConfirm.setOnClickListener(this::onClick_confirm);
        btnBack.setOnClickListener(this::onClick_back);

        Intent credentials = getIntent();
        defaultUsername = credentials.getStringExtra("email").split("@")[0];
        etDisplayName.setText(defaultUsername);
        etTag.setText(defaultUsername);

        etDisplayName.setText(credentials.getStringExtra("email"));
    }

    private void onClick_confirm(View view) {
        if (view.getId() != btnConfirm.getId()){
            saveUsernames(etDisplayName.getText().toString(), etTag.getText().toString());
            setResult(RESULT_OK);
            finish();
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


}