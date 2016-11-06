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
import com.huyentran.tweets.models.User;
import com.huyentran.tweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.huyentran.tweets.TwitterClient.API_USER_TIMELINE;

/**
 * {@link TweetsListFragment} for displaying a user's timeline of tweets.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private User user;

    public static UserTimelineFragment newInstance(User user) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = Parcels.unwrap(getArguments().getParcelable("user"));
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
                .where(Tweet_Table.source.is(API_USER_TIMELINE))
                .and(Tweet_Table.user_uid.is(this.user.getUid()))
                .orderBy(Tweet_Table.uid, false).queryList();
        if (savedTweets.isEmpty()) {
            Log.d("DEBUG", String.format("No saved user tweets for %s. " +
                    "Fetching fresh tweets from API", this.user.getScreenName()));
            populateTweets();
        } else {
            Log.d("DEBUG", String.format("Loading %d saved user tweets for %s",
                    savedTweets.size(), this.user.getScreenName()));
            this.curMaxId = appendTweets(savedTweets);
        }
    }

    /**
     * Sends an async request to fetch the user timeline tweets for the user associated with
     * the provided screen name. If the provided screen name is null, the authenticated user's
     * timeline is fetched.
     */
    @Override
    public void populateTweets() {
        String screen_name = this.user.getScreenName();
        Log.d("DEBUG", String.format("populateTimeline (user: %s) with maxId: %d",
                screen_name, this.curMaxId));
        this.client.getUserTimeline(screen_name, this.curMaxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", String.format("getUserTimeline success: %s", response.toString()));
                List<Tweet> tweetResults = Tweet.fromJsonArray(response, API_USER_TIMELINE);
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
        Log.d("DEBUG", String.format("Clearing user timeline tweets for %s",
                this.user.getScreenName()));
        SQLite.delete(Tweet.class)
                .where(Tweet_Table.source.is(API_USER_TIMELINE))
                .and(Tweet_Table.user_uid.is(this.user.getUid()))
                .async()
                .execute();
        super.clearTweets();
    }

    @Override
    public void insertTopTweet(Tweet tweet) {
        // do nothing
    }
}
