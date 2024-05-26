package me.gm.twaddle.rvextras;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import me.gm.twaddle.LoginActivity;
import me.gm.twaddle.R;
import me.gm.twaddle.obj.DisplayChat;

public class DisplayChatAdapter extends RecyclerView.Adapter<DisplayChatAdapter.DisplayChatViewHolder> {

    static class DisplayChatViewHolder extends RecyclerView.ViewHolder{

        private TextView dispName;

        private TextView msgPreview;
        private TextView timeLastMsg;
        private TextView unreadBubble;
        private ImageView profilePic;

        public DisplayChatViewHolder(@NonNull View itemView) {
            super(itemView);

            this.dispName = itemView.findViewById(R.id.chatItem_displayname);
            this.profilePic = itemView.findViewById(R.id.chatItem_profilePic);
            this.msgPreview = itemView.findViewById(R.id.chatItem_msgPreview);
            this.timeLastMsg = itemView.findViewById(R.id.chatItem_msgTime);
            this.unreadBubble = itemView.findViewById(R.id.chatItem_unreadBubble);
        }
    }

    Context ctx;
    List<DisplayChat> objs;

    DateTimeFormatter  formatter;
    ZoneOffset defaultOffset;

    public DisplayChatAdapter(@NonNull Context context, @NonNull List<DisplayChat> objects) {
        super();

        this.ctx = context;
        this.objs = objects;
        this.formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.defaultOffset = ZoneOffset.ofTotalSeconds(ZonedDateTime.now().getOffset().getTotalSeconds());

        //DateFormat.SHORT, LocaleList.getDefault().get(0)
    }

    @NonNull
    @Override
    public DisplayChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((Activity)ctx).getLayoutInflater().inflate(R.layout.activity_chats_item, parent, false);

        return new DisplayChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayChatViewHolder holder, int position) {
        // Get current chat
        DisplayChat chat = objs.get(position);

        // Set last active time
        holder.timeLastMsg
                .setText(LocalDateTime.ofEpochSecond(chat.getTimeLastMsg(), 0, defaultOffset)
                .format(formatter));

        // Set chat name
        holder.dispName.setText(chat.getName());

        // Set unreads (if over 99, set to 99+)
        if (chat.getUnreads() <= 99)
            holder.unreadBubble.setText("" + chat.getUnreads());
        else
            holder.unreadBubble.setText("99+");

        // Set whether the unreads bubble should be visible
        if (chat.getUnreads() > 0) holder.unreadBubble.setVisibility(View.VISIBLE);
        else holder.unreadBubble.setVisibility(View.GONE);

        // Set the last msg preview
        if (!chat.getLastMsgPreview().isEmpty()) {
            holder.msgPreview.setTextColor(Color.WHITE);
            holder.msgPreview.setText(chat.getLastMsgPreview());
        }
        else {
            holder.msgPreview.setTextColor(Color.GRAY);
            holder.msgPreview.setText("Nothing to see here... yet!");
        }

        // Set the On Click Listener for the view card
        holder.itemView.setOnClickListener(view -> {
            Bundle extras = new Bundle();
            extras.putLong("chat_id", chat.getChatID());
            extras.putString("chat_name", chat.getName());
            Intent intent = new Intent(view.getContext(), LoginActivity.SingleChatActivity.class);
            intent.putExtras(extras);
            view.getContext().startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return objs.size();
    }

}
