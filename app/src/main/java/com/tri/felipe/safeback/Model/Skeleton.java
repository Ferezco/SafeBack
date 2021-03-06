package com.tri.felipe.safeback.Model;

import com.threed.jpct.Matrix;
import com.threed.jpct.SimpleVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.SkeletonPose;

/**
 * Created by Felipe on 14-11-18.
 */
public class Skeleton implements Serializable {
    public static final int HAND_L_JOINT = 29;
    public static final int HAND_R_JOINT = 30;
    public static final float UPPERARM_SEGMENT_WEIGHT = 2.71f / 100.f;
    public static final float UPPERARM_MASS_CENTER = 1 - 55.02f / 100.f;
    public static final float FOREARM_SEGMENT_WEIGHT = 1.62f / 100.f;//0.022f;
    public static final float FOREARM_MASS_CENTER = 1 - 57.26f / 100f;// 0.392f;;
    public static final float HEAD_SEGMENT_WEIGHT = 6.94f / 100f;
    public static final float HEAD_MASS_CENTER = 1 - 50.02f / 100f;//1.0f
    public static final float UPPER_TRUNK_SEGMENT_WEIGHT = 15.96f / 100f;//0.497f;
    public static final float UPPER_TRUNK_MASS_CENTER = 1 - 50.66f / 100f;//0.50f;
    public static final float HAND_SEGMENT_WEIGHT = 0.006f;
    private static final SimpleVector X_POS_PLANE = new SimpleVector(1, 0, 0);
    private static final SimpleVector Y_POS_PLANE = new SimpleVector(0, 1, 0);
    private static final SimpleVector Y_NEG_PLANE = new SimpleVector(0, -1, 0);
    private static final SimpleVector Z_POS_PLANE = new SimpleVector(0, 0, 1);
    private static final SimpleVector Z_NEG_PLANE = new SimpleVector(0, 0, -1);
    private static final SimpleVector X_NEG_Z_POS_PLANE = new SimpleVector(-1, 0, 1);
    private static final SimpleVector X_NEG_Z_NEG_PLANE = new SimpleVector(-1, 0, -1);
    //Correspond to the number on the joints of the skeleton
    private static final int PELVIS_JOINT = 2;
    private static final int TRUNK_JOINT = 3;
    private static final int CHEST_JOINT = 6;
    private static final int NECK_JOINT = 17;
    private static final int SHOULDER_L_JOINT = 20;
    private static final int SHOULDER_R_JOINT = 21;
    private static final int TOP_OF_HEAD = 22;
    private static final int ELBOW_R_JOINT = 26;
    private static final int ELBOW_L_JOINT = 25;
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mDescription;
    private int mWeight;
    private int mHeight;
    private int mBox;
    private HashMap<Integer, ArrayList<JointAngle>> mJoints;


