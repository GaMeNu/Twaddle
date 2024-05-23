package me.gm.twaddle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.gm.twaddle.c2s.WSAPI;

/**
 * A simple {@link Fragment} subclass.
 */
public class DirectMessagesFragment extends Fragment {

    RecyclerView chats;
    FloatingActionButton btnNewChat;

    View view;

    WSAPI wsApi = WSInstanceManager.getInstance();

    /**
     * Request the webserver to reload the chats.
     * This will return a JSON Object containing all chats.
     * @param v View to reload
     */
    private void requestChatsReload(View v){
        wsApi.reqs()
                .loadChats(WSInstanceManager.getUserData().userID())
                .onResponse(pl -> ((Activity)view.getContext()).runOnUiThread(() -> {
                    DisplayChatAdapter adapter = new DisplayChatAdapter(v.getContext(), DisplayChat.fromPayload(pl));
                    chats.setAdapter(adapter);
                }))
                .send();
    }

    /**
     * Set the adapter. Request chat reload.
     * This WILL throw an error to the log, but it's fine because the empty adapter gets set later.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_direct_messages, container, false);

        // Initialise chats RV and request a chats reload (will execute on seperate thread thru WS)
        chats = v.findViewById(R.id.directs_chatsRecyclerView);
        chats.setLayoutManager(new LinearLayoutManager(v.getContext()));
        requestChatsReload(v);

        view = v;

        // Initialise the Create Chat button and set the OnClick Listener
        btnNewChat = v.findViewById(R.id.btn_newChat);
        btnNewChat.setOnClickListener(this::onClick_btnNewChat);

        /*

        // Old testing code, please ignore.

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

        */
        return v;
    }

    /**
     * On resume - request chat reload.
     * Mostly useful after exiting a chat or after adding a new chat.
     * Could probably be optimized by, well,
     * not spamming the webserver with requests and using what we already have?
     * Just a thought /s
     */
    @Override
    public void onResume() {
        super.onResume();
        requestChatsReload(this.getView());
    }

    /**
     * Create a new chat.
     * @param view The View to check.
     */
    private void onClick_btnNewChat(View view) {
        if (view.getId() != btnNewChat.getId()) return;

        startActivity(new Intent(getActivity(), NewChatActivity.class));
    }
}