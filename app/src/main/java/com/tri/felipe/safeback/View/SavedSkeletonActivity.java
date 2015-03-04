package com.tri.felipe.safeback.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tri.felipe.safeback.Controller.SkeletonController;
import com.tri.felipe.safeback.Model.Skeleton;
import com.tri.felipe.safeback.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Felipe on 15-03-01.
 */

public class SavedSkeletonActivity extends Activity {

    private ListView mSkeletonList;
    private ArrayList<Skeleton> mSkeletons;
    private Context mContext;
    private SkeletonAdapter mAdapter;

    /**
     * When the Activity is created, it loads and sets the list of saved skeletons
     * to display
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_load_skeleton);
        mSkeletonList = (ListView) findViewById(R.id.load_skeleton_list);
        mSkeletons = SkeletonController.get(this).getSkeletons();
        mAdapter = new SkeletonAdapter(mSkeletons);
        mSkeletonList.setAdapter(mAdapter);

        /*
         * When a skeleton is selected the id of selected
         * is put into an extra and sent back to the main fragment
         */
        mSkeletonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UUID sid = mSkeletons.get(position).getId();
                SkeletonController.get(mContext).logSkeletonStatus("Saved Skeleton", mSkeletons.get(position));
                Intent intent = new Intent();
                intent.putExtra(SkeletonFragment.LOAD, sid);
                setResult(Activity.RESULT_OK, intent);
                finish();//finishing activity
            }
        });

        /*
         * Handles the swipe mechanic for deleting
         */
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(mSkeletonList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {

                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mSkeletons.remove(position);
                                }
                                for (Skeleton s : mSkeletons) {
                                    Log.d("Current List", s.getTitle());
                                }
                                mAdapter.notifyDataSetChanged();
                                SkeletonController.get(mContext).saveAllSkeletons();
                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });

        mSkeletonList.setOnTouchListener(touchListener);
        //On Scroll listener required, so that scroll is not mistaken for swipe
        mSkeletonList.setOnScrollListener(touchListener.makeScrollListener());
    }

    private class SkeletonAdapter extends ArrayAdapter<Skeleton> {
        public SkeletonAdapter(ArrayList<Skeleton> skeletons) {
            super(mContext, 0, skeletons);
        }

        /*
         * Creates the individual rows for saved skeletons
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.activity_load_skeleton_list_item, null);
            }
            Skeleton skeleton = getItem(position);
            TextView skeletonName =
                    (TextView) convertView.findViewById(R.id.skeleton_list_item_name);
            skeletonName.setText(skeleton.getTitle());

            TextView skeletonDescription =
                    (TextView) convertView.findViewById(R.id.skeleton_list_item_description);
            if (skeleton.getDescription().length() < 40) {
                skeletonDescription.setText(skeleton.getDescription());
            } else {
                skeletonDescription.setText(skeleton.getDescription().substring(0, 40) + "...");
            }
            TextView timeStamp = (TextView) convertView.findViewById(R.id.skeleton_list_item_timestamp);
            timeStamp.setText(DateFormat.format("dd-MMM h:mm ", skeleton.getDate()));

            return convertView;
        }

    }
}

