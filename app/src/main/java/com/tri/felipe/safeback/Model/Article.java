package com.tri.felipe.safeback.Model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-17.
 */
public class Article extends Story {
    private String mUrl;

    public Article(String title, String description, String url, Date date){
        mTitle = title;
        mDescription = description;
        mDate = date;
        mUrl = url;
        mId = UUID.randomUUID();
    }

    public String getUrl() {
        return mUrl;
    }
}
