package com.tri.felipe.safeback.Controller;

import android.content.Context;

import com.tri.felipe.safeback.Model.Skeleton;
import com.tri.felipe.safeback.Model.Training;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-23.
 */
public class SkeletonController {

    private ArrayList<Skeleton> mSkeletons;
    private static SkeletonController sSkeletonController;
    private Context mAppContext;

    private SkeletonController(Context appContext) {
        mAppContext = appContext;
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

    public Skeleton getSkeleton(UUID id) {
        for (Skeleton s : mSkeletons) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
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
