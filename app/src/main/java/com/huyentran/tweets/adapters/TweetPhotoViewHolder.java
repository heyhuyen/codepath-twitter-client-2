package com.huyentran.tweets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyentran.tweets.databinding.ItemTweetPhotoBinding;

/**
 * ViewHolder class for tweets with embedded photos.
 */
public class TweetPhotoViewHolder extends RecyclerView.ViewHolder {
    final ItemTweetPhotoBinding binding;
    private ImageView ivProfilePic;
    private TextView tvUserName;
    private TextView tvScreenName;
    private TextView tvTime;
    private TextView tvBody;

    public TweetPhotoViewHolder(View itemView) {
        super(itemView);
        this.binding = ItemTweetPhotoBinding.bind(itemView);
        this.ivProfilePic = this.binding.ivProfilePic;
        this.tvUserName = this.binding.tvUserName;
        this.tvScreenName = this.binding.tvScreenName;
        this.tvTime = this.binding.tvTime;
        this.tvBody = this.binding.tvBody;
    }

    public ImageView getIvProfilePic() {
        return this.ivProfilePic;
    }

    public void setIvProfilePic(ImageView ivProfilePic) {
        this.ivProfilePic = ivProfilePic;
    }

    public TextView getTvUserName() {
        return this.tvUserName;
    }

    public void setTvUserName(TextView tvUserName) {
        this.tvUserName = tvUserName;
    }

    public TextView getTvScreenName() {
        return this.tvScreenName;
    }

    public void setTvScreenName(TextView tvScreenName) {
        this.tvScreenName = tvScreenName;
    }

    public TextView getTvTime() {
        return this.tvTime;
    }

    public void setTvTime(TextView tvTime) {
        this.tvTime = tvTime;
    }

    public TextView getTvBody() {
        return this.tvBody;
    }

    public void setTvBody(TextView tvBody) {
        this.tvBody = tvBody;
    }
}

