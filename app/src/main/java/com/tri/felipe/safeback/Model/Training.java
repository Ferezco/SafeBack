package com.tri.felipe.safeback.Model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Felipe on 2014-10-16.
 */
public class Training extends Story implements Comparable<Object>{

    protected int mCategory;

    public Training(String title, int category, String description, Date date){
        this.mId = UUID.randomUUID();
        this.mTitle = title;
        this.mCategory = category;
        this.mDescription = description;
        this.mDate = date;
    }

    public int getType() {
        return mCategory;
    }

    public void setType(int mType) {
        // 0 = Graphic, 1 = Video, 2 = Text
        this.mCategory = mType;
    }

    @Override
    public int compareTo(Object obj) {
        if(((Training) obj).getDate().equals(this.getDate()))
            return 0;
        else if (((Training)obj).getDate().after(this.getDate()))
            return 1;
        else
            return -11;
    }
}




