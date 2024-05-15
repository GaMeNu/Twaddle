package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.gm.twaddle.c2s.Payload;
import me.gm.twaddle.c2s.RespPayload;
import me.gm.twaddle.c2s.WSAPI;

public class NewChatActivity extends AppCompatActivity {

    EditText addTag;
    Button newChat;

    TextView errTitle;
    TextView errDesc;

    LinearLayout errBox;

    private WSAPI wsApi;

    private static final char[] validTagChars = "abcdefghijklmnopqrstuvwxyz0123456789._".toCharArray();

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

    private static boolean isValidChar(char ch){
        for (char c : validTagChars){
            if (ch == c) return true;
        }

        return false;
    }



    private void onClick_btnNewChat(View view) {
        if (view.getId() != newChat.getId()){
            return;
        }

        resetError();

        String tag = addTag.getText().toString();

        if (tag.isEmpty()){
            setError("Failed to create new chat", "Tag can't be empty");
            return;
        }

        if (tag.charAt(0) == '@'){
            if (tag.length() == 1){
                setError("Failed to create new chat", "Tag cannot be empty.");
                return;
            }
            tag = tag.substring(1);
        }

        for (int i = 0; i < tag.length(); i++){
            char curChar = tag.charAt(i);
            if (!isValidChar(curChar)){
                setError("Failed to create new chat", "Tag must only contain alphanumeric, '.', or '_' characters.");
                return;
            }
        }

        int userID = getSharedPreferences("authCreds", MODE_PRIVATE).getInt("user_id", 0);
        if (userID == 0){
            setError("Failed to create new chat", "Could not find your user ID");
            return;
        }

        Payload res;

        wsApi.reqs()
                .createChat(userID, tag)
                .onResponse(this::onResponse_newChat)
                .send();


    }

    /**
     * Check if we created the new chat or not
     * If not, set the errbox
     * else finish the activity (reloading chats will occur automatically)
     * @param pl the response payload
     */
    private void onResponse_newChat(RespPayload pl){
        if (!pl.isSuccessful()){

            setError("Failed to create new chat", "Serverside error");
            return;
        }

        finish();
    }

    /**
     * Set the errorbox
     * @param title errbox title
     * @param desc errbox description
     */
    private void setError(CharSequence title, CharSequence desc){
        runOnUiThread(() -> {
            errTitle.setText(title);
            errDesc.setText(desc);
            errBox.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Reset the errorbox
     */
    private void resetError(){
        runOnUiThread(() -> {
            errTitle.setText("");
            errDesc.setText("");
            errBox.setVisibility(View.INVISIBLE);
        });
    }
}