package com.huyentran.tweets.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

    @BindingAdapter({"bind:backgroundUrl"})
    public static void loadBackground(RelativeLayout relativeLayout, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Context context = relativeLayout.getContext();
            Glide.with(context).load(imageUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                    relativeLayout.setBackground(drawable);
                }
            });
        }
    }
}
