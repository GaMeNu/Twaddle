package me.gm.twaddle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseAppCompatActivity {

    TextView errorTitle;
    TextView errorDescription;
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;

    CheckBox rememberCreds;

    LinearLayout errorBoxes;

    FirebaseAuth mAuth;

    String email;
    String password;

    boolean remember;

    boolean register_new;

    String wsUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        errorTitle = findViewById(R.id.tv_errTitle_login);
        errorDescription = findViewById(R.id.tv_errDesc_login);
        errorBoxes = findViewById(R.id.layout_errBoxes);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_gotoRegister);

        rememberCreds = findViewById(R.id.cb_rememberCredentials_register);

        btnLogin.setOnClickListener(this::onClick_login);
        btnRegister.setOnClickListener(this::onClick_register);

        wsUri = getIntent().getExtras().getString("ws_uri");

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("startForLogin", false)) {
            try {
                loginUser(getIntent().getExtras().getString("email"), getIntent().getExtras().getString("password"));
            } catch (NullPointerException e){
                setError("Missing auth credentials", "Activity started for login but no auth credentials were given");
            } catch (Exception e){
                setError("Error:", "Failed to login using external credentials");
            }
        }

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
        remember = rememberCreds.isChecked();
        loginUser(etEmail.getText().toString(), etPassword.getText().toString());
    }

    private void onClick_register(View view){
        Intent intent = new Intent(this, RegisterStepTwoActivity.class);
        intent.putExtra("userEmail", etEmail.getText().toString())
                .putExtra("userPassword", etPassword.getText().toString())
                .putExtra("userRemember", rememberCreds.isChecked());
        activityResultLauncher.launch(intent);
    }


    public void loginUser(String email, String password){
        resetError();
        if (email.isEmpty() || password.isEmpty()){
            setError("Error:", "Email and Password fields cannot be empty.");
            return;
        }

        this.email = email;
        this.password = password;

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(this::loginUser_success).addOnFailureListener(this::loginUser_failure);
    }

    private void loginUser_failure(Exception e) {
        setError("Error while logging in:", e.getMessage());
    }

    private void loginUser_success(AuthResult authResult) {

        Intent intent = new Intent(this, HomeActivity.class);
        setError("Logged in successfully", "Using ErrorBox because lazy again.");
        if (remember){
            rememberCredentials(this.email, this.password);
        }

        if (register_new){
            intent.putExtra("register_new", true)
                    .putExtra("uid", authResult.getUser().getUid());
        }

        intent.putExtra("ws_uri", wsUri);

        startActivity(intent);
        finish();
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onActivityResult
    );

    private void onActivityResult(ActivityResult result){
        if (result.getResultCode() == RESULT_OK){
            Intent data = result.getData();
            String email;
            String password;

            register_new = true;

            try {
                email = data.getExtras().getString("email");
                password = data.getExtras().getString("password");
                remember = data.getExtras().getBoolean("remember");
            } catch (NullPointerException e){
                setError("Data missing, please login!", e.getMessage());
                return;
            }



            loginUser(email, password);

        } else {
            setError("Something went wrong...", "Please try logging in or signing up again.\nResult Code" + result.getResultCode());
        }
    }

    private void rememberCredentials(String email, String password){
        SharedPreferences sp = getSharedPreferences("authCreds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();

    }

}