    public Skeleton(){
        this.mId = UUID.randomUUID();
        this.mTitle = "";
        /* mJoints holds all of the information regarding a skeleton
         * each arraylist within the map corresponds to a joint in the skeleton
         */
        this.mJoints = new LinkedHashMap<>();

        final Matrix neckRotation = new Matrix();
        ArrayList<JointAngle> neck = new ArrayList<>();
        neck.add(new JointAngle(NECK_JOINT, 0, -40, 40, X_POS_PLANE, X_POS_PLANE, neckRotation));
        neck.add(new JointAngle(NECK_JOINT, 0, -20, 20, Z_POS_PLANE, Z_POS_PLANE, neckRotation));
        neck.add(new JointAngle(NECK_JOINT, 0, -10, 30, Y_NEG_PLANE, Y_NEG_PLANE, neckRotation));
        this.mJoints.put(0, neck);

        final Matrix leftShoulderRotation = new Matrix();
        final Matrix rightShoulderRotation = new Matrix();
        ArrayList<JointAngle> shoulder = new ArrayList<>();
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, 10, -10, 135, Z_NEG_PLANE, X_NEG_Z_NEG_PLANE, leftShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, 10, -10, 135, Z_POS_PLANE, X_NEG_Z_POS_PLANE, rightShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, 0, -20, 135, Y_POS_PLANE, Y_POS_PLANE, leftShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, 0, -20, 135, Y_POS_PLANE, Y_POS_PLANE, rightShoulderRotation));
        this.mJoints.put(1, shoulder);

        final Matrix trunkRotation = new Matrix();
        ArrayList<JointAngle> trunk = new ArrayList<>();
        trunk.add(new JointAngle(TRUNK_JOINT, 0, -90, 90, X_POS_PLANE, X_POS_PLANE, trunkRotation));
        trunk.add(new JointAngle(TRUNK_JOINT, 0, -90, 90, Z_POS_PLANE, Z_POS_PLANE, trunkRotation));
        trunk.add(new JointAngle(TRUNK_JOINT, 0, -15, 105, Y_NEG_PLANE, Y_NEG_PLANE, trunkRotation));
        this.mJoints.put(2, trunk);

        ArrayList<JointAngle> elbow = new ArrayList<>();
        elbow.add(new JointAngle(ELBOW_L_JOINT, 0, 0, 120, Y_POS_PLANE, Y_POS_PLANE, new Matrix()));
        elbow.add(new JointAngle(ELBOW_R_JOINT, 0, 0, 120, Y_POS_PLANE, Y_POS_PLANE, new Matrix()));
        this.mJoints.put(3, elbow);

        this.mWeight = 0;
        this.mHeight = 0;
        this.mBox = 0;
        this.mDescription = "";
        this.mDate = new Date();
    }

    public Skeleton(Matrix mNeck, Matrix mLeftShoulder, Matrix mRightShoulder, Matrix mTrunk,
                    Matrix mLeftElbow, Matrix mRightElbow, Integer[] neckAngles,
                    Integer[] shoulderAngles, Integer[] trunkAngles, Integer[] elbowAngles,
                    int weight, int height, int box, String title, String description, Date date){
        this.mId = UUID.randomUUID();
        this.mTitle = title;
        this.mDescription = description;
        this.mDate = date;
        this.mWeight = weight;
        this.mHeight = height;
        this.mBox = box;

        /* mJoints holds all of the information regarding a skeleton
         * each arraylist within the map corresponds to a joint in the skeleton
         */
        this.mJoints = new LinkedHashMap<>();
        ArrayList<JointAngle> neck = new ArrayList<>();
        neck.add(new JointAngle(NECK_JOINT, neckAngles[0],
                -40, 40, X_POS_PLANE, X_POS_PLANE, mNeck));
        neck.add(new JointAngle(NECK_JOINT, neckAngles[1],
                -20, 20, Z_POS_PLANE, Z_POS_PLANE, mNeck));
        neck.add(new JointAngle(NECK_JOINT, neckAngles[2],
                -10, 30, Y_NEG_PLANE, Y_NEG_PLANE, mNeck));
        this.mJoints.put(0, neck);

        ArrayList<JointAngle> shoulder = new ArrayList<>();
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, shoulderAngles[0],
                -10, 135, Z_NEG_PLANE, X_NEG_Z_NEG_PLANE, mLeftShoulder));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, shoulderAngles[1],
                -10, 135, Z_POS_PLANE, X_NEG_Z_POS_PLANE, mRightShoulder));
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, shoulderAngles[2],
                -20, 135, Y_POS_PLANE, Y_POS_PLANE, mLeftShoulder));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, shoulderAngles[3],
                -20, 135, Y_POS_PLANE, Y_POS_PLANE, mRightShoulder));
        this.mJoints.put(1, shoulder);

        ArrayList<JointAngle> trunk = new ArrayList<>();
        trunk.add(new JointAngle(TRUNK_JOINT, trunkAngles[0],
                -90, 90, X_POS_PLANE, X_POS_PLANE, mTrunk));
        trunk.add(new JointAngle(TRUNK_JOINT, trunkAngles[1],
                -90, 90, Z_POS_PLANE, Z_POS_PLANE, mTrunk));
        trunk.add(new JointAngle(TRUNK_JOINT, trunkAngles[2],
                -15, 105, Y_NEG_PLANE, Y_NEG_PLANE, mTrunk));
        this.mJoints.put(2, trunk);

        ArrayList<JointAngle> elbow = new ArrayList<>();
        elbow.add(new JointAngle(ELBOW_L_JOINT, elbowAngles[0],
                0, 120, Y_POS_PLANE, Y_POS_PLANE, mLeftElbow));
        elbow.add(new JointAngle(ELBOW_R_JOINT, elbowAngles[1],
                0, 120, Y_POS_PLANE, Y_POS_PLANE, mRightElbow));
        this.mJoints.put(3, elbow);
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public HashMap<Integer, ArrayList<JointAngle>> getJoints() {
        return mJoints;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public int getUserStat(int index) {
        if (index == 0)
            return mWeight;
        else if (index == 1)
            return mHeight;
        else
            return mBox;
    }

    public void setUserStat(int value, int index) {
        if(index == 0)
            this.mWeight = value;
        else if (index == 1)
            this.mHeight = value;
        else
            this.mBox = value;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getWeight(){
        return mWeight;
    }

    public int getHeight(){
        return mHeight;
    }

    public int getBoxWeight(){
        return mBox;
    }


    public Skeleton copy(){
        ArrayList<JointAngle> neck, shoulder, trunk, elbow;
        Integer[] neckAngles, shoulderAngles, trunkAngles, elbowAngles;
        Matrix mneck, mleftshoulder, mrightshoulder, mtrunk, mleftelbow, mrightelbow;

        neck = this.mJoints.get(0);
        mneck = neck.get(0).getRotationMatrix();
        neckAngles = getAngles(neck);

        shoulder = this.mJoints.get(1);
        mleftshoulder = shoulder.get(0).getRotationMatrix();
        mrightshoulder = shoulder.get(1).getRotationMatrix();
        shoulderAngles = getAngles(shoulder);

        trunk = this.mJoints.get(2);
        mtrunk = trunk.get(0).getRotationMatrix();
        trunkAngles = getAngles(trunk);

        elbow = this.mJoints.get(3);
        mleftelbow = elbow.get(0).getRotationMatrix();
        mrightelbow = elbow.get(1).getRotationMatrix();
        elbowAngles = getAngles(elbow);

        return new Skeleton(mneck, mleftshoulder, mrightshoulder, mtrunk, mleftelbow, mrightelbow,
                neckAngles, shoulderAngles, trunkAngles, elbowAngles, this.mWeight, this.mHeight,
                this.mBox, this.mTitle, this.mDescription, this.mDate);
    }

    public Integer[] getAngles(ArrayList<JointAngle> joint) {
        Integer[] angles = new Integer[4];
        int i = 0;
        for (JointAngle j : joint) {
            angles[i] = j.getAngle();
            i++;
        }
        return angles;
    }

    private float[] calculateMoment(SimpleVector parentTranslation, SimpleVector childTranslation, SimpleVector pelvisTranslation, float userWeight, float centerOfMassConstant, float segmentWeightConstant) {
        float centerX = parentTranslation.x - centerOfMassConstant * (parentTranslation.x - childTranslation.x);
        float centerY = parentTranslation.y - centerOfMassConstant * (parentTranslation.y - childTranslation.y);
        float centerZ = parentTranslation.z - centerOfMassConstant * (parentTranslation.z - childTranslation.z);
        SimpleVector massCenter = new SimpleVector(centerX, centerY, centerZ);

        //Log.d("translation","massCenter   " + massCenter + "      parentJoint   " + parentTranslation);

        float distanceX = -(massCenter.x - pelvisTranslation.x);
        float force = (userWeight * segmentWeightConstant) * 9.8f;
        float momentB = force * distanceX;

        float distanceZ = -(massCenter.z - pelvisTranslation.z);
        float momentF = force * distanceZ;

        //Log.d("moments","   force  "+force+" distanceZ  "+distanceZ);

        float[] moments = new float[2];
        moments[0] = momentF;
        moments[1] = momentB;
        return moments;
    }

    private float[] calculateBoxMoment(SimpleVector handTranslation, SimpleVector pelvisTranslation, float userWeight, float boxWeight) {
        float distanceX = -handTranslation.x + pelvisTranslation.x;
        float force = (float) ((userWeight * HAND_SEGMENT_WEIGHT + 0.5 * boxWeight) * 9.8f);
        float momentB = force * distanceX;
        float distanceZ = -handTranslation.z + pelvisTranslation.z;
        float momentF = force * distanceZ;
        float[] moments = new float[2];
        moments[0] = momentF;
        moments[1] = momentB;
        //Log.d("distanceZ", handTranslation + "   "+distanceZ);

        return moments;
    }

    public float calculateForce(AnimatedGroup skel) {
        float userHeight = this.mHeight / 100;

        //convert to 1.8m skeleton
        float scale = userHeight/1.8f;

        SkeletonPose pose = skel.get(0).getSkeletonPose();

        SimpleVector pelvisTranslation = pose.getGlobal(PELVIS_JOINT).getTranslation();
        SimpleVector trunkTranslation = pose.getGlobal(TRUNK_JOINT).getTranslation();
        SimpleVector chestTranslation = pose.getGlobal(CHEST_JOINT).getTranslation();
        SimpleVector headTopTranslation = pose.getGlobal(TOP_OF_HEAD).getTranslation();
        SimpleVector shoulderLTranslation = pose.getGlobal(SHOULDER_L_JOINT).getTranslation();
        SimpleVector shoulderRTranslation = pose.getGlobal(SHOULDER_R_JOINT).getTranslation();
        SimpleVector elbowLTranslation = pose.getGlobal(ELBOW_L_JOINT).getTranslation();
        SimpleVector elbowRTranslation = pose.getGlobal(ELBOW_R_JOINT).getTranslation();
        SimpleVector handLTranslation = pose.getGlobal(HAND_L_JOINT).getTranslation();
        SimpleVector handRTranslation = pose.getGlobal(HAND_R_JOINT).getTranslation();
        SimpleVector neckTranslation = pose.getGlobal(NECK_JOINT).getTranslation();

        pelvisTranslation.scalarMul(scale);
        trunkTranslation.scalarMul(scale);
        chestTranslation.scalarMul(scale);
        headTopTranslation.scalarMul(scale);
        shoulderLTranslation.scalarMul(scale);
        shoulderRTranslation.scalarMul(scale);
        elbowLTranslation.scalarMul(scale);
        elbowRTranslation.scalarMul(scale);
        handLTranslation.scalarMul(scale);
        handRTranslation.scalarMul(scale);

        float[] upperTrunkMoment = calculateMoment(chestTranslation, neckTranslation, pelvisTranslation, mWeight, UPPER_TRUNK_MASS_CENTER, UPPER_TRUNK_SEGMENT_WEIGHT);
        float[] headMoment = calculateMoment(neckTranslation,headTopTranslation, pelvisTranslation, mWeight, HEAD_MASS_CENTER, HEAD_SEGMENT_WEIGHT);

        float[] upperarmLMoment = calculateMoment(shoulderLTranslation, elbowLTranslation, pelvisTranslation, mWeight, UPPERARM_MASS_CENTER, UPPERARM_SEGMENT_WEIGHT);
        float[] upperarmRMoment = calculateMoment(shoulderRTranslation, elbowRTranslation, pelvisTranslation, mWeight, UPPERARM_MASS_CENTER, UPPERARM_SEGMENT_WEIGHT);
        float[] forearmLMoment = calculateMoment(elbowLTranslation, handLTranslation, pelvisTranslation, mWeight, FOREARM_MASS_CENTER, FOREARM_SEGMENT_WEIGHT);
        float[] forearmRMoment = calculateMoment(elbowRTranslation, handRTranslation, pelvisTranslation, mWeight, FOREARM_MASS_CENTER, FOREARM_SEGMENT_WEIGHT);
        float[] boxLMoment = calculateBoxMoment(handLTranslation, pelvisTranslation, mWeight, mBox);
        float[] boxRMoment = calculateBoxMoment(handRTranslation, pelvisTranslation, mWeight, mBox);

        float flextionMoment = upperTrunkMoment[0]+upperTrunkMoment[0]+upperarmLMoment[0]+ upperarmRMoment[0]+ forearmLMoment[0] + forearmRMoment[0]+boxLMoment[0]+boxRMoment[0]+headMoment[0];

        return (float)(1067.6f + 1.219f * flextionMoment + 0.083f * Math.pow(flextionMoment, 2) - 0.0001f * Math.pow(flextionMoment, 3));
    }


}
