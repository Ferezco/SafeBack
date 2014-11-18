package com.tri.felipe.safeback.View.Skeleton;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tri.felipe.safeback.R;

/**
 * Created by Felipe on 14-11-14.
 */
public class SkeletonFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_skeleton, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
