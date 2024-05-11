package me.gm.twaddle;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class DirectMessagesFragment extends Fragment {

    RecyclerView chats;
    FloatingActionButton btnNewChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_direct_messages, container, false);

        chats = v.findViewById(R.id.directs_chatsRecyclerView);
        chats.setLayoutManager(new LinearLayoutManager(v.getContext()));

        btnNewChat = v.findViewById(R.id.btn_newChat);
        btnNewChat.setOnClickListener(this::onClick_btnNewChat);

        DisplayChat[] arr = {
                new DisplayChat(
                        1,
                        "Johnny Smith",
                        0 ,
                        1,
                        "Hello",
                        0L
                ),
                new DisplayChat(
                        2,
                        "Johnny John!",
                        1 ,
                        2,
                        "Hello",
                        1710921371L
                ),
                new DisplayChat(
                        1272,
                        "Exclamation mark.",
                        100,
                        5,
                        "Hello",
                        60L
                ),
                new DisplayChat(
                        122,
                        "Test",
                        7,
                        5,
                        "Hello",
                        60L
                ),
                new DisplayChat(
                        72,
                        "Test",
                        69,
                        5,
                        "Hello",
                        60L
                ),
                new DisplayChat(
                        127,
                        "Test",
                        33,
                        5,
                        "Hello",
                        60L
                ),
                new DisplayChat(
                        65,
                        "Test",
                        420,
                        5,
                        "Hello",
                        60L
                ),
                new DisplayChat(
                        100,
                        "Test",
                        1,
                        5,
                        "Hello",
                        60L
                ),
                new DisplayChat(
                        69,
                        "Test",
                        17,
                        5,
                        "Hello",
                        60L
                ),
                new DisplayChat(
                        13,
                        "Test",
                        17,
                        5,
                        "Hello",
                        60L
                )

        };

        ArrayList<DisplayChat> objs = new ArrayList<>(Arrays.asList(arr));

        DisplayChatAdapter adapter = new DisplayChatAdapter(v.getContext(), objs);

        chats.setAdapter(adapter);

        return v;
    }

    private void onClick_btnNewChat(View view) {
        if (view.getId() != btnNewChat.getId()) return;

        startActivity(new Intent(getActivity(), NewChatActivity.class));
    }
}