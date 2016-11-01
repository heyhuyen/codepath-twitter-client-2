package com.huyentran.tweets.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huyentran.tweets.R;
import com.huyentran.tweets.TwitterApplication;
import com.huyentran.tweets.TwitterClient;
import com.huyentran.tweets.activities.DraftsActivity;
import com.huyentran.tweets.databinding.FragmentComposeBinding;
import com.huyentran.tweets.models.Tweet;
import com.huyentran.tweets.models.TweetDraft;
import com.huyentran.tweets.models.User;
import com.huyentran.tweets.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Modal overlay for composing tweets.
 */
public class ComposeFragment extends DialogFragment
        implements SaveDraftDialogFragment.DraftDialogListener {

    private static final int MAX_CHAR_COUNT = 140;

    private FragmentComposeBinding binding;
    private User user;
    private ArrayList<TweetDraft> drafts;
    private TweetDraft draft;

    private EditText etBody;
    private TextView tvCharCount;
    private ImageButton btnCancel;
    private Button btnTweet;
    private ImageButton btnDrafts;

    private ComposeFragmentListener listener;

    public interface ComposeFragmentListener {
        void onComposeSuccess(Tweet tweet);
    }

    public ComposeFragment() {
        // empty constructor
    }

    public static ComposeFragment newInstance(User user, List<TweetDraft> drafts) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        args.putParcelable("drafts", Parcels.wrap(drafts));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.binding = FragmentComposeBinding.bind(view);
        this.user = Parcels.unwrap(getArguments().getParcelable("user"));
        this.drafts = Parcels.unwrap(getArguments().getParcelable("drafts"));
        this.binding.setUser(this.user);
        this.binding.setHasDrafts(!this.drafts.isEmpty());
        this.binding.executePendingBindings();
        this.listener = (ComposeFragmentListener) getActivity();
        setupViews();
        return view;
    }

    /**
     * Wiring and setup of view and view-related components.
     */
    private void setupViews() {
        this.etBody = this.binding.etBody;
        this.tvCharCount = this.binding.tvCharCount;
        this.btnTweet = this.binding.btnTweet;
        this.btnCancel = this.binding.btnCancel;
        this.btnDrafts = this.binding.btnDrafts;

        this.etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                int charsLeft = MAX_CHAR_COUNT - s.length();
                tvCharCount.setText(String.valueOf(charsLeft));
                int colorRed, colorNormal;
                if (android.os.Build.VERSION.SDK_INT < 23) {
                    colorRed = getResources().getColor(android.R.color.holo_red_dark);
                    colorNormal = getResources().getColor(R.color.colorTextSecondary);
                } else {
                    colorRed = getResources().getColor(android.R.color.holo_red_dark, null);
                    colorNormal = getResources().getColor(R.color.colorTextSecondary, null);
                }
                if (charsLeft < 0) {
                    tvCharCount.setTextColor(colorRed);
                    btnTweet.setEnabled(false);
                } else {
                    tvCharCount.setTextColor(colorNormal);
                    btnTweet.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
            }
        });

        setupButtons();
    }

    private void setupButtons() {
        this.btnTweet.setOnClickListener(v -> {
            String tweetBody = etBody.getText().toString();
            postTweet(tweetBody);
        });

        this.btnCancel.setOnClickListener(v -> {
            String tweetBody = etBody.getText().toString();
            if (!TextUtils.isEmpty(tweetBody)) {
                showSaveDraftDialog();
            } else {
                dismiss();
            }
        });

        this.btnDrafts.setOnClickListener(v -> {
            launchDraftsActivity();
        });
    }

    private void postTweet(String tweetBody) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postUpdate(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "postTweet succeeded");
                listener.onComposeSuccess(Tweet.fromJson(response));
                if (draft != null) {
                    Log.d("DEBUG", String.format("Deleting tweeted draft [%d]: %s",
                            draft.getId(), draft.getBody()));
                    draft.delete();
                }
                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.d("DEBUG", String.format("postTweet failed: %s. Retrying...",
                        errorResponse.toString()));
                // TODO: retry
                //postTweet(tweetBody);
            }
        });
    }

    private void showSaveDraftDialog() {
        SaveDraftDialogFragment fragment =
                SaveDraftDialogFragment.newInstance();
        fragment.setTargetFragment(ComposeFragment.this, 5);
        fragment.show(getFragmentManager(), "saveDraftDialogFragment");
    }

    @Override
    public void onSave() {
        String tweetBody = this.etBody.getText().toString();
        if (this.draft != null) {
            this.draft.setBody(tweetBody);
            Log.d("DEBUG", String.format("Updating draft [%d]: %s",
                    this.draft.getId(), this.draft.getBody()));
            this.draft.update();
        } else {
            Log.d("DEBUG", String.format("Saving new draft: %s", tweetBody));
            this.draft = new TweetDraft(tweetBody, this.user);
            this.draft.save();
        }
        dismiss();
    }

    @Override
    public void onDelete() {
        if (this.draft != null) {
            Log.d("DEBUG", String.format("Deleting draft [%d]: %s",
                    this.draft.getId(), this.draft.getBody()));
            this.draft.delete();
        } else {
            Log.d("DEBUG", "Discarding tweet");
        }
        dismiss();
    }

    private void launchDraftsActivity() {
        Log.d("DEBUG", "Launch DraftsActivity");
        Intent intent = new Intent(getActivity(), DraftsActivity.class);
        intent.putExtra("drafts", Parcels.wrap(this.drafts));
        getActivity().startActivityForResult(intent, Constants.DRAFT_REQUEST_CODE);
    }

    public void loadDraft(TweetDraft tweetDraft) {
        Log.d("DEBUG", String.format("Load draft [%d]: %s",
                tweetDraft.getId(), tweetDraft.getBody()));
        this.draft = tweetDraft;
        this.etBody.setText(tweetDraft.getBody());
        this.etBody.setSelection(tweetDraft.getBody().length());
        this.btnDrafts.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
