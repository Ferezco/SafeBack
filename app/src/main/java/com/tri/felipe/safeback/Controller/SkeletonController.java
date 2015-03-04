package com.tri.felipe.safeback.Controller;

import android.content.Context;
import android.util.Log;

import com.tri.felipe.safeback.Model.Skeleton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-23.
 */
public class SkeletonController {
    private static SkeletonController sSkeletonController;
    private static String SAVEFILE = "skeleton_records.ser";
    private ArrayList<Skeleton> mSkeletons;
    private Context appContext;

    private SkeletonController() {
        mSkeletons = new ArrayList<>();
    }

    public static SkeletonController get(Context c) {
        if (sSkeletonController == null) {
            sSkeletonController = new SkeletonController();
        }
        sSkeletonController.appContext = c;
        return sSkeletonController;
    }

    public ArrayList<Skeleton> getSkeletons() {
        return mSkeletons;
    }

    public int CmToInch(int value) {
        return Math.round(value * 0.393701f);
    }

    public int InchToCm(int value) {
        return Math.round(value * 2.54f);
    }

    public int KiloToPound(int value) {
        return Math.round(value * 2.20462f);
    }

    public int PoundToKilo(int value) {
        return Math.round(value * 0.453592f);
    }

    /**
     * Deletes previously written storage files, then recreates and writes
     * to them all of the current saved Skeletons
     */
    public void saveAllSkeletons() {
        appContext.deleteFile(SAVEFILE);
        try {
            FileOutputStream skeleton_records = appContext.openFileOutput(SAVEFILE, appContext.MODE_PRIVATE);
            OutputStream buffer = new BufferedOutputStream(skeleton_records);
            ObjectOutput output = new ObjectOutputStream(buffer);

            int count = 0;
            for (Skeleton s : mSkeletons) {
                output.writeObject(s);
                count++;
            }

            output.close();
            buffer.close();
            skeleton_records.close();
            Log.d("Writing to File", Integer.toString(count));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logSkeletonStatus(String source, Skeleton mSkeleton) {
        Log.d(source, String.format("Neck %d %d %d, Shoulder %d %d %d %d, Trunk %d %d %d, Elbow %d %d, User %d %d %d",
                mSkeleton.getJoints().get(0).get(0).getAngle(),
                mSkeleton.getJoints().get(0).get(1).getAngle(),
                mSkeleton.getJoints().get(0).get(2).getAngle(),
                mSkeleton.getJoints().get(1).get(0).getAngle(),
                mSkeleton.getJoints().get(1).get(1).getAngle(),
                mSkeleton.getJoints().get(1).get(2).getAngle(),
                mSkeleton.getJoints().get(1).get(3).getAngle(),
                mSkeleton.getJoints().get(2).get(0).getAngle(),
                mSkeleton.getJoints().get(2).get(1).getAngle(),
                mSkeleton.getJoints().get(2).get(2).getAngle(),
                mSkeleton.getJoints().get(3).get(0).getAngle(),
                mSkeleton.getJoints().get(3).get(1).getAngle(),
                mSkeleton.getWeight(), mSkeleton.getHeight(),
                mSkeleton.getBoxWeight()));
    }

    public void loadAllSkeletons() throws IOException {
        try {
            InputStream skeleton_records = appContext.openFileInput(SAVEFILE);
            InputStream buffer = new BufferedInputStream(skeleton_records);
            ObjectInput input = new ObjectInputStream(buffer);

            for (; ; ) {
                mSkeletons.add((Skeleton) input.readObject());
            }
        } catch (FileNotFoundException e) {
            Log.d("Exception", "FileNotFound");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Skeleton getSkeletonByID(UUID id) {
        for (Skeleton s : mSkeletons) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }
}
