package com.huyentran.tweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.huyentran.tweets.TwitterApplication;
import com.huyentran.tweets.databinding.ActivityTimelineBinding;
import com.huyentran.tweets.R;
import com.huyentran.tweets.TwitterClient;
import com.huyentran.tweets.fragment.HomeTimelineFragment;
import com.huyentran.tweets.fragment.MentionsTimelineFragment;
import com.huyentran.tweets.fragment.dialog.ComposeDialogFragment;
import com.huyentran.tweets.fragment.TweetsListFragment;
import com.huyentran.tweets.models.Tweet;
import com.huyentran.tweets.models.TweetDraft;
import com.huyentran.tweets.models.User;
import com.huyentran.tweets.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity
        implements ComposeDialogFragment.ComposeFragmentListener, TwitterClient.TwitterClientListener {

    private ActivityTimelineBinding binding;
    private CoordinatorLayout clActivity;
    private Snackbar snackbar;
    private FloatingActionButton fabCompose;

    private ViewPager viewPager;
    private TweetsPagerAdapter vpAdapter;
    private HomeTimelineFragment homeFragment;
    private MentionsTimelineFragment mentionsFragment;

    private TwitterClient client;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        Toolbar toolbar = this.binding.toolbar;
        setSupportActionBar(toolbar);

        setupViews();

        this.client = TwitterApplication.getRestClient();
        this.client.registerListener(TimelineActivity.this);
        getAuthenticatedUser(); // needed for composing tweets

        if (savedInstanceState == null) {
            this.homeFragment = HomeTimelineFragment.newInstance();
            this.mentionsFragment = MentionsTimelineFragment.newInstance();
        }
    }

    private void setupViews() {
        setupTabs();
        this.clActivity = this.binding.activityTimeline;

        // internet snackbar
        this.snackbar = Snackbar.make(this.clActivity, R.string.error_internet,
                Snackbar.LENGTH_INDEFINITE);

        // compose floating action button
        this.fabCompose = this.binding.fabCompose;
        this.fabCompose.setEnabled(false);
        this.fabCompose.setOnClickListener(v -> launchCompose());
    }

    private void setupTabs() {
        this.viewPager = this.binding.viewpager;
        this.vpAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(this.vpAdapter);
        PagerSlidingTabStrip tabStrip = this.binding.tabs;
        tabStrip.setViewPager(viewPager);
    }

    private void getAuthenticatedUser() {
        this.client.getAuthenticatedUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG",
                        String.format("getAuthenticatedUser success: %s", response.toString()));
                user = User.fromJson(response);
                mentionsFragment.setUser(user);
                fabCompose.setEnabled(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                if (!snackbar.isShownOrQueued()) {
                    // TODO: retry
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.miCompose) {
//            launchCompose();
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches {@link ComposeDialogFragment} modal overlay.
     */
    private void launchCompose() {
        // fetch drafts
        List<TweetDraft> drafts = SQLite.select().from(TweetDraft.class).queryList();
        Log.d("DEBUG", String.format("Loaded %d drafts from db", drafts.size()));
        ComposeDialogFragment composeDialogFragment =
                ComposeDialogFragment.newInstance(this.user, drafts);
        composeDialogFragment.setStyle(
                DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        composeDialogFragment.show(getSupportFragmentManager(), "composeDialogFragment");
    }

    @Override
    public void onComposeSuccess(Tweet tweet) {
        HomeTimelineFragment homeFragment = (HomeTimelineFragment) this.vpAdapter.getItem(HOME);
        homeFragment.insertTopTweet(tweet);

        MentionsTimelineFragment mentionsFragment =
                (MentionsTimelineFragment) this.vpAdapter.getItem(MENTIONS);
        mentionsFragment.insertTopTweet(tweet);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.DRAFT_REQUEST_CODE) {
            TweetDraft selectedDraft = Parcels.unwrap(data.getParcelableExtra("draft"));
            Log.d("DEBUG", String.format("Received draft [%d]: %s",
                    selectedDraft.getId(), selectedDraft.getBody()));
            ComposeDialogFragment fragment = (ComposeDialogFragment) getSupportFragmentManager()
                    .findFragmentByTag("composeDialogFragment");
            fragment.loadDraft(selectedDraft);
        }
    }

    @Override
    public void onInternetConnected() {
        this.snackbar.dismiss();
    }

    @Override
    public void onInternetDisconnected() {
        this.snackbar.show();
        TweetsListFragment currentFragment =
                (TweetsListFragment) this.vpAdapter.getItem(this.viewPager.getCurrentItem());
        currentFragment.stopSwipeRefreshing();
    }

    private static final int HOME = 0;
    private static final int MENTIONS = 1;

    /**
     * Returns the order of the fragments in the viewpager.
     */
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = { "Home", "Mentions" };

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == HOME) {
                return homeFragment;
            } else if (position == MENTIONS) {
                return mentionsFragment;
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.tabTitles[position];
        }

        @Override
        public int getCount() {
            return this.tabTitles.length;
        }
    }
}
