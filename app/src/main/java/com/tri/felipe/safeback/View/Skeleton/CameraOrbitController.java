package com.tri.felipe.safeback.View.Skeleton;

import com.threed.jpct.Camera;
import com.threed.jpct.SimpleVector;


public class CameraOrbitController {

    public final SimpleVector cameraTarget = new SimpleVector(0, 0, 0);
    /** the angle with respect to positive Z axis. initial value is PI so looking down to positive Z axis. */
    public float cameraAngle = (float)(Math.PI);
    public float cameraRadius = 20f;

    private Camera camera;

    public CameraOrbitController(Camera camera) {
        this.camera = camera;
    }
    public void placeCamera() {

        float camX = (float) Math.sin(cameraAngle) * cameraRadius;
        float camZ = (float) Math.cos(cameraAngle) * cameraRadius;

        SimpleVector camPos = new SimpleVector(camX, 0, camZ);
        camPos.add(cameraTarget);
        camera.setPosition(camPos);
        camera.lookAt(cameraTarget);
    }


}
