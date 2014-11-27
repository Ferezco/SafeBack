package com.tri.felipe.safeback.Model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-18.
 */
public class Skeleton {
    private UUID mId;
    private String mTitle;
    private int[][] mJoints;
    private Date mDate;
    private String mDescription;
    private int mWeight;
    private int mHeight;
    private int mBox;


    public Skeleton(int[][] joints, int weight, int height, int box, String title,
                    String description, Date date){
        this.mId = UUID.randomUUID();
        this.mTitle = title;
        this.mJoints = joints;
        this.mWeight = weight;
        this.mHeight = height;
        this.mBox = box;
        this.mDescription = description;
        this.mDate = date;
    }

    public Skeleton(){
        this.mId = UUID.randomUUID();
        this.mTitle = "";
        this.mJoints = new int[4][4];
        this.mWeight = 0;
        this.mHeight = 0;
        this.mBox = 0;
        this.mDescription = "";
        this.mDate = new Date();

    }

    public void setDate(Date date){
        this.mDate = date;
    }

    public Date getDate(){
        return mDate;
    }
    public int[][] getJoints() {
        return mJoints;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }
    public String getTitle(){
        return mTitle;
    }

    public void setJoints(int[][] mJoints) {
        this.mJoints = mJoints;
    }

    public void setDescription(String description){
        this.mDescription = description;
    }

    public String getDescription(){
        return mDescription;
    }

    public void setWeight(int weight){
        this.mWeight = weight;
    }

    public int getWeight(){
        return mWeight;
    }

    public void setHeight(int height){
        this.mHeight = height;
    }

    public int getHeight(){
        return mHeight;
    }

    public void setBoxWeight(int box){
        this.mBox = box;
    }

    public int getBoxWeight(){
        return mBox;
    }

    public UUID getId(){
        return mId;
    }
}
