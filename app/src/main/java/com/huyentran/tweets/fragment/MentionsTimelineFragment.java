package com.huyentran.tweets.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyentran.tweets.TwitterApplication;
import com.huyentran.tweets.TwitterClient;
import com.huyentran.tweets.db.MyDatabase;
import com.huyentran.tweets.models.Tweet;
import com.huyentran.tweets.models.Tweet_Table;
import com.huyentran.tweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.huyentran.tweets.TwitterClient.API_MENTIONS_TIMELINE;

/**
 * {@link TweetsListFragment} for displaying a user's timeline of mentions.
 */
public class MentionsTimelineFragment extends TweetsListFragment {
    private User user;

    public static MentionsTimelineFragment newInstance() {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        initTimeline();
        return view;
    }

    /**
     * Initializes the timeline with tweets. If the db contains saved tweets, they are loaded.
     * Otherwise, fresh tweets are loaded from an API request.
     */
    private void initTimeline() {
        List<Tweet> savedTweets = SQLite.select().from(Tweet.class)
                .where(Tweet_Table.source.is(API_MENTIONS_TIMELINE))
                .orderBy(Tweet_Table.uid, false).queryList();
        if (savedTweets.isEmpty()) {
            Log.d("DEBUG", "No saved mention tweets. Fetching fresh tweets from API");
            populateTweets();
        } else {
            Log.d("DEBUG", String.format("Loading %d saved mention tweets", savedTweets.size()));
            this.curMaxId = appendTweets(savedTweets);
        }
    }

    /**
     * Sends an async request to fetch tweets for the authenticated user's mentions timeline
     */
    @Override
    public void populateTweets() {
        Log.d("DEBUG", String.format("populateTimeline (mentions) with maxId: %d", this.curMaxId));

        this.client.getMentionsTimeline(this.curMaxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", String.format("getMentionsTimeline success: %s", response.toString()));
                List<Tweet> tweetResults = Tweet.fromJsonArray(response, API_MENTIONS_TIMELINE);
                curMaxId = appendTweets(tweetResults);
                MyDatabase.persistTweets(tweetResults);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public void clearTweets() {
        Log.d("DEBUG", "Clearing mentions timeline tweets...");
        SQLite.delete(Tweet.class)
                .where(Tweet_Table.source.is(API_MENTIONS_TIMELINE))
                .async()
                .execute();
        super.clearTweets();
    }

    @Override
    public void insertTopTweet(Tweet tweet) {
        // check that the tweet is a self mention before inserting
        if (user !=null
                && tweet.getBody().contains(String.format("@%s", this.user.getScreenName()))) {
            super.insertTopTweet(tweet);
            Tweet copy = Tweet.copy(tweet);
            copy.setSource(API_MENTIONS_TIMELINE);
            copy.save();
            Log.d("DEBUG",String.format("Inserted new tweet to top of mentions timeline " +
                    "and persisted to db: [%d] %s", copy.getId(), copy.getBody()));
        }
    }

    public void setUser(User user) {
        this.user = user;
    }
}
