package com.tri.felipe.safeback.View.Skeleton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tri.felipe.safeback.Controller.JSONParser;
import com.tri.felipe.safeback.Controller.SkeletonController;
import com.tri.felipe.safeback.Model.Skeleton;
import com.tri.felipe.safeback.Model.Training;
import com.tri.felipe.safeback.R;
import com.tri.felipe.safeback.View.NavigationActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Felipe on 14-11-14.
 */
public class SkeletonFragment extends Fragment {

    private SkeletonController control = new SkeletonController();
    private TextView mTotalForce;
    private int mForce = 0;
    private RelativeLayout mSkeletonLayout;
    private final SkeletonRenderer mRenderer = new SkeletonRenderer();

    private static String URL_SKELTON = "http://104.131.178.112:8000/safeback/skeleton";

    private Spinner mJoint;
    private TextView mBottomText;
    private LinearLayout mTraitPanel;
    private TextView mTrait1;
    private SeekBar mSeek1;
    private TextView mCount1;
    private TextView mTrait2;
    private SeekBar mSeek2;
    private TextView mCount2;
    private TextView mTrait3;
    private SeekBar mSeek3;
    private TextView mCount3;
    private TextView mTrait4;
    private SeekBar mSeek4;
    private TextView mCount4;
    private ImageButton mExpandButton;
    private RelativeLayout mUserPanel;
    private TextView mWeight;
    private SeekBar mWeightSeek;
    private EditText mWeightEdit;
    private TextView mHeight;
    private SeekBar mHeightSeek;
    private EditText mHeightEdit;
    private TextView mBox;
    private SeekBar mBoxSeek;
    private EditText mBoxEdit;

    private EditText mSkeletonName;
    private EditText mSkeletonDescription;
    private AlertDialog.Builder SkeletonDialog;
    private ListView mSkeletonList;

    //if user panel is expanded or not
    private boolean USER_EXPANDED = false;
    //true: Metric, false: Imperial
    private boolean METRIC = true;
    //current joint being shown
    private int CURRENT_JOINT = 0;

    //SeekBar maximum values
    private static int USER_MAX = 250;
    private static int SKELETON_MAX = 180;
    private static int SKELETON_MID = 90;
    private Skeleton mSkeleton;

    //JSON
    private JSONObject jSkeleton = null;
    private static final String TAG_TRAINING = "fields";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DATE = "date";
    private static final String TAG_TRUNK = "trunk";
    private static final String TAG_SHOULDER = "shoulder";
    private static final String TAG_NECK = "neck";
    private static final String TAG_ELBOW = "elbow";
    private static final String TAG_USER = "user";

    private ArrayList<Skeleton> mSkeletons;

    public Resources res = getActivity().getResources();

