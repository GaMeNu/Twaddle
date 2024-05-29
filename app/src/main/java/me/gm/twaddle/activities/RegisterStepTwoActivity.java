package me.gm.twaddle.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import me.gm.twaddle.R;

public class RegisterStepTwoActivity extends BaseAppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    private Button btnBack;
    private Button btnRegister;

    private TextView errorTitle;
    private TextView errorDescription;
    private LinearLayout errorBoxes;
    private FirebaseAuth mAuth;
    private CheckBox rememberCreds;

    private String email;
    private String password;
    private String wsUri;
    private boolean remember;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onActivityResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_two);

        etEmail = findViewById(R.id.et_emailRegister);
        etPassword = findViewById(R.id.et_passwordRegister);
        etPasswordConfirm = findViewById(R.id.et_passwordRegisterConfirm);

        btnBack = findViewById(R.id.btn_backRegister);
        btnRegister = findViewById(R.id.btn_register);

        errorTitle = findViewById(R.id.tv_errTitle_register);
        errorDescription = findViewById(R.id.tv_errDesc_register);
        errorBoxes = findViewById(R.id.layout_errBoxesRegister);

        rememberCreds = findViewById(R.id.cb_rememberCredentials_register);

        btnBack.setOnClickListener(this::onClick_back);
        btnRegister.setOnClickListener(this::onClick_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail.setText(getIntent().getStringExtra("userEmail"));
        etPassword.setText(getIntent().getStringExtra("userPassword"));
        rememberCreds.setChecked(getIntent().getBooleanExtra("userRemember", false));

        wsUri = getIntent().getStringExtra("ws_uri");


    }

    private void onClick_back(View view) {
        finish();
    }

    private void onClick_register(View view){
        resetError();

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConf = etPasswordConfirm.getText().toString();
        // Confirm passwords matching:

        if (email.isEmpty()){
            setError("Error:", "Email field cannot be empty.");
            return;
        }

        if (password.isEmpty()){
            setError("Error:", "Password field cannot be empty.");
            return;
        }

        if (!password.equals(passwordConf)){
            setError("Error:", "Password and Confirmation fields do not match.");
            return;
        }

        registerUser(email, password);
    }

    public void registerUser(String email, String password){
        resetError();

        this.email = email;
        this.password = password;

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(this::registerUser_success).addOnFailureListener(this::registerUser_failure);
    }

    private void registerUser_failure(Exception e) {
        setError("Error while creating user", e.getMessage());
        Log.e("RS2", "ERROR creating user", e);
    }

    private void registerUser_success(AuthResult authResult) {
        setError("Registered successfully", "I'm using the error box because lazy.");


        Intent usernameIntent = new Intent(RegisterStepTwoActivity.this, RegisterStepThreeActivity.class);

        usernameIntent.putExtra("email", this.email)
                .putExtra("ws_uri", wsUri);

        activityResultLauncher.launch(usernameIntent);
    }

    private void onActivityResult(ActivityResult result){
        if (result.getResultCode() == RESULT_OK){
            Intent resultData = new Intent()
            .putExtra("email", this.email)
            .putExtra("password", this.password)
            .putExtra("remember", this.rememberCreds.isChecked());

            setResult(RESULT_OK, resultData);
            finish();
        }
    }

    public void setError(CharSequence title, CharSequence description){
        errorTitle.setText(title);
        errorDescription.setText(description);
        errorBoxes.setVisibility(View.VISIBLE);
    }

    public void resetError(){
        errorTitle.setText("ErrorTitle");
        errorDescription.setText("Error description placeholder");
        errorBoxes.setVisibility(View.INVISIBLE);
    }





}