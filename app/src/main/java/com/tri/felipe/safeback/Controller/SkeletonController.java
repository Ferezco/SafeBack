package com.tri.felipe.safeback.Controller;

import android.content.Context;
import android.util.Log;

import com.tri.felipe.safeback.Model.Skeleton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-23.
 */
public class SkeletonController {
    private ArrayList<Skeleton> mSkeletons;
    private static SkeletonController sSkeletonController;
    private Context appContext;
    private static String SAVEFILE = "skeleton_records.txt";

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

    /**
     * Deletes previously written storage files, then recreates and writes
     * to them all of the current saved Skeletons
     */
    public void saveAllSkeletons() {
        FileOutputStream skeleton_records;
        appContext.deleteFile(SAVEFILE);
        try {
            skeleton_records = appContext.openFileOutput(SAVEFILE, appContext.MODE_PRIVATE);
        OutputStreamWriter skeletons = new OutputStreamWriter(skeleton_records);

        for (Skeleton s: mSkeletons){
            //Format neck*3, Shoulder *4, Trunk *3, Elbow * 2, Title, Description, Time
            String data = String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%s,%s,%d\n",
                    s.getJoints().get(0).get(0).getAngle(),
                    s.getJoints().get(0).get(1).getAngle(),
                    s.getJoints().get(0).get(2).getAngle(),
                    s.getJoints().get(1).get(0).getAngle(),
                    s.getJoints().get(1).get(1).getAngle(),
                    s.getJoints().get(1).get(2).getAngle(),
                    s.getJoints().get(1).get(3).getAngle(),
                    s.getJoints().get(2).get(0).getAngle(),
                    s.getJoints().get(2).get(1).getAngle(),
                    s.getJoints().get(2).get(2).getAngle(),
                    s.getJoints().get(3).get(0).getAngle(),
                    s.getJoints().get(3).get(1).getAngle(), s.getWeight(), s.getHeight(),
                    s.getBoxWeight(), s.getTitle(), s.getDescription(), s.getDate().getTime() );
            Log.d("SkeletonController", data);
            skeletons.write(data);
        }
            skeletons.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllSkeletons() throws IOException{
        String[] reader;
        Scanner scanner;
        try {
            scanner = new Scanner(appContext.openFileInput(SAVEFILE));
            Log.d("SkeletonController", "completed reading from dynamic");
            while (scanner.hasNextLine()){
                reader = scanner.nextLine().split(",");
                Log.d("dynamic", reader[0]);
                this.createSkeleton(reader);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            Log.d("Exception", "FileNotFound");
        }
    }

    private void createSkeleton(String[] l) {
        int[] values = new int[15];
        for (int i = 0; i < 15; i++ ){
            values[i] = Integer.parseInt(l[0]);
        }
        Skeleton s = new Skeleton(values[0], values[1], values[2], values[3], values[4], values[5],
                values[6], values[7], values[8], values[9], values[10], values[11], values[12],
                values[13], values[14], l[15], l[16], new Date(Long.parseLong(l[17])));
        mSkeletons.add(s);
    }

    public Skeleton getSkeletonByID(UUID id){
        for(Skeleton s : mSkeletons){
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }
}
