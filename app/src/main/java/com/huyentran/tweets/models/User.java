package com.huyentran.tweets.models;

import com.huyentran.tweets.db.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static android.R.attr.id;

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

    public User() {
        // empty constructor for Parceler
    }

    public static User fromJson(JSONObject json) {
        User user = new User();
        try {
            user.uid = json.getLong("id");
            user.name = json.getString("name");
            user.screenName = String.format("@%s", json.getString("screen_name"));
            user.profileImageUrl = json.getString("profile_image_url");
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
}
