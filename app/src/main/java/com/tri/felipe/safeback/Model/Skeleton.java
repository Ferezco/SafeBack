package com.tri.felipe.safeback.Model;

import com.threed.jpct.Matrix;
import com.threed.jpct.SimpleVector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.SkeletonPose;

/**
 * Created by Felipe on 14-11-18.
 */
public class Skeleton {
    private String mTitle;
    private Date mDate;
    private String mDescription;
    private int mWeight;
    private int mHeight;
    private int mBox;
    private HashMap<Integer, ArrayList<JointAngle>> mJoints;

    private static final SimpleVector X_POSITIVE_PLANE = new SimpleVector(1, 0, 0);
    private static final SimpleVector Y_POSITIVE_PLANE = new SimpleVector(0, 1, 0);
    private static final SimpleVector Y_NEGATIVE_PLANE = new SimpleVector(0, -1, 0);
    private static final SimpleVector Z_POSITIVE_PLANE = new SimpleVector(0, 0, 1);
    private static final SimpleVector Z_NEGATIVE_PLANE = new SimpleVector(0, 0, -1);
    private static final SimpleVector X_NEGATIVE_Z_POSITIVE_PLANE = new SimpleVector(-1, 0, 1);
    private static final SimpleVector X_NEGATIVE_Z_NEGATIVE_PLANE = new SimpleVector(-1, 0, -1);

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
    public static final int HAND_L_JOINT = 29;
    public static final int HAND_R_JOINT = 30;

    public static final float UPPERARM_SEGMENT_WEIGHT = 2.71f/100.f;
    public static final float UPPERARM_MASS_CENTER = 1-55.02f/100.f;

    public static final float FOREARM_SEGMENT_WEIGHT = 1.62f/100.f;//0.022f;
    public static final float FOREARM_MASS_CENTER =1-57.26f/100f;// 0.392f;;

    public static final float HEAD_SEGMENT_WEIGHT = 6.94f/100f;
    public static final float HEAD_MASS_CENTER = 1-50.02f/100f;//1.0f

    public static final float UPPER_TRUNK_SEGMENT_WEIGHT = 15.96f/100f;//0.497f;
    public static final float UPPER_TRUNK_MASS_CENTER = 1-50.66f/100f;//0.50f;

    public static final float MID_TRUNK_SEGMENT_WEIGHT = 16.38f/100f;//0.497f;
    public static final float MID_TRUNK_MASS_CENTER = 1-45.02f/100f;//0.50f;

    public static final float HAND_SEGMENT_WEIGHT = 0.006f;
    public static final float HAND_MASS_CENTER = 1-63.09f/100f;


