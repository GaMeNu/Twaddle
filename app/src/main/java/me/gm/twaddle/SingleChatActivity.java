package me.gm.twaddle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class SingleChatActivity extends AppCompatActivity {

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