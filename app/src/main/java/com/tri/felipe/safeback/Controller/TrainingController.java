package com.tri.felipe.safeback.Controller;

import android.content.Context;

import com.tri.felipe.safeback.Model.Training;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Felipe on 14-10-20.
 */
public class TrainingController {

    private ArrayList<Training> mTraining;
    private static TrainingController sTrainingController;
    private Context mAppContext;

    private TrainingController(Context appContext) {
        mAppContext = appContext;
        mTraining = new ArrayList<Training>();

    }

    public static TrainingController get(Context c) {
        if (sTrainingController == null) {
            sTrainingController = new TrainingController(c.getApplicationContext());
        }
        return sTrainingController;
    }

    public ArrayList<Training> getTrainings() {
        return mTraining;
    }

    public Training getTraining(UUID id) {
        for (Training c : mTraining) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }
    public void AddTraining(Training training){
        mTraining.add(training);
    }
}
