package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import me.gm.twaddle.c2s.Payload;
import me.gm.twaddle.c2s.WSAPI;

public class NewChatActivity extends AppCompatActivity {

    EditText addTag;
    Button newChat;

    TextView errTitle;
    TextView errDesc;

    LinearLayout errBox;

    private WSAPI wsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        addTag = findViewById(R.id.et_nc_addTag);
        newChat = findViewById(R.id.btn_nc_createNewChat);

        errTitle = findViewById(R.id.tv_nc_errTitle);
        errDesc = findViewById(R.id.tv_nc_errDesc);
        errBox = findViewById(R.id.ll_nc_errBox);

        newChat.setOnClickListener(this::onClick_btnNewChat);

        wsApi = WSInstanceManager.getInstance();

    }

    private void onClick_btnNewChat(View view) {
        if (view.getId() != newChat.getId()){
            return;
        }

        String tag = addTag.getText().toString();

        if (tag.isEmpty()){
            errTitle.setText("Failed to create new chat");
            errDesc.setText("Tag can't be empty");
            errBox.setVisibility(View.VISIBLE);
        }
        int userID = getSharedPreferences("authCreds", MODE_PRIVATE).getInt("user_id", 0);
        if (userID == 0){
            errTitle.setText("Failed to create new chat");
            errDesc.setText("Could not find sneder UserID");
            errBox.setVisibility(View.VISIBLE);
        }

        Payload res;

        try {
            res = Payload.event(Payload.Events.CREATE_USER_CHAT, new JSONObject()
                    .put("orig_user_id", userID)
                    .put("recv_user_tag", tag));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Log.i("NEWCHAT", res.getData().toString());
    }
}