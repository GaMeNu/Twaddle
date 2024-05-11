package me.gm.twaddle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import me.gm.twaddle.obj.Message;

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
        DisplayChat chat = objs.get(position);

        holder.timeLastMsg.setText(LocalDateTime.ofEpochSecond(chat.getTimeLastMsg(), 0, defaultOffset)
                .format(formatter));
        Log.i("CHATS_ADAPTER", "User locale: " + formatter.getLocale());
        Log.i("CHATS_ADAPTER", "TIME: " + chat.getTimeLastMsg());
        holder.dispName.setText(chat.getName());
        holder.msgPreview.setText(Message.fromID(chat.getLastMessage()).getContent());
        if (chat.getUnreads() < 99)
            holder.unreadBubble.setText("" + chat.getUnreads());
        else
            holder.unreadBubble.setText("99+");
        if (chat.unreads > 0) holder.unreadBubble.setVisibility(View.VISIBLE);
        else holder.unreadBubble.setVisibility(View.GONE);

        holder.msgPreview.setText(chat.getLastMsgPreview());

        holder.itemView.setOnClickListener(view -> view.getContext().startActivity(
                new Intent(view.getContext(), SingleChatActivity.class)
                        .putExtra("chat_id", chat.getChatID())
        ));

    }


    @Override
    public int getItemCount() {
        Log.i("CHATS_ADAPTER", "Objs size: " + objs.size());
        return objs.size();
    }

}
