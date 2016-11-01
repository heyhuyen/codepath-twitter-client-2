package com.huyentran.tweets.models;

import com.huyentran.tweets.db.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Tweet draft model.
 */
@Table(database = MyDatabase.class)
@Parcel(analyze={TweetDraft.class})
public class TweetDraft extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    int id;

    @Column
    String body;

    @Column
    @ForeignKey
    User user;

    public TweetDraft() {
        // empty constructor for Parceler
    }

    public TweetDraft(String body, User user) {
        this.body = body;
        this.user = user;
    }

    public int getId() {
        return this.id;
    }

    public String getBody() {
        return this.body;
    }

    public User getUser() {
        return this.user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
