package me.gm.twaddle.rvextras;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import java.util.Map;

import me.gm.twaddle.R;
import me.gm.twaddle.TTSService;
import me.gm.twaddle.obj.Message;
import me.gm.twaddle.obj.User;

public class SingleMessageAdapter extends RecyclerView.Adapter<SingleMessageAdapter.SingleMessageViewHolder> {

    static class SingleMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private TextView timeSent;
        private TextView content;
        private ImageView pfp;
        public SingleMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.msgItem_username);
            this.timeSent = itemView.findViewById(R.id.msgItem_timeSent);
            this.content  = itemView.findViewById(R.id.msgItem_content );
            this.pfp      = itemView.findViewById(R.id.msgItem_pfp     );
        }
    }

    Context ctx;
    List<Message> objs;

    DateTimeFormatter formatter;

    ZoneOffset defaultOffset;

    Map<Long, User> userMap;

    public SingleMessageAdapter(@NonNull Context ctx, @NonNull List<Message> objs) {
        this.ctx = ctx;
        this.objs = objs;
        this.formatter = DateTimeFormatter.ofPattern("HH:mm");
        this.defaultOffset = ZoneOffset.ofTotalSeconds(ZonedDateTime.now().getOffset().getTotalSeconds());
    }

    public SingleMessageAdapter setUserMap(Map<Long, User> userMap) {
        this.userMap = userMap;
        return this;
    }

    @NonNull
    @Override
    public SingleMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((Activity)ctx).getLayoutInflater().inflate(R.layout.activity_messages_item, parent, false);
        return new SingleMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleMessageViewHolder holder, int position) {
        Message msg = objs.get(position);

        // Set message views
        User user = userMap.get(msg.getAuthorID());
        holder.username.setText(user.getDisplayName());
        holder.timeSent
                .setText(LocalDateTime.ofEpochSecond(msg.getTimeSent(), 0, defaultOffset)
                .format(formatter));
        holder.content.setText(msg.getContent());

        holder.username.setVisibility(View.VISIBLE);
        holder.timeSent.setVisibility(View.VISIBLE);
        holder.pfp.setVisibility(View.VISIBLE);

        if (position != objs.size() - 1){
            if (msg.getAuthorID() == objs.get(position + 1).getAuthorID()){
                holder.username.setVisibility(View.GONE);
                holder.timeSent.setVisibility(View.GONE);
                holder.pfp.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnLongClickListener(view -> {
            String textToRead = holder.content.getText().toString();
            Intent serviceIntent = new Intent(ctx, TTSService.class);
            serviceIntent.putExtra("tts", textToRead);
            ctx.startService(serviceIntent);
            return true;
        });

    }

    public void addItem(Message item){
        objs.add(0, item);
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return objs.size();
    }
}
