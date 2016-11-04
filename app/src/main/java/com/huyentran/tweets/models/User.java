package com.huyentran.tweets.models;

import com.huyentran.tweets.db.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * User model.
 */
@Table(database = MyDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel {

    @Column
    @PrimaryKey
    long uid;

    @Column
    String name;

    @Column
    String screenName;

    @Column
    String profileImageUrl;

    @Column
    String tagline;

    @Column
    int followersCount;

    @Column
    int friendsCount;

    public User() {
        // empty constructor for Parceler
    }

    public static User fromJson(JSONObject json) {
        User user = new User();
        try {
            user.uid = json.getLong("id");
            user.name = json.getString("name");
//            String.format("@%s", json.getString("screen_name"));
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
            user.tagline = json.getString("description");
            user.followersCount = json.getInt("followers_count");
            user.friendsCount = json.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public long getUid() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public String getScreenName() {
        return this.screenName;
    }

    public String getProfileImageUrl() {
        return this.profileImageUrl;
    }

    public String getTagline() {
        return this.tagline;
    }

    public int getFollowersCount() {
        return this.followersCount;
    }

    public int getFriendsCount() {
        return this.friendsCount;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }
}
