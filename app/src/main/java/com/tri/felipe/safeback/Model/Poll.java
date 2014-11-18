package com.tri.felipe.safeback.Model;

/**
 * Created by Felipe on 14-11-17.
 */
public class Poll{
    private String mQuestion;
    private int mTrue;
    private int mFalse;

    public Poll(String question, int t1, int t2){
        this.mQuestion = question;
        this.mTrue = t1;
        this.mFalse = t2;
    }
}
