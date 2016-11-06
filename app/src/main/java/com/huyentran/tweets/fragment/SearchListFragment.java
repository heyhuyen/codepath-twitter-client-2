package com.huyentran.tweets.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyentran.tweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.huyentran.tweets.TwitterClient.API_SEARCH_TWEETS;

/**
 * {@link TweetsListFragment} for displaying a tweet search results.
 */
public class SearchListFragment extends TweetsListFragment {

    private String query;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        this.curMaxId = -1;
        return view;
    }

    /**
     * Sends an async request to query tweets
     */
    @Override
    public void populateTweets() {
        if (this.query == null || TextUtils.isEmpty(this.query)) {
            Log.d("DEBUG", "No query");
            return;
        }
        Log.d("DEBUG", String.format("populateTweets (search) with maxId: %d", this.curMaxId));
        this.client.getTweets(this.curMaxId, this.query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", String.format("populateTweets success: %s", response.toString()));
                try {
                    JSONArray results = response.getJSONArray("statuses");
                    List<Tweet> tweetResults = Tweet.fromJsonArray(results, API_SEARCH_TWEETS);
                    // TODO: show something different if results are empty
                    curMaxId = appendTweets(tweetResults);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public void insertTopTweet(Tweet tweet) {
        // do nothing
    }

    /**
     * Performs a search for the given query and updates the results.
     * @param query the query to search
     */
    public void search(String query) {
        super.clearTweets();
        this.query = query;
        populateTweets();
    }

}

