package me.gm.twaddle.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import me.gm.twaddle.R;
import me.gm.twaddle.c2s.WSInstanceManager;
import me.gm.twaddle.activities.EditProfileActivity;
import me.gm.twaddle.activities.LoginActivity;
import me.gm.twaddle.obj.SettingsItem;
import me.gm.twaddle.rvadapters.SettingsItemAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    TextView tvUsername;
    TextView tvUsertag;

    RecyclerView rvSettings;

    private static SettingsItem[] settingsArray = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        tvUsername = v.findViewById(R.id.tv_settings_username);
        tvUsertag = v.findViewById(R.id.tv_settings_usertag);

        updateUserDetails();

        rvSettings = v.findViewById(R.id.settings_rvList);

        if (settingsArray == null) createSettingsArray(v);

        rvSettings.setAdapter(new SettingsItemAdapter(v.getContext(), Arrays.asList(settingsArray)));

        rvSettings.setLayoutManager(new LinearLayoutManager(v.getContext()));
        
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserDetails();
    }

    private void updateUserDetails(){
        tvUsername.setText(WSInstanceManager.getUserData().username());
        tvUsertag.setText("@"+WSInstanceManager.getUserData().userTag());
    }

    private void createSettingsArray(View v) {

        settingsArray = new SettingsItem[]{
                new SettingsItem(
                        "Edit Profile",
                        R.drawable.baseline_person,
                        view -> {
                            Intent intent = new Intent(v.getContext(), EditProfileActivity.class);
                            v.getContext().startActivity(intent);
                        }
                ),
                new SettingsItem(
                        "Log Out",
                        R.drawable.baseline_logout_24,
                        view -> {
                            new AlertDialog.Builder(view.getContext(), R.style.Theme_AlertDialog)
                                    .setTitle("Are you sure you want to log out?")
                                    .setPositiveButton("Yes", (dialogInterface, i) -> performLogout(view))
                                    .setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()))
                                    .show();
                        }
                )
        };

    }


    private void performLogout(View view){
        SharedPreferences sp = getContext().getSharedPreferences("authCreds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("email")
                .remove("password")
                .remove("user_id");
        editor.apply();

        Log.i("SETTINGS", sp.getAll().toString());

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("ws_uri", WSInstanceManager.getInstance().getClient().getURI().toString());
        startActivity(intent);
        ((Activity)view.getContext()).finishAffinity();
    }
}