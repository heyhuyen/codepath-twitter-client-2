package com.huyentran.tweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyentran.tweets.R;
import com.huyentran.tweets.models.TweetDraft;

import java.util.List;

/**
 * Custom adapter that takes {@link TweetDraft} objects and turns them into views to display in a list.
 */
public class TweetDraftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TweetDraft> mDrafts;
    private Context mContext;

    public TweetDraftAdapter(Context context, List<TweetDraft> drafts) {
        this.mDrafts = drafts;
        this.mContext = context;
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View defaultView = inflater.inflate(R.layout.item_tweet_draft, parent, false);
        viewHolder = new TweetDraftViewHolder(defaultView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TweetDraftViewHolder vh = (TweetDraftViewHolder) viewHolder;
        TweetDraft draft = this.mDrafts.get(position);
        vh.binding.setDraft(draft);
        vh.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return this.mDrafts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}
