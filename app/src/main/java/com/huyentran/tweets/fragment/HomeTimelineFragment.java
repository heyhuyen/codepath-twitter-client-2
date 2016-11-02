package com.huyentran.tweets.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyentran.tweets.TwitterApplication;
import com.huyentran.tweets.TwitterClient;
import com.huyentran.tweets.db.MyDatabase;
import com.huyentran.tweets.models.Tweet;
import com.huyentran.tweets.models.Tweet_Table;
import com.huyentran.tweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * {@link TweetsListFragment} for displaying a user's home timeline of tweets.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private long curMaxId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.client = TwitterApplication.getRestClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        setupViews();
        initTimeline();
        return view;
    }

    private void setupViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        this.rvTweets.setLayoutManager(layoutManager);
        this.rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("DEBUG",
                        String.format("Endless Scroll: onLoadMore(%d, %d)", page, totalItemsCount));
                populateTimeline();
            }
        });

        // pull to refresh swipe container
        this.swipeContainer.setOnRefreshListener(() -> {
            Log.d("DEBUG", "Swipe Refresh");
            curMaxId = -1;
            clearTweets();
            populateTimeline();
        });
    }

    /**
     * Initializes the timeline with tweets. If the db contains saved tweets, they are loaded.
     * Otherwise, fresh tweets are loaded from an API request.
     */
    private void initTimeline() {
        this.curMaxId = -1;
        // check db for saved tweets
        List<Tweet> savedTweets = SQLite.select().from(Tweet.class)
                .orderBy(Tweet_Table.uid, false).queryList();
        if (savedTweets.isEmpty()) {
            Log.d("DEBUG", "No saved tweets. Fetching fresh tweets from API");
            populateTimeline();
        } else {
            Log.d("DEBUG", String.format("Loading %d saved tweets", savedTweets.size()));
            this.curMaxId = appendTweets(savedTweets);
        }
    }

    /**
     * Sends an async request to fetch tweets for the authenticated user's home timeline
     */
    private void populateTimeline() {
        Log.d("DEBUG", String.format("populateTimeline with maxId: %d", this.curMaxId));

        this.client.getHomeTimeline(this.curMaxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", String.format("getHomeTimeline success: %s", response.toString()));
                List<Tweet> tweetResults = Tweet.fromJsonArray(response);
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

}
