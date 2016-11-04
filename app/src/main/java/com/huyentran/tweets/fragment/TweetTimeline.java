package com.huyentran.tweets.fragment;

import com.huyentran.tweets.models.Tweet;

/**
 * Interface for Tweet timelines.
 */
public interface TweetTimeline {
    /**
     * Handles clearing persisted tweets.
     */
    void clearTweets();

    /**
     * Handles inserting a recently composed tweet at the top of the list.
     *
     * @param tweet the tweet to insert
     */
    void insertTopTweet(Tweet tweet);
}
