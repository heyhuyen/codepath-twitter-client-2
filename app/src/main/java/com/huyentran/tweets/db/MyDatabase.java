package com.huyentran.tweets.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {

    public static final String NAME = "TwitterClientDatabase";

    public static final int VERSION = 2;
}
