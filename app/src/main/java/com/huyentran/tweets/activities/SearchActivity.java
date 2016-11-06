package com.huyentran.tweets.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.huyentran.tweets.R;
import com.huyentran.tweets.adapters.TweetsArrayAdapter;
import com.huyentran.tweets.fragment.SearchListFragment;
import com.huyentran.tweets.fragment.TweetsListFragment;
import com.huyentran.tweets.models.Tweet;

import org.parceler.Parcels;

/**
 * Activity for searching tweets.
 */
public class SearchActivity extends AppCompatActivity
        implements TweetsListFragment.TweetClickListener, TweetsArrayAdapter.TweetClickListener {

    private SearchListFragment resultsFragment;
    private SearchView searchView;
    private String providedQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // if query provided, fetch & populate results
            this.providedQuery = getIntent().getStringExtra("query");
            this.resultsFragment = new SearchListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, this.resultsFragment);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // hookup icon_search view in action bar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        this.searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                resultsFragment.search(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.requestFocus();
        // prepopulate and search if query was provided
        if (this.providedQuery != null && !this.providedQuery.isEmpty()) {
            searchView.setQuery(this.providedQuery, true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches {@link TweetDetailActivity} for the given tweet.
     */
    @Override
    public void tweetOnClick(Tweet tweet) {
        Log.d("DEBUG", "Launch TweetDetailActivity");
        Intent intent = new Intent(this, TweetDetailActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }

    /**
     * Launches {@link ProfileActivity} for the user with the given screen name.
     */
    @Override
    public void profileOnClick(String screenName) {
        Log.d("DEBUG", String.format("Launching Profile Activity for user: @%s", screenName));
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("screen_name", screenName);
        startActivity(intent);
    }

    /**
     * Launches {@link SearchActivity} with the given hashtag query
     */
    @Override
    public void hashtagOnClick(String hashtag) {
        Log.d("DEBUG", String.format("Launching Search Activity for: %s", hashtag));
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("query", hashtag);
        startActivity(intent);
    }
}
