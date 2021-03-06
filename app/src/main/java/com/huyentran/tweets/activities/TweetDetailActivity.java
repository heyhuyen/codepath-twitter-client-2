package com.huyentran.tweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huyentran.tweets.R;
import com.huyentran.tweets.databinding.ActivityTweetDetailBinding;
import com.huyentran.tweets.models.Media;
import com.huyentran.tweets.models.Tweet;
import com.huyentran.tweets.utils.PatternEditableBuilder;
import com.huyentran.tweets.utils.TweetDateUtils;

import org.parceler.Parcels;

import java.util.regex.Pattern;

/**
 * Activity for displaying a single tweet in detail.
 */
public class TweetDetailActivity extends AppCompatActivity {

    private ActivityTweetDetailBinding binding;
    private Tweet tweet;
    private ImageView ivMedia;
    private TextView tvTimestamp;
    private TextView tvBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet_detail);

        Toolbar toolbar = this.binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        this.binding.setTweet(this.tweet);
        this.binding.setUser(this.tweet.getUser());
        this.binding.setMedia(this.tweet.getMedia());
        this.binding.executePendingBindings();
        setupViews();
    }

    private void setupViews() {
        this.ivMedia = this.binding.ivMediaPhoto;
        this.tvTimestamp = this.binding.tvTimestamp;
        this.tvBody = this.binding.tvBody;

        Media media = this.tweet.getMedia();
        if (media == null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.tvTimestamp.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.tvBody);
            this.tvTimestamp.setLayoutParams(params);
            this.ivMedia.setVisibility(View.GONE);
        } else {
            String textBody = tweet.getBody();
            this.tvBody.setText(textBody.replace(tweet.getMedia().getUrl(), ""));
        }

        // get timestamp string; TODO: use data binding
        this.tvTimestamp.setText(TweetDateUtils.getDetailDateFormat(this.tweet.getCreatedAt()));

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), R.color.colorAccent,
                        text -> profileOnClick(text.substring(1)))
                .into(this.tvBody);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), R.color.colorAccent,
                        text -> hashtagOnClick(text))
                .into(this.tvBody);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches {@link ProfileActivity} for the user with the given screen name.
     */
    public void profileOnClick(String screenName) {
        Log.d("DEBUG", String.format("Launching Profile Activity for user: @%s", screenName));
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("screen_name", screenName);
        startActivity(intent);
    }

    /**
     * Launches {@link SearchActivity} with the given hashtag query
     */
    public void hashtagOnClick(String hashtag) {
        Log.d("DEBUG", String.format("Launching Search Activity for: %s", hashtag));
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("query", hashtag);
        startActivity(intent);
    }
}
