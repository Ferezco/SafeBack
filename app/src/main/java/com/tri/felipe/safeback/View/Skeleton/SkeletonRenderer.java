package com.tri.felipe.safeback.View.Skeleton;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;

import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Logger;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Felipe on 14-11-20.
 */
public class SkeletonRenderer implements GLSurfaceView.Renderer {
        private int fps = 0;
        private int lfps = 0;
        private FrameBuffer frameBuffer;

        private long fpsTime = System.currentTimeMillis();

        public SkeletonRenderer() {
            Config.maxPolysVisible = 5000;
            Config.farPlane = 1500;
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            Logger.log("onSurfaceChanged");
            if (frameBuffer != null) {
                frameBuffer.dispose();
            }

            frameBuffer = new FrameBuffer(gl, w, h);

            //cameraController.placeCamera();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Logger.log("onSurfaceCreated");

            TextureManager.getInstance().flush();
            Resources res = SkeletonFragment.res;

            Texture texture = new Texture(res.openRawResource(R.raw.skeleton_texture));
            texture.keepPixelData(true);
            TextureManager.getInstance().addTexture("texture", texture);

            for (Animated3D a : skeleton)
                a.setTexture("texture");
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            if (frameBuffer == null)
                return;


            long now = System.currentTimeMillis();
            aggregatedTime += (now - frameTime);
            frameTime = now;

            if (aggregatedTime > 1000) {
                aggregatedTime = 0;
            }


            while (aggregatedTime > GRANULARITY) {
                aggregatedTime -= GRANULARITY;
                cameraController.placeCamera();

            }


            frameBuffer.clear(new RGBColor(222, 218, 207, 0));

            world.renderScene(frameBuffer);
            world.draw(frameBuffer);

            frameBuffer.display();

            if (System.currentTimeMillis() - fpsTime >= 1000) {
                lfps = (fps + lfps) >> 1;
                fps = 0;
                fpsTime = System.currentTimeMillis();
            }
            fps++;

        }


        /** adjusts camera based on current mesh of skinned group.
         * camera looks at mid point of height and placed at a distance
         * such that group height occupies 2/3 of screen height. */
        protected void autoAdjustCamera() {
            float[] bb = calcBoundingBox();
            float groupHeight = bb[3] - bb[2];
            cameraController.cameraRadius = calcDistance(world.getCamera(), frameBuffer,
                    frameBuffer.getHeight() / 1.5f , groupHeight);
            cameraController.minCameraRadius = groupHeight / 10f;
            cameraController.cameraTarget.y = (bb[3] + bb[2]) / 2;
            cameraController.placeCamera();
        }

        /** calculates and returns whole bounding box of skinned group */
        protected float[] calcBoundingBox() {
            float[] box = null;

            for (Animated3D skin : skeleton) {
                float[] skinBB = skin.getMesh().getBoundingBox();

                if (box == null) {
                    box = skinBB;
                } else {
                    // x
                    box[0] = Math.min(box[0], skinBB[0]);
                    box[1] = Math.max(box[1], skinBB[1]);
                    // y
                    box[2] = Math.min(box[2], skinBB[2]);
                    box[3] = Math.max(box[3], skinBB[3]);
                    // z
                    box[4] = Math.min(box[4], skinBB[4]);
                    box[5] = Math.max(box[5], skinBB[5]);
                }
            }
            return box;
        }

        /**
         * calculates a camera distance to make object look height pixels on screen
         * @author EgonOlsen
         * */
        protected float calcDistance(Camera c, FrameBuffer buffer, float height, float objectHeight) {
            float h = height / 2f;
            float os = objectHeight / 2f;

            Camera cam = new Camera();
            cam.setFOV(c.getFOV());
            SimpleVector p1 = Interact2D.project3D2D(cam, buffer, new SimpleVector(0f, os, 1f));
            float y1 = p1.y - buffer.getCenterY();
            float z = (1f/h) * y1;

            return z;
        }
    }

    class MyRadioGroupOnCheckedChangedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (!changedGroup) {
                changedGroup = true;
                if (group == radioGroup1) {
                    radioGroup2.clearCheck();
                    radioGroup3.clearCheck();
                } else if (group == radioGroup2) {
                    radioGroup1.clearCheck();
                    radioGroup3.clearCheck();
                } else if (group == radioGroup3){
                    radioGroup1.clearCheck();
                    radioGroup2.clearCheck();
                }
                changedGroup = false;
            }
        }
    }
}
