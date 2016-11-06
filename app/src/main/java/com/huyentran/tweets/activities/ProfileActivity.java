package com.huyentran.tweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.huyentran.tweets.R;
import com.huyentran.tweets.TwitterApplication;
import com.huyentran.tweets.TwitterClient;
import com.huyentran.tweets.adapters.TweetsArrayAdapter;
import com.huyentran.tweets.databinding.ActivityProfileBinding;
import com.huyentran.tweets.fragment.SearchListFragment;
import com.huyentran.tweets.fragment.TweetsListFragment;
import com.huyentran.tweets.fragment.UserHeaderFragment;
import com.huyentran.tweets.fragment.UserTimelineFragment;
import com.huyentran.tweets.models.Tweet;
import com.huyentran.tweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 * Activity for displaying a user's profile and tweets.
 */
public class ProfileActivity extends AppCompatActivity implements
        TweetsListFragment.TweetClickListener, TweetsArrayAdapter.TweetClickListener {

    private ActivityProfileBinding binding;
    private TwitterClient client;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        Toolbar toolbar = this.binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO: perhaps set an expiration (5 min?) and only fetch if the user data is "stale"
        // always fetch user data so we can keep it up to date
        this.client = TwitterApplication.getRestClient();
        String screenName = getIntent().getStringExtra("screen_name");
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJson(response);
                user.save();
                getSupportActionBar().setTitle(String.format("@%s", user.getScreenName()));
                if (savedInstanceState == null) {
                    setupFragments();
                }
            }
        };

        if (screenName == null) {
            this.client.getAuthenticatedUser(handler);
        } else {
            this.client.getUser(screenName, handler);
        }
    }

    private void setupFragments() {
        UserHeaderFragment headerFragment = UserHeaderFragment.newInstance(this.user);
        UserTimelineFragment timelineFragment = UserTimelineFragment.newInstance(this.user);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flHeader, headerFragment);
        ft.replace(R.id.flTimeline, timelineFragment);
        ft.commit();
    }

    /**
     * Launches {@link TweetDetailActivity} for the given tweet.
     */
    @Override
    public void tweetOnClick(Tweet tweet) {
        Log.d("DEBUG", "Launch TweetDetailActivity");
        Intent intent = new Intent(this, TweetDetailActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }

    /**
     * Does nothing since the user profile for the given screen name is already showing.
     */
    @Override
    public void profileOnClick(String screenName) {
        Log.d("DEBUG", String.format("Ignoring profileOnClick for screenname: %s. " +
                "Current profile: %s", screenName, this.user.getScreenName()));
    }

    /**
     * Launches {@link SearchActivity} with the given hashtag query
     */
    @Override
    public void hashtagOnClick(String hashtag) {
        Log.d("DEBUG", String.format("Launching Search Activity for: %s", hashtag));
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("query", hashtag);
        startActivity(intent);
    }
}
