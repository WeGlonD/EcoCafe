package com.example.ecocafe;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecocafe.firebase.Cafe;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{

    private ArrayList<Cafe> arrayList;
    private static Context context;

    public CustomAdapter(ArrayList<Cafe> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getPic())
                .into(holder.iv_pic);

        holder.tv_event.setText(arrayList.get(position).getEvent());
        holder.tv_name.setText(arrayList.get(position).getName());
        holder.tv_link.setText(arrayList.get(position).getLink());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        TextView tv_name;
        TextView tv_event;
        TextView tv_link;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_pic = itemView.findViewById(R.id.iv_pic);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_event = itemView.findViewById(R.id.tv_event);
            this.tv_link = itemView.findViewById(R.id.tv_link);
            tv_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView now = (TextView)view;
                    String url = (String) now.getText();
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setData(Uri.parse(url));
                    context.startActivity(it);
                }
            });
        }
    }
}
