package com.example.cbc.the_hack.module.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbc.the_hack.entity.NewPoem;
import com.example.cbc.the_hack.entity.Poem;

import java.util.List;

import com.example.cbc.R;

/**
 * Created by ABINGCBC
 * on 2020-04-17
 */
public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<NewPoem> poems;

    public CardStackAdapter(List<NewPoem> poems) {
        this.poems = poems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_spot, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int circular = position % poems.size();
        NewPoem poem = poems.get(circular);
        holder.title.setText(poem.getPoetryName());
        holder.dynasty.setText(poem.getPoetryDynasty());
        holder.author.setText(poem.getPoetryAuthor());
        holder.body.setText(poem.getPoetryBody());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView dynasty;
        TextView author;
        TextView body;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            dynasty = itemView.findViewById(R.id.dynasty);
            author = itemView.findViewById(R.id.author);
            body = itemView.findViewById(R.id.body);
        }
    }
}
