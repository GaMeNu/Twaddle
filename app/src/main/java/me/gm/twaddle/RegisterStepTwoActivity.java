package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterStepTwoActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    private Button btnBack;
    private Button btnRegister;

    private TextView errorTitle;
    private TextView errorDescription;
    private LinearLayout errorBoxes;
    private FirebaseAuth mAuth;

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

        btnBack.setOnClickListener(this::onClick_back);
        btnRegister.setOnClickListener(this::onClick_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail.setText(getIntent().getStringExtra("userEmail"));
        etPassword.setText(getIntent().getStringExtra("userPassword"));


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

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(this::registerUser_success).addOnFailureListener(this::registerUser_failure);
    }

    private void registerUser_failure(Exception e) {
        setError("Error while creating user", e.getMessage());
    }

    private void registerUser_success(AuthResult authResult) {
        setError("Registered successfully", "I'm using the error box because lazy.");
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