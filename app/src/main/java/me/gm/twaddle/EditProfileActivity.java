package me.gm.twaddle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import me.gm.twaddle.c2s.WSAPI;
import me.gm.twaddle.obj.User;

public class EditProfileActivity extends AppCompatActivity {

    TextView tvUsername;
    TextView tvUsertag;

    LinearLayout lUsername;
    LinearLayout lUsertag;

    EditText etUsername;
    EditText etUsertag;

    AlertDialog dialog;

    WSAPI wsapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tvUsername = findViewById(R.id.settings_tv_Username);
        tvUsertag = findViewById(R.id.settings_tv_usertag);
        lUsername = findViewById(R.id.settings_layout_editUsername);
        lUsertag = findViewById(R.id.settings_layout_editUsertag);

        wsapi = WSInstanceManager.getInstance();

        tvUsername.setText(WSInstanceManager.getUserData().username());
        tvUsertag.setText("@"+WSInstanceManager.getUserData().userTag());


        lUsername.setOnClickListener(view -> {
            if (view.getId() != lUsername.getId()) return;
            dialog = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_username)
                    .setPositiveButton("Confirm", this::onConfirm_updateDetails)
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        });

        lUsertag.setOnClickListener(view -> {
            if (view.getId() != lUsertag.getId()) return;
            dialog = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_usertag)
                    .setPositiveButton("Confirm", this::onConfirm_updateDetails)
                    .setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.cancel()))
                    .show();
        });
    }

    private void onConfirm_updateDetails(DialogInterface dialogInterface, int i) {

        Dialog dialog = (Dialog)dialogInterface;

        //Prepare predefined
        etUsername = dialog.findViewById(R.id.et_editUsername);
        etUsertag = dialog.findViewById(R.id.et_editUsertag);

        User user = WSInstanceManager.getUserData().toUser();

        // Prepare new data
        String username;
        String usertag;

        if (etUsername != null && !etUsername.getText().toString().isEmpty())
            username = etUsername.getText().toString();
        else
            username = user.getDisplayName();


        if (etUsertag != null && !etUsertag.getText().toString().isEmpty())
            usertag = etUsertag.getText().toString();
        else
            usertag = user.getUserTag();

        // Update user object
        user.setDisplayName(username).setUserTag(usertag);

        // Send request
        wsapi.reqs()
                .updateDetails(user)
                .onResponse(pl -> {
                    if (!pl.isSuccessful()) return;
                    User newUser = null;
                    try {
                        newUser = User.fromJSONObject(pl.getData().getJSONObject("data"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    WSInstanceManager.setUserData(WSInstanceManager.UserData.fromUser(newUser));
                    runOnUiThread(() -> {
                        tvUsername.setText(WSInstanceManager.getUserData().username());
                        tvUsertag.setText(WSInstanceManager.getUserData().userTag());
                        dialogInterface.cancel();
                    });
                }).send();
    }


}