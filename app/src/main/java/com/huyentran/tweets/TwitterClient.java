package com.huyentran.tweets;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "30EutN8mDo4EE4ztMKtvjKFwf";
    public static final String REST_CONSUMER_SECRET = "FLBYJFQwGtxSssJWSWoQkWynjuxZOvl1vnNH6e3pnXxNQs2uBQ";
    public static final String REST_CALLBACK_URL = "oauth://cptweetsbyht";

    private static final String API_VERIFY_CREDS = "account/verify_credentials.json";

    private static final String API_HOME_TIMELINE = "statuses/home_timeline.json";
    private static final int DEFAULT_COUNT = 25;
    private static final int DEFAULT_SINCE_ID = 1;

    private static final String API_MENTIONS_TIMELINE = "statuses/mentions_timeline.json";

    private static final String API_COMPOSE = "statuses/update.json";

    private TwitterClientListener listener;

    public interface TwitterClientListener {
        void onInternetConnected();
        void onInternetDisconnected();
    }

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void registerListener(TwitterClientListener listener) {
        this.listener = listener;
    }

    public void getAuthenticatedUser(AsyncHttpResponseHandler handler) {
        if (!isOnline()) {
            this.listener.onInternetDisconnected();
        } else {
            this.listener.onInternetConnected();
            String apiUrl = getApiUrl(API_VERIFY_CREDS);
            RequestParams params = new RequestParams();
            params.put("include_entities", false);
            params.put("skip_status", true);
            params.put("include_email", false);
            this.client.get(apiUrl, params, handler);
        }
    }

    public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
        if (!isOnline()) {
            this.listener.onInternetDisconnected();
        } else {
            this.listener.onInternetConnected();
            String apiUrl = getApiUrl(API_HOME_TIMELINE);
            RequestParams params = new RequestParams();
            params.put("count", DEFAULT_COUNT);
            params.put("since_id", DEFAULT_SINCE_ID);
            if (maxId > 0) {
                params.put("max_id", maxId);
            }
            this.client.get(apiUrl, params, handler);
        }
    }

    public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
        if (!isOnline()) {
            this.listener.onInternetDisconnected();
        } else {
            this.listener.onInternetConnected();
            String apiUrl = getApiUrl(API_MENTIONS_TIMELINE);
            RequestParams params = new RequestParams();
            params.put("count", DEFAULT_COUNT);
            if (maxId > 0) {
                params.put("max_id", maxId);
            }
            this.client.get(apiUrl, params, handler);
        }
    }

    public void postUpdate(String body, AsyncHttpResponseHandler handler) {
        if (!isOnline()) {
            this.listener.onInternetDisconnected();
        } else {
            this.listener.onInternetConnected();
            String apiUrl = getApiUrl(API_COMPOSE);
            RequestParams params = new RequestParams();
            params.put("status", body);
            this.client.post(apiUrl, params, handler);
        }
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            if (exitValue == 0) {
                // online
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // offline
        return false;
    }

}