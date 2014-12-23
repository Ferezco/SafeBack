package com.tri.felipe.safeback.Model;

import com.threed.jpct.Matrix;
import com.threed.jpct.SimpleVector;

/**
 * Created by Felipe on 14-12-23.
 */
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