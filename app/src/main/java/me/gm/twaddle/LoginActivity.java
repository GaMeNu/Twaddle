package me.gm.twaddle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gm.twaddle.c2s.RespPayload;
import me.gm.twaddle.c2s.WSAPI;
import me.gm.twaddle.obj.Message;
import me.gm.twaddle.obj.User;
import me.gm.twaddle.rvextras.SingleMessageAdapter;

public class LoginActivity extends AppCompatActivity {

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


    public static class SingleChatActivity extends AppCompatActivity {

        private long chatID;
        private TextView tvUsername;
        private EditText etMessage;
        private RecyclerView rvMsgs;
        private SingleMessageAdapter rvMsgsAdapter;
        ImageButton btnSend;
        ImageButton btnBack;

        private WSAPI wsApi;

        private Map<Long, User> userMap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_single_chat);

            // Get instances
            tvUsername = findViewById(R.id.tv_sc_username);
            rvMsgs = findViewById(R.id.singleChat_messages);
            etMessage = findViewById(R.id.sc_etMessage);
            btnSend = findViewById(R.id.sc_btnSend);
            btnBack = findViewById(R.id.sc_btnBack);
            btnBack.setOnClickListener(view -> {
                    if (view.getId() == btnBack.getId()) finish();
                }
            );

            if (getSupportActionBar().isShowing()) getSupportActionBar().hide();

            // Get WSAPI
            wsApi = WSInstanceManager.getInstance();

            chatID = getIntent().getLongExtra("chat_id", 0);

            // Oopsie daisy! There was an error :(
            if (chatID == 0){
                new AlertDialog.Builder(SingleChatActivity.this)
                        .setTitle("Failed to load chat!")
                        .setOnDismissListener(dialogInterface -> finishAffinity())
                        .setPositiveButton("OK", (dialogInterface, i) -> finish())
                        .show();
            }

            // Request chat load thru WSAPI
            wsApi.reqs()
                    .loadSingleChat(chatID)
                    .onResponse(this::loadChat)
                    .send();



            // And also another weird error if we messed up in a weird way
            String chatName = getIntent().getStringExtra("chat_name");
            if (chatName == null){
                chatName = "Failed to load";
            }

            wsApi.addPayloadHandler("op_onMessage_singleChat", pl -> {
                if (pl.getOpcode() != ((short)2)) return;
                Log.i("SINGLE_CHAT", pl.getData().toString());
                JSONObject data = pl.getData();
                Message msg = Message.fromJSONObject(data);
                appendMessage(msg);
            });

            tvUsername.setText(chatName);

            btnSend.setOnClickListener(this::sendMessage);


        }

        private void appendMessage(Message msg){
            boolean scroll = !rvMsgs.canScrollVertically(1);
            runOnUiThread(() -> {
                rvMsgsAdapter.addItem(msg);
                if (scroll)
                    rvMsgs.smoothScrollToPosition(0);
            });
        }

        private void sendMessage(View view) {
            if (view.getId() != btnSend.getId()) return;
            if (etMessage.getText().toString().isEmpty()) return;
            SendableMessage sndMsg = new SendableMessage()
                    .setChatID(chatID)
                    .setContent(etMessage.getText().toString());
            wsApi.reqs()
                    .sendChatMsg(sndMsg)
                    .onResponse(pl -> {
                        try {
                            JSONObject data = pl.getData().getJSONObject("data");
                            Message msg = Message.fromJSONObject(data);
                            appendMessage(msg);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .send();
            etMessage.setText("");
        }

        /**
         * Load the chat
         * @param pl Payload to load from
         */
        @SuppressLint("NotifyDataSetChanged")
        private void loadChat(RespPayload pl){
            userMap = new HashMap<>();
            List<Message> messages = new ArrayList<>();
            try {
                JSONObject data = pl.getData().getJSONObject("data");
                JSONArray users = data.getJSONArray("users");
                for (int i=0; i < users.length(); i++){
                    User user = User.fromJSONObject(users.getJSONObject(i));
                    this.userMap.put(user.getUserID(), user);
                }

                JSONArray msgs = data.getJSONArray("messages");
                for (int i=0; i < msgs.length(); i++){
                    JSONObject msg = msgs.getJSONObject(i);
                    messages.add(Message.fromJSONObject(msg));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Log.i("SINGLE_CHAT", messages.toString());

            rvMsgsAdapter = new SingleMessageAdapter(this, messages);
            rvMsgsAdapter.setUserMap(userMap);
            runOnUiThread(() -> {
                rvMsgs.setAdapter(rvMsgsAdapter);
                LinearLayoutManager lmng = new LinearLayoutManager(this);
                lmng.setReverseLayout(true);
                rvMsgs.setLayoutManager(lmng);
                rvMsgsAdapter.notifyDataSetChanged();
            });
        }
    }
}