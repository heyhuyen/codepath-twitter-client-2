package com.huyentran.tweets.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.huyentran.tweets.R;
import com.huyentran.tweets.adapters.TweetDraftAdapter;
import com.huyentran.tweets.databinding.ActivityDraftsBinding;
import com.huyentran.tweets.models.TweetDraft;
import com.huyentran.tweets.utils.DividerItemDecoration;
import com.huyentran.tweets.utils.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Activity for displaying a list of saved tweet drafts.
 */
public class DraftsActivity extends AppCompatActivity {

    private ActivityDraftsBinding binding;
    private ArrayList<TweetDraft> drafts;
    private TweetDraftAdapter draftsAdapter;
    private RecyclerView rvDrafts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_drafts);

        Toolbar toolbar = this.binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.drafts = Parcels.unwrap(getIntent().getParcelableExtra("drafts"));
        this.draftsAdapter = new TweetDraftAdapter(getContext(), this.drafts);
        this.rvDrafts = this.binding.rvDrafts;
        this.rvDrafts.setAdapter(this.draftsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        this.rvDrafts.setLayoutManager(layoutManager);
        ItemClickSupport.addTo(this.rvDrafts).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    TweetDraft draft = drafts.get(position);
                    Log.d("DEBUG", String.format("Draft selected: [%d] %s",
                            draft.getId(), draft.getBody()));
                    draftSelected(draft);
                }
        );
        ItemClickSupport.addTo(this.rvDrafts).setOnItemLongClickListener(
                (recyclerView, position, v) -> {
                    TweetDraft draft = drafts.get(position);
                    Log.d("DEBUG", String.format("Deleting draft [%d]: %s",
                            draft.getId(), draft.getBody()));
                    draft.delete();
                    drafts.remove(position);
                    draftsAdapter.notifyDataSetChanged();
                    return true;
                }
        );
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        this.rvDrafts.addItemDecoration(itemDecoration);
    }

    private void draftSelected(TweetDraft draft) {
        Intent intent = new Intent();
        intent.putExtra("draft", Parcels.wrap(draft));
        setResult(RESULT_OK, intent);
        finish();
    }
}
