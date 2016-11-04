package com.huyentran.tweets.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyentran.tweets.R;
import com.huyentran.tweets.databinding.FragmentProfileHeaderBinding;
import com.huyentran.tweets.models.User;

import org.parceler.Parcels;

/**
 * {@link TweetsListFragment} for displaying a user's profile header.
 */
public class UserHeaderFragment extends Fragment {

    protected FragmentProfileHeaderBinding binding;

    public static UserHeaderFragment newInstance(User user) {
        UserHeaderFragment fragment = new UserHeaderFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_header, parent, false);
        this.binding = FragmentProfileHeaderBinding.bind(view);
        this.binding.setUser(Parcels.unwrap(getArguments().getParcelable("user")));
        return view;
    }
}
