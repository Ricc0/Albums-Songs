package com.elegion.myfirstapplication.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.elegion.myfirstapplication.ApiUtils;
import com.elegion.myfirstapplication.App;
import com.elegion.myfirstapplication.R;
import com.elegion.myfirstapplication.db.MusicDao;
import com.elegion.myfirstapplication.model.Comment;
import com.elegion.myfirstapplication.model.IdResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ALBUM_ID = "ALBUM_ID";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefresher;
    private View mErrorView;
    private int mAlbumId;
    private ImageButton mSendButton;
    private EditText mCommentEditText;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    @NonNull
    private final CommentsAdapter mCommentsAdapter = new CommentsAdapter();

    public static CommentsFragment newInstance(int albumId) {
        Bundle args = new Bundle();
        args.putInt(ALBUM_ID, albumId);
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fr_comments_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRefresher = view.findViewById(R.id.comments_refresher);
        mRefresher.setOnRefreshListener(this);
        mErrorView = view.findViewById(R.id.errorView);
        mSendButton = view.findViewById(R.id.sendMessage);
        mCommentEditText = view.findViewById(R.id.etText);

        mRecyclerView = view.findViewById(R.id.comments_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mCommentsAdapter);

        mSendButton.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(mCommentEditText.getText())) {
                showToast("Пустой edit text");
                return;
            }

            Comment comment = new Comment();
            comment.setAlbumId(mAlbumId);
            comment.setText(mCommentEditText.getText().toString());

            Disposable request = ApiUtils.getApiService(false).postComment(comment)
                    .subscribeOn(Schedulers.io())
                    .map(rb -> ApiUtils.getGson().fromJson(rb.string(), IdResult.class).getId())
                    .flatMap(id -> ApiUtils.getApiService(false).getCommentById(id))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> mRefresher.setRefreshing(true))
                    .doFinally(() -> mRefresher.setRefreshing(false))
                    .subscribe(com -> {
                        mCommentEditText.setText("");
                        mCommentsAdapter.addComment(com);
                        mRecyclerView.scrollToPosition(mCommentsAdapter.getItemCount() - 1);
                    }, throwable -> {
                        showToast(throwable.getMessage());
                    });

            mDisposables.add(request);

        });

        mCommentEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                mSendButton.performClick();
            }
            return true;
        });

        getActivity().setTitle("Комментарии");

        mAlbumId = getArguments().getInt(ALBUM_ID, -1);
        if (mAlbumId == -1) {
            throw new IllegalStateException("Provide album id extra");
        }

        loadAlbumComments(false);
    }

    private void loadAlbumComments(boolean withToast) {
        Disposable request = ApiUtils.getApiService(false).getAlbumComments(mAlbumId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(comments -> getMusicDao().insertComments(comments))
                .onErrorReturn(throwable -> {
                    if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.getClass())) {
                        return getMusicDao().getAlbumComments(mAlbumId);
                    }
                    return null;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mRefresher.setRefreshing(true))
                .doFinally(() -> mRefresher.setRefreshing(false))
                .subscribe(comments -> {
                    if (withToast) {
                        if (comments.size() == mCommentsAdapter.getItemCount()) {
                            showToast("Новых комментариев нет");
                        } else {
                            showToast("Комментарии обновлены");
                        }
                    }
                    mCommentsAdapter.setComments(comments);
                    mRecyclerView.scrollToPosition(mCommentsAdapter.getItemCount() - 1);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mErrorView.setVisibility(View.GONE);
                }, throwable -> {
                    mCommentsAdapter.clearComments();
                    mRecyclerView.setVisibility(View.GONE);
                    mErrorView.setVisibility(View.VISIBLE);
                });

        mDisposables.add(request);
    }

    @Override
    public void onRefresh() {
        loadAlbumComments(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mDisposables.dispose();
    }

    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private MusicDao getMusicDao() {
        return ((App) getActivity().getApplication()).getDatabase().getMusicDao();
    }
}
