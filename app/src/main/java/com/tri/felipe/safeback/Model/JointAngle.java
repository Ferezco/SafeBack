package com.tri.felipe.safeback.Model;

import com.threed.jpct.Matrix;
import com.threed.jpct.SimpleVector;

import java.io.Serializable;

/**
 * Created by Felipe on 14-12-23.
 */
public class JointAngle implements Serializable {
    int maxAngle;
    private int id;
    private int angle;
    private int prevAngle;
    private int minAngle;
    private Matrix mRotationMatrix;
    private SimpleVector positivePoseDirection;
    private SimpleVector negativePoseDirection;

    public JointAngle(int id, int angle, int minAngle, int maxAngle, SimpleVector positivePoseDirection, SimpleVector negativePoseDirection, Matrix rotation) {
        this.id = id;
        this.angle = angle;
        this.prevAngle = 0;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.mRotationMatrix = rotation;
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

    public Matrix getRotationMatrix() {
        return mRotationMatrix;
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