    public Skeleton(){
        this.mTitle = "";
        /* mJoints holds all of the information regarding a skeleton
         * each arraylist within the map corresponds to a joint in the skeleton
         */
        this.mJoints = new LinkedHashMap<>();

        final Matrix neckRotation = new Matrix();
        ArrayList<JointAngle> neck = new ArrayList<>();
        neck.add(new JointAngle(NECK_JOINT, 0, -40, 40, X_POSITIVE_PLANE, X_POSITIVE_PLANE, neckRotation));
        neck.add(new JointAngle(NECK_JOINT, 0, -20, 20, Z_POSITIVE_PLANE, Z_POSITIVE_PLANE, neckRotation));
        neck.add(new JointAngle(NECK_JOINT, 0, -10, 30, Y_NEGATIVE_PLANE, Y_NEGATIVE_PLANE, neckRotation));
        this.mJoints.put(0, neck);

        final Matrix leftShoulderRotation = new Matrix();
        final Matrix rightShoulderRotation = new Matrix();
        ArrayList<JointAngle> shoulder = new ArrayList<>();
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, 10, -10, 135, Z_NEGATIVE_PLANE, X_NEGATIVE_Z_NEGATIVE_PLANE, leftShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, 10, -10, 135, Z_POSITIVE_PLANE, X_NEGATIVE_Z_POSITIVE_PLANE, rightShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, 0, -20, 135, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, leftShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, 0, -20, 135, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, rightShoulderRotation));
        this.mJoints.put(1, shoulder);

        final Matrix trunkRotation = new Matrix();
        ArrayList<JointAngle> trunk = new ArrayList<>();
        trunk.add(new JointAngle(TRUNK_JOINT, 0, -90, 90, X_POSITIVE_PLANE, X_POSITIVE_PLANE, trunkRotation));
        trunk.add(new JointAngle(TRUNK_JOINT, 0, -90, 90, Z_POSITIVE_PLANE, Z_POSITIVE_PLANE, trunkRotation));
        trunk.add(new JointAngle(TRUNK_JOINT, 0, -15, 105, Y_NEGATIVE_PLANE, Y_NEGATIVE_PLANE, trunkRotation));
        this.mJoints.put(2, trunk);

        ArrayList<JointAngle> elbow = new ArrayList<>();
        elbow.add(new JointAngle(ELBOW_L_JOINT, 0, 0, 120, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, new Matrix()));
        elbow.add(new JointAngle(ELBOW_R_JOINT, 0, 0, 120, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, new Matrix()));
        this.mJoints.put(3, elbow);

        this.mWeight = 0;
        this.mHeight = 0;
        this.mBox = 0;
        this.mDescription = "";
        this.mDate = new Date();
    }

    public Skeleton(int neck1, int neck2, int neck3, int shoulder1, int shoulder2, int shoulder3,
                    int shoulder4, int trunk1, int trunk2, int trunk3, int elbow1, int elbow2,
                    int weight, int height, int box, String title, String description, Date date){

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

        final Matrix neckRotation = new Matrix();
        ArrayList<JointAngle> neck = new ArrayList<>();
        neck.add(new JointAngle(NECK_JOINT, neck1, -40, 40, X_POSITIVE_PLANE, X_POSITIVE_PLANE, neckRotation));
        neck.add(new JointAngle(NECK_JOINT, neck2, -20, 20, Z_POSITIVE_PLANE, Z_POSITIVE_PLANE, neckRotation));
        neck.add(new JointAngle(NECK_JOINT, neck3, -10, 30, Y_NEGATIVE_PLANE, Y_NEGATIVE_PLANE, neckRotation));
        this.mJoints.put(0, neck);

        final Matrix leftShoulderRotation = new Matrix();
        final Matrix rightShoulderRotation = new Matrix();
        ArrayList<JointAngle> shoulder = new ArrayList<>();
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, shoulder1, -10, 135, Z_NEGATIVE_PLANE, X_NEGATIVE_Z_NEGATIVE_PLANE, leftShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, shoulder2, -10, 135, Z_POSITIVE_PLANE, X_NEGATIVE_Z_POSITIVE_PLANE, rightShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_L_JOINT, shoulder3, -20, 135, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, leftShoulderRotation));
        shoulder.add(new JointAngle(SHOULDER_R_JOINT, shoulder4, -20, 135, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, rightShoulderRotation));
        this.mJoints.put(1, shoulder);

        final Matrix trunkRotation = new Matrix();
        ArrayList<JointAngle> trunk = new ArrayList<>();
        trunk.add(new JointAngle(TRUNK_JOINT, trunk1, -90, 90, X_POSITIVE_PLANE, X_POSITIVE_PLANE, trunkRotation));
        trunk.add(new JointAngle(TRUNK_JOINT, trunk2, -90, 90, Z_POSITIVE_PLANE, Z_POSITIVE_PLANE, trunkRotation));
        trunk.add(new JointAngle(TRUNK_JOINT, trunk3, -15, 105, Y_NEGATIVE_PLANE, Y_NEGATIVE_PLANE, trunkRotation));
        this.mJoints.put(2, trunk);

        ArrayList<JointAngle> elbow = new ArrayList<>();
        elbow.add(new JointAngle(ELBOW_L_JOINT, elbow1, 0, 120, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, new Matrix()));
        elbow.add(new JointAngle(ELBOW_R_JOINT, elbow2, 0, 120, Y_POSITIVE_PLANE, Y_POSITIVE_PLANE, new Matrix()));
        this.mJoints.put(3, elbow);
    }

    public void setDate(Date date){
        this.mDate = date;
    }

    public Date getDate(){
        return mDate;
    }
    public HashMap<Integer, ArrayList<JointAngle>> getJoints() {
        return mJoints;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }
    public String getTitle(){
        return mTitle;
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


    public Skeleton copy(){
        ArrayList<JointAngle> n, s, t, e;
        n = this.mJoints.get(0);
        s = this.mJoints.get(1);
        t = this.mJoints.get(2);
        e = this.mJoints.get(3);

        return new Skeleton(n.get(0).getAngle(), n.get(1).getAngle(), n.get(2).getAngle(),
                s.get(0).getAngle(), s.get(1).getAngle(), s.get(2).getAngle(), s.get(3).getAngle(),
                t.get(0).getAngle(), t.get(1).getAngle(), t.get(2).getAngle(), e.get(0).getAngle(),
                e.get(1).getAngle(), this.mWeight,this.mHeight, this.mBox, this.mTitle,
                this.mDescription, this.mDate);
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

       /* Log.d("test", "userWeight " + userWeight);
//        Log.d("test","userHeight "+userHeight);
        Log.d("test","boxWeight "+boxWeight);*/

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
        float[] midTrunkMoment = calculateMoment(trunkTranslation, chestTranslation, pelvisTranslation, mWeight, MID_TRUNK_MASS_CENTER, MID_TRUNK_SEGMENT_WEIGHT);
        float[] headMoment = calculateMoment(neckTranslation,headTopTranslation, pelvisTranslation, mWeight, HEAD_MASS_CENTER, HEAD_SEGMENT_WEIGHT);

        float[] upperarmLMoment = calculateMoment(shoulderLTranslation, elbowLTranslation, pelvisTranslation, mWeight, UPPERARM_MASS_CENTER, UPPERARM_SEGMENT_WEIGHT);
        float[] upperarmRMoment = calculateMoment(shoulderRTranslation, elbowRTranslation, pelvisTranslation, mWeight, UPPERARM_MASS_CENTER, UPPERARM_SEGMENT_WEIGHT);
        float[] forearmLMoment = calculateMoment(elbowLTranslation, handLTranslation, pelvisTranslation, mWeight, FOREARM_MASS_CENTER, FOREARM_SEGMENT_WEIGHT);
        float[] forearmRMoment = calculateMoment(elbowRTranslation, handRTranslation, pelvisTranslation, mWeight, FOREARM_MASS_CENTER, FOREARM_SEGMENT_WEIGHT);
        float[] boxLMoment = calculateBoxMoment(handLTranslation, pelvisTranslation, mWeight, mBox);
        float[] boxRMoment = calculateBoxMoment(handRTranslation, pelvisTranslation, mWeight, mBox);

        float flextionMoment = upperTrunkMoment[0]+upperTrunkMoment[0]+upperarmLMoment[0]+ upperarmRMoment[0]+ forearmLMoment[0] + forearmRMoment[0]+boxLMoment[0]+boxRMoment[0]+headMoment[0];
        float bendingMoment = midTrunkMoment[1]+midTrunkMoment[1]+upperarmLMoment[1]+ upperarmRMoment[1]+ forearmLMoment[1] + forearmRMoment[1]+boxLMoment[1]+boxRMoment[1]+headMoment[1];

        return (float)(1067.6f + 1.219f * flextionMoment + 0.083f * Math.pow(flextionMoment, 2) - 0.0001f * Math.pow(flextionMoment, 3));
        /*Log.d("test","totalForce   "+totalForce);
        Log.d("moment","totalFlextion   " + flextionMoment);
        Log.d("moment","totalFlextion   " + 0.083f*Math.pow(flextionMoment, 2));
        Log.d("moment","totalFlextion   " + - 0.0001f*Math.pow(flextionMoment, 3));
        Log.d("moment","totalBending   "+bendingMoment);*/
    }


    public class JointAngle {
        int id;
        int angle;
        int prevAngle;
        int minAngle;
        int maxAngle;
        Matrix rotation;
        SimpleVector positivePoseDirection;
        SimpleVector negativePoseDirection;

        public JointAngle(int id, int minAngle, int maxAngle, SimpleVector positivePoseDirection, SimpleVector negativePoseDirection, Matrix rotation) {
            this.id = id;
            this.angle = 0;
            this.prevAngle = 0;
            this.minAngle = minAngle;
            this.maxAngle = maxAngle;
            this.rotation = rotation;
            this.positivePoseDirection = positivePoseDirection;
            this.negativePoseDirection = negativePoseDirection;
        }

        public JointAngle(int id, int angle, int minAngle, int maxAngle, SimpleVector positivePoseDirection, SimpleVector negativePoseDirection, Matrix rotation) {
            this.id = id;
            this.angle = angle;
            this.prevAngle = 0;
            this.minAngle = minAngle;
            this.maxAngle = maxAngle;
            this.rotation = rotation;
            this.positivePoseDirection = positivePoseDirection;
            this.negativePoseDirection = negativePoseDirection;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }

        public int getPrevAngle() {
            return prevAngle;
        }

        public void updatePrevAngle() {
            this.prevAngle = this.angle;
        }

        public int getMinAngle() {
            return minAngle;
        }

        public void setMinAngle(int minAngle) {
            this.minAngle = minAngle;
        }

        public int getMaxAngle() {
            return maxAngle;
        }

        public void setMaxAngle(int maxAngle) {
            this.maxAngle = maxAngle;
        }

        public Matrix getRotation() {
            return rotation;
        }

        public void setRotation(Matrix rotation) {
            this.rotation = rotation;
        }

        public int getMidAngle(){
            return maxAngle - minAngle;
        }

        public SimpleVector getNegativePoseDirection() {
            return negativePoseDirection;
        }

        public SimpleVector getPositivePoseDirection() {
            return positivePoseDirection;
        }
    }
}
