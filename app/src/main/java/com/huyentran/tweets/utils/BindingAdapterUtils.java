package com.huyentran.tweets.utils;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huyentran.tweets.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Utils for data binding.
 */
public class BindingAdapterUtils {
    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String imageUrl) {
        imageView.setImageResource(0);
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(imageView.getContext()).load(imageUrl)
                    .placeholder(R.drawable.icon_like)
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(imageView.getContext(), 5, 0))
                    .into(imageView);
        }
    }
}
