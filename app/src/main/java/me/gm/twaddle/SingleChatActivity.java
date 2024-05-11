package me.gm.twaddle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import me.gm.twaddle.c2s.WSAPI;

public class SingleChatActivity extends AppCompatActivity {

    private long chatID;

    private WSAPI wsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);

        chatID = getIntent().getLongExtra("chat_id", 0);

        if (chatID == 0){
            new AlertDialog.Builder(SingleChatActivity.this)
                    .setTitle("Failed to load chat!")
                    .setOnDismissListener(dialogInterface -> finishAffinity())
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        finish();
                    })
                    .show();
        }

        wsApi = WSInstanceManager.getInstance();

    }
}