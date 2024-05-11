package me.gm.twaddle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        btnLogout = v.findViewById(R.id.btn_logOut);
        btnLogout.setOnClickListener(this::onClick_logout);
        
        return v;
    }

    private void onClick_logout(View view) {
        if (view.getId() != R.id.btn_logOut) return;
        SharedPreferences sp = getContext().getSharedPreferences("authCreds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("email")
                .remove("password")
                .remove("user_id");
        editor.apply();

        Log.i("SETTINGS", sp.getAll().toString());

        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finishAffinity();
    }
}