package com.huyentran.tweets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huyentran.tweets.databinding.ItemTweetDraftBinding;

/**
 * ViewHolder class for tweet drafts.
 */
public class TweetDraftViewHolder extends RecyclerView.ViewHolder {
    final ItemTweetDraftBinding binding;
    private TextView tvBody;

    public TweetDraftViewHolder(View itemView) {
        super(itemView);
        this.binding = ItemTweetDraftBinding.bind(itemView);
        this.tvBody = this.binding.tvBody;
    }

    public TextView getTvBody() {
        return this.tvBody;
    }

    public void setTvBody(TextView tvBody) {
        this.tvBody = tvBody;
    }
}
