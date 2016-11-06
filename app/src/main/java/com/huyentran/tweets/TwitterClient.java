package com.huyentran.tweets;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

/**
 * OAuth client for interacting with the Twitter API.
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "30EutN8mDo4EE4ztMKtvjKFwf";
    public static final String REST_CONSUMER_SECRET = "FLBYJFQwGtxSssJWSWoQkWynjuxZOvl1vnNH6e3pnXxNQs2uBQ";
    public static final String REST_CALLBACK_URL = "oauth://cptweetsbyht";

    private static final String API_VERIFY_CREDS = "account/verify_credentials.json";
    public static final String API_USER_SHOW = "users/show.json";

    public static final String API_SEARCH_TWEETS = "search/tweets.json";
    public static final String API_HOME_TIMELINE = "statuses/home_timeline.json";
    public static final String API_MENTIONS_TIMELINE = "statuses/mentions_timeline.json";
    public static final String API_USER_TIMELINE = "statuses/user_timeline.json";
    private static final int DEFAULT_COUNT = 25;
    private static final int DEFAULT_SINCE_ID = 1;

    public static final String API_COMPOSE = "statuses/update.json";

    private TwitterClientListener listener;

    public interface TwitterClientListener {
        void onInternetConnected();
        void onInternetDisconnected();
    }

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    /**
     * Registers listener to notifiy of internet connectivity.
     *
     * @param listener the listener to register
     */
    public void registerListener(TwitterClientListener listener) {
        this.listener = listener;
    }

    /**
     * Queries the Twitter API for the authenticated user.
     *
     * @param handler the HTTP response handler
     */
    public void getAuthenticatedUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(API_VERIFY_CREDS);
        RequestParams params = new RequestParams();
        params.put("include_entities", false);
        params.put("skip_status", true);
        params.put("include_email", false);
        getRequest(apiUrl, params, handler);
    }

    /**
     * Queries the Twitter API for the user.
     *
     * @param screenName the user screen name to search for
     * @param handler the HTTP response handler
     */
    public void getUser(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(API_USER_SHOW);
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        getRequest(apiUrl, params, handler);
    }

    /**
     * Queries the Twitter API for tweets based on the given query string.
     *
     * @param maxId the max id threshold (if positive, fetch tweets whose ids do not exceed this max id)
     * @param handler the HTTP response handler
     */
    public void getTweets(long maxId, String query, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(API_SEARCH_TWEETS);
        RequestParams params = new RequestParams();
        params.put("q", query);
        params.put("count", DEFAULT_COUNT);
        params.put("since_id", DEFAULT_SINCE_ID);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        getRequest(apiUrl, params, handler);
    }

    /**
     * Queries the Twitter API for home timeline of tweets for the authenticated user.
     *
     * @param maxId the max id threshold (if positive, fetch tweets whose ids do not exceed this max id)
     * @param handler the HTTP response handler
     */
    public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(API_HOME_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_COUNT);
        params.put("since_id", DEFAULT_SINCE_ID);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        getRequest(apiUrl, params, handler);
    }

    /**
     * Queries the Twitter API for mentions timeline of tweets for the authenticated user.
     *
     * @param maxId the max id threshold (if positive, fetch tweets whose ids do not exceed this max id)
     * @param handler the HTTP response handler
     */
    public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(API_MENTIONS_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_COUNT);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        getRequest(apiUrl, params, handler);
    }

    /**
     * Queries the Twitter API for a user's timeline of tweets.
     *
     * @param screenName the screenname of the user to fetch timeline for
     * @param maxId the max id threshold (if positive, fetch tweets whose ids do not exceed this max id)
     * @param handler the HTTP response handler
     */
    public void getUserTimeline(String screenName, long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(API_USER_TIMELINE);
        RequestParams params = new RequestParams();
        params.put("count", DEFAULT_COUNT);
        params.put("screen_name", screenName);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        getRequest(apiUrl, params, handler);
    }

    /**
     * Posts a tweet to Twitter for the authenticated user.
     *
     * @param body the tweet body
     * @param handler the HTTP response handler
     */
    public void postUpdate(String body, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(API_COMPOSE);
        RequestParams params = new RequestParams();
        params.put("status", body);
        postRequest(apiUrl, params, handler);
    }

    /**
     * Checks for internet before dispatching API GET request.
     */
    private void getRequest(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        if (!isOnline()) {
            this.listener.onInternetDisconnected();
        } else {
            this.listener.onInternetConnected();
            this.client.get(url, params, handler);
        }
    }

    /**
     * Checks for internet before dispatching API POST request.
     */
    private void postRequest(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        if (!isOnline()) {
            this.listener.onInternetDisconnected();
        } else {
            this.listener.onInternetConnected();
            this.client.post(url, params, handler);
        }
    }

    /**
     * Checks for internet connection by pinging google.
     *
     * @return true if successful, false otherwise
     */
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