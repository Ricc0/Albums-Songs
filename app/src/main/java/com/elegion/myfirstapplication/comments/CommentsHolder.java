package com.elegion.myfirstapplication.comments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elegion.myfirstapplication.R;
import com.elegion.myfirstapplication.model.Comment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CommentsHolder extends RecyclerView.ViewHolder{

    private DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    private DateFormat timeFormat = SimpleDateFormat.getTimeInstance();

    private DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);

    private TextView mAuthor;
    private TextView mText;
    private TextView mTime;

    public CommentsHolder(View itemView) {
        super(itemView);

        mAuthor = itemView.findViewById(R.id.tv_author);
        mText = itemView.findViewById(R.id.tv_text);
        mTime = itemView.findViewById(R.id.tv_time);
    }

    public void bind(Comment comment) {
        mAuthor.setText(comment.getAuthor());
        mText.setText(comment.getText());
        mTime.setText(comment.getTimestamp());

        try {
            Date date = originalFormat.parse(comment.getTimestamp());
            long diffs = System.currentTimeMillis() - date.getTime();
            if(diffs >= TimeUnit.DAYS.toMillis(1)) {
                mTime.setText(dateFormat.format(date));
            } else {
                mTime.setText(timeFormat.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
