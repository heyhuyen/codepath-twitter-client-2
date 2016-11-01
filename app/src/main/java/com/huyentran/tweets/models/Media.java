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
 * Media entity model.
 */
@Table(database = MyDatabase.class)
@Parcel(analyze={Media.class})
public class Media extends BaseModel {

    @Column
    @PrimaryKey
    long uid;

    @Column
    String type;

    @Column
    String mediaUrl;

    @Column
    String url;

    public Media() {
        // empty constructor for Parceler
    }

    public static Media fromJson(JSONObject json) {
        Media media = new Media();
        try {
            media.uid = json.getLong("id");
            media.type = json.getString("type");
            media.mediaUrl = json.getString("media_url");
            media.url = json.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }

    public long getUid() {
        return this.uid;
    }

    public String getType() {
        return this.type;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