    private String[][] traits =
            {{"Trunk Rotation", "Trunk Lateral Bending", "Trunk Extension"},
                    {"L Shoulder Abduction", "R Shoulder Abduction",
                            "L Shoulder  Extension", "R Shoulder Extenson"},
                    {"Neck Axial Twist", "Neck Lateral Bending", "Neck Extension"},
                    {"L Elbow Extension", "R Elbow Extension"}};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSkeletons =SkeletonController.get(getActivity()).getSkeletons();
        mSkeleton = new Skeleton();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_skeleton, container, false);

        /*GLSurfaceView mGLView = new GLSurfaceView(getActivity().getApplication());
        mGLView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mGLView.setRenderer(mRenderer);
        mSkeletonLayout = (RelativeLayout) rootView.findViewById(R.id.skeleton_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
        mSkeletonLayout.addView(mGLView, params);*/

        mTotalForce = (TextView) rootView.findViewById(R.id.total_force);

        mBottomText = (TextView) rootView.findViewById(R.id.skeleton_user_info);
        mTraitPanel = (LinearLayout) rootView.findViewById(R.id.skeleton_trait_panel);

        mJoint = (Spinner) rootView.findViewById(R.id.skeleton_joint_spinner);
        mJoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setVisible();
                CURRENT_JOINT = position;
                mTrait1.setText(traits[position][0]);
                mCount1.setText(Integer.toString(mSkeleton.getJoints()[position][0]));
                mSeek1.setProgress(mSkeleton.getJoints()[position][0] + SKELETON_MID);

                mTrait2.setText(traits[position][1]);
                mCount2.setText(Integer.toString(mSkeleton.getJoints()[position][1]));
                mSeek2.setProgress(mSkeleton.getJoints()[position][1] + SKELETON_MID);

                for (int i = 0; i < mSkeleton.getJoints()[position].length; i++){
                    Log.d("Trait" + position, "value:" + mSkeleton.getJoints()[position][i]);
                }

                if (position == 3) {
                    mTrait3.setVisibility(View.INVISIBLE);
                    mSeek3.setVisibility(View.INVISIBLE);
                    mCount3.setVisibility(View.INVISIBLE);

                    mTrait4.setVisibility(View.INVISIBLE);
                    mSeek4.setVisibility(View.INVISIBLE);
                    mCount4.setVisibility(View.INVISIBLE);
                }
                else {
                    mTrait3.setText(traits[position][2]);
                    mCount3.setText(Integer.toString(mSkeleton.getJoints()[position][2]));
                    mSeek3.setProgress(mSkeleton.getJoints()[position][2] + SKELETON_MID);

                    if (position == 1) {
                        mTrait4.setText(traits[position][3]);
                        mCount4.setText(Integer.toString(mSkeleton.getJoints()[position][3]));
                        mSeek4.setProgress(mSkeleton.getJoints()[position][3] + SKELETON_MID);
                    } else {
                        mTrait4.setVisibility(View.INVISIBLE);
                        mSeek4.setVisibility(View.INVISIBLE);
                        mCount4.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTrait1 = (TextView) rootView.findViewById(R.id.skeleton_trait1_text);
        mTrait1.setText(traits[CURRENT_JOINT][0]);
        mSeek1 = (SeekBar) rootView.findViewById(R.id.skeleton_trait1_slider);
        mSeek1.setMax(SKELETON_MAX);
        mSeek1.setProgress(SKELETON_MID);
        mSeek1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mForce -= mSkeleton.getJoints()[CURRENT_JOINT][0];
                mSkeleton.getJoints()[CURRENT_JOINT][0] = progress - SKELETON_MID;
                mCount1.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][0]));
                mForce += mSkeleton.getJoints()[CURRENT_JOINT][0];
                mTotalForce.setText(Integer.toString(mForce) + " N");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mCount1 = (TextView) rootView.findViewById(R.id.skeleton_trait1_value);

        mTrait2 = (TextView) rootView.findViewById(R.id.skeleton_trait2_text);
        mTrait2.setText(traits[CURRENT_JOINT][1]);
        mSeek2 = (SeekBar) rootView.findViewById(R.id.skeleton_trait2_slider);
        mSeek2.setMax(SKELETON_MAX);
        mSeek2.setProgress(SKELETON_MID);
        mSeek2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mForce -= mSkeleton.getJoints()[CURRENT_JOINT][1];
                mSkeleton.getJoints()[CURRENT_JOINT][1] = progress - SKELETON_MID;
                mCount2.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][1]));
                mForce += mSkeleton.getJoints()[CURRENT_JOINT][1];
                mTotalForce.setText(Integer.toString(mForce) + " N");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mCount2 = (TextView) rootView.findViewById(R.id.skeleton_trait2_value);

        mTrait3 = (TextView) rootView.findViewById(R.id.skeleton_trait3_text);
        mTrait3.setText(traits[0][2]);
        mSeek3 = (SeekBar) rootView.findViewById(R.id.skeleton_trait3_slider);
        mSeek3.setMax(SKELETON_MAX);
        mSeek3.setProgress(SKELETON_MID);
        mSeek3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mForce -= mSkeleton.getJoints()[CURRENT_JOINT][2];
                mSkeleton.getJoints()[CURRENT_JOINT][2] = progress - SKELETON_MID;
                mCount3.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][2]));
                mForce += mSkeleton.getJoints()[CURRENT_JOINT][2];
                mTotalForce.setText(Integer.toString(mForce) + " N");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mCount3 = (TextView) rootView.findViewById(R.id.skeleton_trait3_value);
        mCount3.setVisibility(View.INVISIBLE);

        mTrait4 = (TextView) rootView.findViewById(R.id.skeleton_trait4_text);
        mTrait4.setVisibility(View.INVISIBLE);
        mSeek4 = (SeekBar) rootView.findViewById(R.id.skeleton_trait4_slider);
        mSeek4.setMax(SKELETON_MAX);
        mSeek4.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mForce -= mSkeleton.getJoints()[CURRENT_JOINT][3];
                mSkeleton.getJoints()[CURRENT_JOINT][3] = progress - SKELETON_MID;
                mCount4.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][3]));
                mForce += mSkeleton.getJoints()[CURRENT_JOINT][3];
                mTotalForce.setText(Integer.toString(mForce) + " N");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mSeek4.setVisibility(View.INVISIBLE);
        mCount4 = (TextView) rootView.findViewById(R.id.skeleton_trait4_value);
        mCount4.setVisibility(View.INVISIBLE);

        mWeight = (TextView) rootView.findViewById(R.id.user_weight_text);
        mWeightSeek = (SeekBar) rootView.findViewById(R.id.user_weight_slider);
        mWeightEdit = (EditText) rootView.findViewById(R.id.user_weight_EditText);
        mWeightEdit.setText("0");
        mWeightEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (METRIC) {
                    mWeightSeek.setProgress(Math.min((Integer.parseInt(
                            mWeightEdit.getText().toString())), USER_MAX));
                }
                else{
                    mWeightSeek.setProgress(Math.min(
                            control.PoundToKilo(Integer.parseInt(
                                    mWeightEdit.getText().toString())),
                            USER_MAX));
                }
            }
        });
        mWeightSeek.setMax(USER_MAX);
        mWeightSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSkeleton.setWeight(progress);
                if (METRIC){
                    mWeightEdit.setText(Integer.toString(progress));
                }
                else{
                    mWeightEdit.setText(Integer.toString(control.
                            KiloToPound(Math.min(progress, USER_MAX))));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        mHeight = (TextView) rootView.findViewById(R.id.user_height_text);
        mHeightSeek = (SeekBar) rootView.findViewById(R.id.user_height_slider);
        mHeightEdit = (EditText) rootView.findViewById(R.id.user_height_EditText);
        mHeightEdit.setText("0");
        mHeightEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (METRIC) {
                    mWeightSeek.setProgress(Math.min((Integer.parseInt(
                            mWeightEdit.getText().toString())), USER_MAX));
                }
                else{
                    mWeightSeek.setProgress(Math.min(
                            control.PoundToKilo(Integer.parseInt(
                                    mWeightEdit.getText().toString())),
                            USER_MAX));
                }
            }
        });
        mHeightSeek.setMax(USER_MAX);
        mHeightSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSkeleton.setHeight(progress);
                if (METRIC){
                    mHeightEdit.setText(Integer.toString(progress));
                }
                else{
                    mHeightEdit.setText(Integer.toString(control.
                            CmToInch(Math.min(progress, USER_MAX))));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        mBox = (TextView) rootView.findViewById(R.id.box_weight_text);
        mBoxSeek = (SeekBar) rootView.findViewById(R.id.box_weight_slider);
        mBoxEdit = (EditText) rootView.findViewById(R.id.box_weight_EditText);
        mBoxEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (METRIC) {
                    mBoxSeek.setProgress(Math.min((Integer.parseInt(
                            mWeightEdit.getText().toString())), USER_MAX));
                }
                else{
                    mBoxSeek.setProgress(Math.min(
                            control.PoundToKilo(Integer.parseInt(
                                    mWeightEdit.getText().toString())), USER_MAX));
                }
            }
        });
        mBoxEdit.setText("0");
        mBoxSeek.setMax(USER_MAX);
        mBoxSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSkeleton.setBoxWeight(progress);
                if (METRIC) {
                    mBoxEdit.setText(Integer.toString(progress));
                } else {
                    mBoxEdit.setText(Integer.toString(control.
                            KiloToPound(Math.min(progress, USER_MAX))));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mUserPanel = (RelativeLayout) rootView.findViewById(R.id.expanded_user_panel);
        mExpandButton = (ImageButton) rootView.findViewById(R.id.expand_user_panel_button);
        mExpandButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int mShortAnimationDuration = getResources()
                        .getInteger(android.R.integer.config_shortAnimTime);
                Animator anim;
                int buttonTrans = -mUserPanel.getHeight() + mBottomText.getHeight();

                if (USER_EXPANDED){
                    if (Build.VERSION.SDK_INT >= 21) {
                        int cx = (mUserPanel.getLeft() + mUserPanel.getRight()) / 2;
                        int cy = (mUserPanel.getTop() + mUserPanel.getBottom()) / 2;

                        int initialRadius = mUserPanel.getWidth();
                        anim = ViewAnimationUtils.createCircularReveal(mUserPanel, cx, cy, initialRadius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mUserPanel.setVisibility(View.INVISIBLE);
                            }
                        });
                        anim.start();
                    }
                    else {
                        mUserPanel.animate()
                            .alpha(0f)
                            .setDuration(mShortAnimationDuration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mUserPanel.setVisibility(View.INVISIBLE);
                                }
                            });
                    }
                    mExpandButton.animate()
                            .translationYBy(-buttonTrans)
                            .rotation(90)
                            .setDuration(mShortAnimationDuration);
                    USER_EXPANDED = false;
                }else{
                    if (Build.VERSION.SDK_INT >= 21) {
                        int cx = (mUserPanel.getLeft() + mUserPanel.getRight()) / 2;
                        int cy = (mUserPanel.getTop() + mUserPanel.getBottom()) / 2;
                        int finalRadius = Math.max(mUserPanel.getWidth(), mUserPanel.getHeight());

                        anim = ViewAnimationUtils.createCircularReveal(mUserPanel, cx, cy, 0, finalRadius);
                        mUserPanel.setVisibility(View.VISIBLE);
                        anim.start();
                    }
                    else {
                        mUserPanel.setAlpha(0f);
                        mUserPanel.setVisibility(View.VISIBLE);

                        mUserPanel.animate()
                                .alpha(1f)
                                .setDuration(mShortAnimationDuration)
                                .setListener(null);
                    }
                    mExpandButton.animate()
                            .translationYBy(buttonTrans)
                            .rotation(-90)
                            .setDuration(mShortAnimationDuration);
                    USER_EXPANDED = true;
                }
                Log.d("User",  "Weight" + mWeightEdit.getText().toString());
                Log.d("User",  "Height" + mHeightEdit.getText().toString());
                Log.d("User",  "Box" + mBoxEdit.getText().toString());

            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.safeback, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View view1;
        switch (item.getItemId()) {
            case R.id.action_save:
                view1 = factory.inflate(R.layout.dialog_save_skeleton, null);
                mSkeletonName = (EditText) view1.findViewById(R.id.skeleton_name_editText);
                mSkeletonDescription = (EditText) view1.findViewById(R.id.skeleton_description_editText);
                SkeletonDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Save Skeleton Configuration").setView(view1).
                        setPositiveButton("Save", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!mSkeletonName.getText().toString().equals("") &&
                                        !mSkeletonDescription.getText().toString().equals("")) {
                                    mSkeleton.setTitle(mSkeletonName.getText().toString());
                                    mSkeleton.setDescription(mSkeletonDescription.getText().toString());
                                    mSkeleton.setDate(new Date());
                                    mSkeletons.add(mSkeleton);
                                    mSkeleton = new Skeleton();
                                }
                                else{
                                    Toast.makeText(getActivity(), "Missing title or description", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("Cancel", null);

                AlertDialog saveDialog = SkeletonDialog.show();
                if (Build.VERSION.SDK_INT < 21) {
                    int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
                    View titleDivider = saveDialog.findViewById(titleDividerId);
                    if (titleDivider != null)
                        titleDivider.setBackgroundColor(Color.parseColor("#036bac"));
                }
                break;
            case R.id.action_units:
                if (METRIC){
                    mWeight.setText("User Weight: lbs");
                    mWeightEdit.setText(Integer.toString(control.
                            KiloToPound(Integer.parseInt(mWeightEdit.getText().toString()))));
                    mHeight.setText("User Height: Inches");
                    mHeightEdit.setText(Integer.toString(control.
                            CmToInch(Integer.parseInt(mHeightEdit.getText().toString()))));
                    mBox.setText("Box Weight: lbs");
                    mBoxEdit.setText(Integer.toString(control.
                            KiloToPound(Integer.parseInt(mBoxEdit.getText().toString()))));
                    item.setTitle("Imperial");
                }else{
                    mWeight.setText("User Weight: kg");
                    mWeightEdit.setText(Integer.toString(control.
                            PoundToKilo(Integer.parseInt(mWeightEdit.getText().toString()))));
                    mHeight.setText("User Height: cm");
                    mHeightEdit.setText(Integer.toString(control.
                            InchToCm(Integer.parseInt(mHeightEdit.getText().toString()))));
                    mBox.setText("Box Weight: kg");
                    mBoxEdit.setText(Integer.toString(control.
                            PoundToKilo(Integer.parseInt(mBoxEdit.getText().toString()))));
                    item.setTitle("Metric");
                }
                METRIC = !METRIC;
                break;
            case R.id.action_load_skeleton:
                mSkeletonList = (ListView) factory.inflate(R.layout.dialog_load_skeleton, null);
                mSkeletonList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Skeleton s = (Skeleton) mSkeletonList.getAdapter().getItem(position);
                        mSkeleton = s;
                        UpdateControls();
                    }
                });
                if (mSkeletons.size() == 0){
                    new LoadAllSkeletons().execute();
                }
                SkeletonAdapter adapter = new SkeletonAdapter(mSkeletons);
                mSkeletonList.setAdapter(adapter);
                SkeletonDialog = new AlertDialog.Builder(getActivity()).setView(mSkeletonList)
                        .setTitle("Select a Skeleton").setNegativeButton("Dismiss", null);

                AlertDialog loadDialog = SkeletonDialog.show();
                if (Build.VERSION.SDK_INT < 21) {
                    int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
                    View titleDivider = loadDialog.findViewById(titleDividerId);
                    if (titleDivider != null)
                        titleDivider.setBackgroundColor(Color.parseColor("#036bac"));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setVisible(){
        mTrait3.setVisibility(View.VISIBLE);
        mTrait4.setVisibility(View.VISIBLE);
        mSeek3.setVisibility(View.VISIBLE);
        mSeek4.setVisibility(View.VISIBLE);
        mCount3.setVisibility(View.VISIBLE);
        mCount4.setVisibility(View.VISIBLE);

    }
    private void UpdateControls() {
        mWeight.setText("User Weight: kg");
        mWeightSeek.setProgress(mSkeleton.getWeight());
        mWeightEdit.setText(Integer.toString(mSkeleton.getWeight()));

        mHeight.setText("User Height: cm");
        mHeightSeek.setProgress(mSkeleton.getHeight());
        mHeightEdit.setText(Integer.toString(mSkeleton.getHeight()));

        mBox.setText("Box Weight: kg");
        mBoxSeek.setProgress(mSkeleton.getBoxWeight());
        mBoxEdit.setText(Integer.toString(mSkeleton.getBoxWeight()));

        mSeek1.setProgress(mSkeleton.getJoints()[CURRENT_JOINT][0] + SKELETON_MID);
        mCount1.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][0]));
        mSeek2.setProgress(mSkeleton.getJoints()[CURRENT_JOINT][1] + SKELETON_MID);
        mCount2.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][1]));
        mSeek3.setProgress(mSkeleton.getJoints()[CURRENT_JOINT][2] + SKELETON_MID);
        mCount3.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][2]));
        mSeek4.setProgress(mSkeleton.getJoints()[CURRENT_JOINT][3] + SKELETON_MID);
        mCount4.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][3]));
        METRIC = true;

    }

    private class SkeletonAdapter extends ArrayAdapter<Skeleton> {

        public SkeletonAdapter(ArrayList<Skeleton> skeletons) {
            super(getActivity(), 0, skeletons);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_skeleton, null);
            }
            Skeleton s = getItem(position);

            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.skeleton_list_item_titleTextView);
            titleTextView.setText(s.getTitle());

            TextView descriptionTextView =
                    (TextView) convertView.findViewById(R.id.skeleton_list_item_descriptionTextView);
            if(s.getDescription().length() < 40){
                descriptionTextView.setText(s.getDescription());
            }
            else {
                descriptionTextView.setText(s.getDescription().substring(0, 40) + "...");
            }

            return convertView;
        }
    }

    class LoadAllSkeletons extends AsyncTask<String, String, Void> {

        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... args) {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONParser jParser = new JSONParser();
            JSONArray json = jParser.makeHttpRequest(URL_SKELTON, "GET", params);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            Log.d("All Skeleton:", json.toString());
            try{
                for (int i = 0; i < json.length(); i++){
                    jSkeleton = json.getJSONObject(i).getJSONObject(TAG_TRAINING);
                    Log.d("single skeleton:", jSkeleton.toString());
                    String title = jSkeleton.getString(TAG_TITLE);
                    String description = jSkeleton.getString(TAG_DESCRIPTION);
                    String created_at = jSkeleton.getString(TAG_DATE);
                    String trunk = jSkeleton.getString(TAG_TRUNK);
                    String shoulder = jSkeleton.getString(TAG_SHOULDER);
                    String neck = jSkeleton.getString(TAG_NECK);
                    String elbow = jSkeleton.getString(TAG_ELBOW);
                    String[] user = jSkeleton.getString(TAG_USER).split(",");

                    int[][] joints = new int[4][4];
                    joints[0] = JointsFromString(trunk);
                    joints[1] = JointsFromString(shoulder);
                    joints[2] = JointsFromString(neck);
                    joints[3] = JointsFromString(elbow);
;                   Skeleton s = new Skeleton(joints,
                            Integer.parseInt(user[0]), Integer.parseInt(user[1]),
                            Integer.parseInt(user[2]), title, description, df.parse(created_at));
                    mSkeletons.add(s);
                }
            }catch (JSONException e){
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPreExecute();
            pDialog.dismiss();
            SkeletonAdapter adapter = new SkeletonAdapter(mSkeletons);
            mSkeletonList.setAdapter(adapter);
        }
    }

    private int[] JointsFromString (String initial){
        String[] broken = initial.split(",");
        int[] output = new int[4];
        for (int i = 0; i < 4; i++){
            output[i] = Integer.parseInt(broken[i]);
        }
        return output;
    }
}
