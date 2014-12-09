package com.tri.felipe.safeback.Controller;

import android.content.Context;

import com.tri.felipe.safeback.Model.Skeleton;
import java.util.ArrayList;

/**
 * Created by Felipe on 14-11-23.
 */
public class SkeletonController {
    private ArrayList<Skeleton> mSkeletons;
    private static SkeletonController sSkeletonController;

    private SkeletonController(Context appContext) {
        mSkeletons = new ArrayList<Skeleton>();
    }

    public static SkeletonController get(Context c) {
        if (sSkeletonController == null) {
            sSkeletonController = new SkeletonController(c.getApplicationContext());
        }
        return sSkeletonController;
    }

    public SkeletonController(){
        this.mSkeletons = new ArrayList<Skeleton>();
    }

    public ArrayList<Skeleton> getSkeletons(){
        return mSkeletons;
    }

    public int CmToInch(int value){
        return Math.round(value * 0.393701f);
    }

    public int InchToCm(int value){
        return Math.round(value * 2.54f);
    }

    public int KiloToPound(int value){
        return Math.round(value * 2.20462f);
    }

    public int PoundToKilo(int value) {
        return Math.round(value * 0.453592f);
    }
}
