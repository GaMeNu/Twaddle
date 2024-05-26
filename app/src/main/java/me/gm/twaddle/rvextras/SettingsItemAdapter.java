package me.gm.twaddle.rvextras;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.gm.twaddle.R;
import me.gm.twaddle.obj.SettingsItem;

public class SettingsItemAdapter extends RecyclerView.Adapter<SettingsItemAdapter.SettingsItemViewHolder> {

    class SettingsItemViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView name;

        public SettingsItemViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.setting_icon);
            name = itemView.findViewById(R.id.setting_name);
        }

    }
    Context ctx;
    List<SettingsItem> objs;

    public SettingsItemAdapter(Context ctx, List<SettingsItem> objs) {
        this.ctx = ctx;
        this.objs = objs;
    }

    @NonNull
    @Override
    public SettingsItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = ((Activity)ctx).getLayoutInflater().inflate(R.layout.activity_settings_item, parent, false);
        Log.i("SETTINGS_ADAPTER", objs.toString());

        return new SettingsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsItemViewHolder holder, int position) {
        SettingsItem item = objs.get(position);


        holder.name.setText(item.name());
        holder.icon.setImageResource(item.drawableID());

        // oc listener to start the setting's correlated activity
        holder.itemView.setOnClickListener(item.onClickListener());
    }

    @Override
    public int getItemCount() {
        return objs.size();
    }
}
