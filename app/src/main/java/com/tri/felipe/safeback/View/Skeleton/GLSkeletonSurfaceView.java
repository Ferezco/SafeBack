package com.tri.felipe.safeback.View.Skeleton;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Felipe on 14-11-20.
 */
public class GLSkeletonSurfaceView extends GLSurfaceView {

    public GLSkeletonSurfaceView(Context context) {
        super(context);
        setRenderer(new SkeletonRenderer());
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public GLSkeletonSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
