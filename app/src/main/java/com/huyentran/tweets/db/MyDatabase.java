package com.huyentran.tweets.db;

import android.util.Log;

import com.huyentran.tweets.models.Tweet;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

import java.util.List;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {

    public static final String NAME = "TwitterClientDatabase";

    public static final int VERSION = 3;

    /**
     * Persists tweets locally to SQlite DB.
     */
    public static void persistTweets(List<Tweet> tweets) {
        Log.d("DEBUG", String.format("Trying to persist %d tweets", tweets.size()));
        FlowManager.getDatabase(MyDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Tweet>() {
                            @Override
                            public void processModel(Tweet tweet) {
                                tweet.getUser().save();
                                if (tweet.getMedia() != null) {
                                    tweet.getMedia().save();
                                }
                                tweet.save();
                            }
                        })
                        .addAll(tweets).build())
                .error((transaction, error) -> {
                    Log.d("DEBUG", String.format("Error persisting %d tweets", tweets.size()));
                })
                .success(transaction -> {
                    Log.d("DEBUG", String.format("Persisted %d tweets", tweets.size()));
                })
                .build().execute();
    }
}
