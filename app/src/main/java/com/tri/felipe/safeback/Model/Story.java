package com.tri.felipe.safeback.Model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-17.
 */
public class Story {

    protected UUID mId;
    protected String mTitle;
    protected Date mDate;
    protected String mDescription;

    public Story(){
        mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }


}
