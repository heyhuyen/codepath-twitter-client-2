package com.huyentran.tweets.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyentran.tweets.R;
import com.huyentran.tweets.adapters.TweetsArrayAdapter;
import com.huyentran.tweets.databinding.FragmentTweetsListBinding;
import com.huyentran.tweets.models.Tweet;
import com.huyentran.tweets.utils.DividerItemDecoration;
import com.huyentran.tweets.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Fragment} for displaying a list of tweets.
 */
public class TweetsListFragment extends Fragment implements TweetTimeline {

    protected FragmentTweetsListBinding binding;
    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter tweetsAdapter;
    protected RecyclerView rvTweets;
    protected SwipeRefreshLayout swipeContainer;
    private TweetClickListener tweetClickListener;

    public interface TweetClickListener {
        void tweetOnClick(Tweet tweet);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.tweets = new ArrayList<>();
        this.tweetsAdapter = new TweetsArrayAdapter(getContext(), this.tweets);
        this.tweetClickListener = (TweetClickListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        this.binding = FragmentTweetsListBinding.bind(view);
        setupViews();
        return view;
    }

    private void setupViews() {
        // recycler view + adapter
        this.rvTweets = this.binding.rvTweets;
        this.rvTweets.setAdapter(this.tweetsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        this.rvTweets.setLayoutManager(layoutManager);
        ItemClickSupport.addTo(this.rvTweets).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Tweet tweet = tweets.get(position);
                    Log.d("DEBUG", String.format("Tweet selected: [%d] %s",
                            tweet.getUid(), tweet.getBody()));
                    tweetClickListener.tweetOnClick(tweet);
                }
        );
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        this.rvTweets.addItemDecoration(itemDecoration);

        // pull to refresh swipe container
        this.swipeContainer = this.binding.swipeContainer;
        this.swipeContainer.setColorSchemeResources(
                R.color.curiousBlue,
                R.color.black,
                R.color.darkGray);
    }

    /**
     * Appends the given tweets to the list of tweets and updates the adapter and curMaxId.
     */
    public long appendTweets(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        this.tweetsAdapter.notifyDataSetChanged();
        long newMaxId = tweets.get(tweets.size() - 1).getUid();
        Log.d("DEBUG",
                String.format("%d tweets appended. Total tweets: %d. New maxId: %d",
                        tweets.size(), this.tweets.size(), newMaxId));
        this.swipeContainer.setRefreshing(false);
        return newMaxId;
    }

    public void stopSwipeRefreshing() {
        if (this.swipeContainer.isRefreshing()) {
            this.swipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void clearTweets() {
        this.tweets.clear();
    }

    @Override
    public void insertTopTweet(Tweet tweet) {
        this.tweets.add(0, tweet);
        this.tweetsAdapter.notifyDataSetChanged();
    }
}
