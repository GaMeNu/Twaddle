package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView errorTitle;
    TextView errorDescription;
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;

    LinearLayout errorBoxes;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        errorTitle = findViewById(R.id.tv_errTitle);
        errorDescription = findViewById(R.id.tv_errDesc);
        errorBoxes = findViewById(R.id.layout_errBoxes);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_gotoRegister);

        btnLogin.setOnClickListener(this::onClick_login);
        btnRegister.setOnClickListener(this::onClick_register);

    }

    @Override
    protected void onResume() {
        super.onResume();
        resetError();
    }

    public String getEmail() {
        return etEmail.getText().toString();
    }

    public String getPassword() {
        return etPassword.getText().toString();
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

    private void onClick_login(View view){
        loginUser(etEmail.getText().toString(), etPassword.getText().toString());
    }

    private void onClick_register(View view){
        Intent intent = new Intent(this, RegisterStepTwoActivity.class);
        intent.putExtra("userEmail", etEmail.getText().toString())
                .putExtra("userPassword", etPassword.getText().toString());
        startActivity(intent);
    }


    public void loginUser(String email, String password){
        resetError();
        if (email.isEmpty() || password.isEmpty()){
            setError("Error:", "Email and Password fields cannot be empty.");
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(this::loginUser_success).addOnFailureListener(this::loginUser_failure);
    }

    private void loginUser_failure(Exception e) {
        setError("Error while logging in:", e.getMessage());
    }

    private void loginUser_success(AuthResult authResult) {

        setError("Logged in successfully", "Using ErrorBox because lazy again.");

    }


}