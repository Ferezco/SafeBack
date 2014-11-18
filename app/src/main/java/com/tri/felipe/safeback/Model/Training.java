package com.tri.felipe.safeback.Model;

import java.util.UUID;

/**
 * Created by Felipe on 2014-10-16.
 */
public class Training extends Story{

    protected int mType;

    public Training(){
        mId = UUID.randomUUID();
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        // 0 = Graphic, 1 = Video, 2 = Text
        this.mType = mType;
    }
}




