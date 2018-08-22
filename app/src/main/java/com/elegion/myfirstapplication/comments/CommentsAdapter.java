package com.elegion.myfirstapplication.comments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.myfirstapplication.R;
import com.elegion.myfirstapplication.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsHolder>{
    @NonNull
    private List<Comment> mComments = new ArrayList<>();

    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_comment, parent, false);
        return new CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void setComments(List<Comment> commentsList) {
        mComments.clear();
        mComments.addAll(commentsList);
        notifyDataSetChanged();
    }

    public void addComment(Comment com) {
        mComments.add(com);
        notifyDataSetChanged();
    }

    public void clearComments() {
        mComments.clear();
        notifyDataSetChanged();
    }

}
