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
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Matrix;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.tri.felipe.safeback.Controller.SkeletonController;
import com.tri.felipe.safeback.Model.Skeleton;
import com.tri.felipe.safeback.Model.JointAngle;
import com.tri.felipe.safeback.R;
import com.tri.felipe.safeback.View.NavigationActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;
import raft.jpct.bones.Quaternion;
import raft.jpct.bones.SkeletonPose;
/**
 * Created by Felipe on 14-11-14.
 */
public class SkeletonFragment extends Fragment {

    private SkeletonController control = new SkeletonController();

    //3D Skeleton
    private GLSurfaceView mGLView;
    private final MyRenderer mRenderer = new MyRenderer();
    private World world = null;
    private CameraOrbitController cameraController;
    private AnimatedGroup skeleton = null;
    private long frameTime = System.currentTimeMillis();
    private long aggregatedTime = 0;
    private static final int GRANULARITY = 25;
    private FrameBuffer frameBuffer = null;
    private int FRONT_VIEW = 0;


    //Views
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
    private TextView mTotalForce;
    private RelativeLayout mSkeletonLayout;

    //if user panel is expanded or not
    private boolean USER_EXPANDED = false;
    //true: Metric, false: Imperial
    private boolean METRIC = true;
    //current joint being shown
    private int CURRENT_JOINT = 0;
    private float FORCE;

    //SeekBar maximum values
    private static int USER_MAX = 250;
    private Skeleton mSkeleton;
    private ArrayList<JointAngle> mCurrentJoint;

    private ArrayList<Skeleton> mSkeletons;

    //public Resources res = getActivity().getResources();

    private String[][] traits = {
                    {"Neck Axial Twist", "Neck Lateral Bending", "Neck Extension"},
                    {"L Shoulder Abduction", "R Shoulder Abduction",
                    "L Shoulder  Extension", "R Shoulder Extenson"},
                    {"Trunk Rotation", "Trunk Lateral Bending", "Trunk Extension"},
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

        //View to be filled by 3D skeleton
        mSkeletonLayout = (RelativeLayout) rootView.findViewById(R.id.skeleton_layout);
        //3D skeletonView
        mGLView = new GLSurfaceView(getActivity().getApplication());

        mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {

            @Override
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer.
                int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });

        //mGLView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mGLView.setRenderer(mRenderer);
        mSkeletonLayout.addView(mGLView);
        mSkeletonLayout.setOnClickListener(new OnClickListener() {

            //Handles touch to the skeleton switch orientation of the view
            @Override
            public void onClick(View v) {
                switch (FRONT_VIEW) {
                    case 0:
                        rightSagittalView();
                        FRONT_VIEW++;
                        break;
                    case 1:
                        leftSagittalView();
                        FRONT_VIEW++;
                        break;
                    case 2:
                        frontView();
                        FRONT_VIEW = 0;
                        break;
                }
            }
        });

        world = new World();
        addSkeleton();

        world.setAmbientLight(250, 250, 250);
        world.buildAllObjects();

        float[] boundingBox = mRenderer.calcBoundingBox();
        float height = (boundingBox[3]); // ninja height
        new Light(world).setPosition(new SimpleVector(0, -height/2, height));

        cameraController = new CameraOrbitController(world.getCamera());
        cameraController.cameraAngle = 0;
        cameraController.placeCamera();
        applyAllRotations();

        //Wiring up View
        mCurrentJoint = mSkeleton.getJoints().get(CURRENT_JOINT);
        mTotalForce = (TextView) rootView.findViewById(R.id.total_force);
        mTotalForce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(getActivity(),
                        String.format("%.0f N is being applied to the lower back",FORCE),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mBottomText = (TextView) rootView.findViewById(R.id.skeleton_user_info);
        mTraitPanel = (LinearLayout) rootView.findViewById(R.id.skeleton_trait_panel);

        mJoint = (Spinner) rootView.findViewById(R.id.skeleton_joint_spinner);
        mJoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Skeleton", String.format("Neck %d %d %d, Shoulder %d %d %d %d, Trunk %d %d %d, Elbow %d %d",
                        mSkeleton.getJoints().get(0).get(0).getAngle(),
                        mSkeleton.getJoints().get(0).get(1).getAngle(),
                        mSkeleton.getJoints().get(0).get(2).getAngle(),
                        mSkeleton.getJoints().get(1).get(0).getAngle(),
                        mSkeleton.getJoints().get(1).get(1).getAngle(),
                        mSkeleton.getJoints().get(1).get(2).getAngle(),
                        mSkeleton.getJoints().get(1).get(3).getAngle(),
                        mSkeleton.getJoints().get(2).get(0).getAngle(),
                        mSkeleton.getJoints().get(2).get(1).getAngle(),
                        mSkeleton.getJoints().get(2).get(2).getAngle(),
                        mSkeleton.getJoints().get(3).get(0).getAngle(),
                        mSkeleton.getJoints().get(3).get(1).getAngle()));
                CURRENT_JOINT = position;

                mCurrentJoint = mSkeleton.getJoints().get(position);
                //resets the visibility of all seekbars
                setVisible();

                mTrait1.setText(traits[position][0]);
                mCount1.setText(Integer.toString(mCurrentJoint.get(0).getAngle()));
                Log.d("Joint 1", Float.toString(mCurrentJoint.get(0).getAngle() -
                        mCurrentJoint.get(0).getMinAngle()));
                mSeek1.setProgress(mCurrentJoint.get(0).getAngle() -
                        mCurrentJoint.get(0).getMinAngle());
                mSeek1.setMax(mCurrentJoint.get(0).getMidAngle());

                Log.d("joint 1", "angle" + mCurrentJoint.get(0).getAngle());

                mTrait2.setText(traits[position][1]);
                mCount2.setText(Integer.toString(mCurrentJoint.get(1).getAngle()));
                mSeek2.setProgress(mCurrentJoint.get(1).getAngle() -
                        mCurrentJoint.get(1).getMinAngle());
                mSeek2.setMax(mCurrentJoint.get(1).getMidAngle());

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
                    mCount3.setText(Integer.toString(mCurrentJoint.get(2).getAngle()));
                    mSeek3.setProgress(mCurrentJoint.get(2).getAngle() -
                            mCurrentJoint.get(2).getMinAngle());
                    mSeek3.setMax(mCurrentJoint.get(2).getMidAngle());

                    if (position == 1) {
                        mTrait4.setText(traits[position][3]);
                        mCount4.setText(Integer.toString(mCurrentJoint.get(3).getAngle()));
                        mSeek4.setProgress(mCurrentJoint.get(3).getAngle() -
                                mCurrentJoint.get(3).getMinAngle());
                        mSeek4.setMax(mCurrentJoint.get(3).getMidAngle());

                    } else {
                        mTrait4.setVisibility(View.INVISIBLE);
                        mSeek4.setVisibility(View.INVISIBLE);
                        mCount4.setVisibility(View.INVISIBLE);
                    }
                }
                //Log all angles for the current joint and changes
                for (int i = 0; i < mCurrentJoint.size(); i++){
                    Log.d("Trait" + position, "value:" + mCurrentJoint.get(i).getAngle());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayout trait = (LinearLayout) rootView.findViewById(R.id.skeleton_trait_panel);
        trait.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mTrait1 = (TextView) rootView.findViewById(R.id.skeleton_trait1_text);
        mTrait1.setText(traits[CURRENT_JOINT][0]);
        mSeek1 = (SeekBar) rootView.findViewById(R.id.skeleton_trait1_slider);
        mSeek1.setMax(mSkeleton.getJoints().get(0).get(0).getMidAngle());
        mSeek1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                JointAngle joint = mCurrentJoint.get(0);
                Log.d("joint 1", "before angle" + joint.getAngle());
                Log.d("joint 1", "progress" + progress);
                joint.updatePrevAngle();
                joint.setAngle(progress + joint.getMinAngle());
                Log.d("joint 1", "after angle" + joint.getAngle());
                mCount1.setText(Integer.toString(joint.getAngle()));
                if(joint.getAngle() < 0)
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getNegativePoseDirection());
                else
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getPositivePoseDirection());
                setForce();
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
        mSeek2.setMax(mSkeleton.getJoints().get(0).get(1).getMidAngle());
        mSeek2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //mForce -= mSkeleton.getJoints()[CURRENT_JOINT][1];
                JointAngle joint = mCurrentJoint.get(1);
                joint.updatePrevAngle();
                joint.setAngle(progress + joint.getMinAngle());
                mCount2.setText(Integer.toString(joint.getAngle()));
                if(joint.getAngle() < 0)
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getNegativePoseDirection());
                else
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getPositivePoseDirection());
                setForce();
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
        mSeek3.setMax(mSkeleton.getJoints().get(0).get(2).getMidAngle());
        mSeek3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //mForce -= mSkeleton.getJoints().get()[CURRENT_JOINT][2];
                JointAngle joint = mCurrentJoint.get(2);
                joint.updatePrevAngle();
                joint.setAngle(progress + joint.getMinAngle());
                mCount3.setText(Integer.toString(joint.getAngle()));
                if(joint.getAngle() < 0)
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getNegativePoseDirection());
                else
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getPositivePoseDirection());
                setForce();
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
        //=mSeek4.setMax(mSkeleton.getJoints().get(0).get(3).getMidAngle());
        mSeek4.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //mForce -= mSkeleton.getJoints()[CURRENT_JOINT][3];
                JointAngle joint = mCurrentJoint.get(3);
                joint.updatePrevAngle();
                joint.setAngle(progress + joint.getMinAngle());
                mCount4.setText(Integer.toString(joint.getAngle()));
                if(joint.getAngle() < 0)
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getNegativePoseDirection());
                else
                    applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                            - joint.getPrevAngle(), joint.getPositivePoseDirection());
                setForce();
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
                setForce();
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
                setForce();
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
                setForce();
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
        setForce();
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
                        mSkeleton = s.copy();
                        //UpdateControls();
                    }
                });
                if (mSkeletons.size() == 0){
                    //new LoadAllSkeletons().execute();
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
            case R.id.reset_skeleton:
                mSkeleton = new Skeleton();
                applyAllRotations();
        }
        return super.onOptionsItemSelected(item);
    }


    private void applyAllRotations(){
        for (ArrayList<JointAngle> aj : mSkeleton.getJoints().values()){
            for (JointAngle joint : aj){
                applyRotation(joint.getId(), joint.getRotation(), joint.getAngle()
                        - joint.getPrevAngle(), joint.getNegativePoseDirection());
            }
        }
    }

    private void setVisible(){
        mTrait3.setVisibility(View.VISIBLE);
        mTrait4.setVisibility(View.VISIBLE);
        mSeek3.setVisibility(View.VISIBLE);
        mSeek4.setVisibility(View.VISIBLE);
        mCount3.setVisibility(View.VISIBLE);
        mCount4.setVisibility(View.VISIBLE);

    }
    /*private void UpdateControls() {
        mWeight.setText("User Weight: kg");
        mWeightSeek.setProgress(mSkeleton.getWeight());
        mWeightEdit.setText(Integer.toString(mSkeleton.getWeight()));

        mHeight.setText("User Height: cm");
        mHeightSeek.setProgress(mSkeleton.getHeight());
        mHeightEdit.setText(Integer.toString(mSkeleton.getHeight()));

        mBox.setText("Box Weight: kg");
        mBoxSeek.setProgress(mSkeleton.getBoxWeight());
        mBoxEdit.setText(Integer.toString(mSkeleton.getBoxWeight()));

        ArrayList<JointAngle> joint = mSkeleton.getJoints().get(CURRENT_JOINT);

        mSeek1.setMax(joint.get(0).getMidAngle());
        mSeek1.setProgress(joint.get(0).getAngle() + joint.get(0).getMinAngle());
        mCount1.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][0]));
        mSeek2.setProgress(mSkeleton.getJoints()[CURRENT_JOINT][1] + SKELETON_MID);
        mCount2.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][1]));
        mSeek3.setProgress(mSkeleton.getJoints()[CURRENT_JOINT][2] + SKELETON_MID);
        mCount3.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][2]));
        mSeek4.setProgress(mSkeleton.getJoints()[CURRENT_JOINT][3] + SKELETON_MID);
        mCount4.setText(Integer.toString(mSkeleton.getJoints()[CURRENT_JOINT][3]));
        METRIC = true;
    }*/

    private void addSkeleton() {
        try {
            Resources res = getResources();
            skeleton = BonesIO.loadGroup(res.openRawResource(R.raw.skeleton02));

            skeleton.setSkeletonPose(new SkeletonPose(skeleton.get(0).getSkeleton()));
            frontView();
            world.addObject(skeleton.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void rightSagittalView() {
        skeleton.getRoot().clearRotation();
        skeleton.getRoot().clearTranslation();
        skeleton.getRoot().translate(-0.1f, 0.95f, 18.f);
        skeleton.getRoot().rotateAxis(new SimpleVector(0, 0, 1), (float) Math.PI);
        skeleton.getRoot().rotateAxis(new SimpleVector(0, 1, 0), (float) -Math.PI/2);
        skeleton.getRoot().translate(0, 0, 0);
    }

    private void leftSagittalView() {
        skeleton.getRoot().clearRotation();
        skeleton.getRoot().clearTranslation();
        skeleton.getRoot().translate(0f, 0.95f, 18f);
        skeleton.getRoot().rotateAxis(new SimpleVector(0, 0, 1), (float) Math.PI);
        skeleton.getRoot().rotateAxis(new SimpleVector(0, 1, 0), (float) Math.PI/2);
        skeleton.getRoot().translate(0, 0, 0);
    }

    private void frontView() {
        skeleton.getRoot().clearRotation();
        skeleton.getRoot().clearTranslation();
        skeleton.getRoot().translate(0f, 0.95f, 18f);
        skeleton.getRoot().rotateAxis(new SimpleVector(0, 0, 1), (float) Math.PI);
        skeleton.getRoot().rotateAxis(new SimpleVector(0, 1, 0), 0);
        skeleton.getRoot().translate(0, 0, 0);
    }

    private void applyRotation(int id, Matrix rotation, int angle, SimpleVector poseDirection) {
        SkeletonPose pose = skeleton.get(0).getSkeletonPose();
        rotateJoint(pose, rotation, id, poseDirection, (float) Math.toRadians(angle), 1f);

        pose.updateTransforms();
        skeleton.applySkeletonPose();
        skeleton.applyAnimation();
    }

    private void rotateJoint(SkeletonPose pose, Matrix rotation, int jointIndex, SimpleVector bindPoseDirection, float angle, final float targetStrength) {
        final int parentIndex = pose.getSkeleton().getJoint(jointIndex).getParentIndex();

        // neckBindGlobalTransform is the neck bone -> model space transform. essentially, it is the world transform of
        // the neck bone in bind pose.
        final Matrix jointInverseBindPose = pose.getSkeleton().getJoint(jointIndex).getInverseBindPose();
        final Matrix jointBindPose = jointInverseBindPose.invert();


        // Get a vector representing forward direction in neck space, use inverse to take from world -> neck space.
        SimpleVector forwardDirection = new SimpleVector(bindPoseDirection);
        forwardDirection.rotate(jointInverseBindPose);

        // Calculate a rotation to go from one direction to the other and set that rotation on a blank transform.
        Quaternion quat = new Quaternion();
        rotation.rotateAxis(bindPoseDirection, angle);
        quat.rotate(rotation);

        final Matrix subGlobal = quat.getRotationMatrix();

        // now remove the global/world transform of the neck's parent bone, leaving us with just the local transform of
        // neck + rotation.
        subGlobal.matMul(jointBindPose);
        subGlobal.matMul(pose.getSkeleton().getJoint(parentIndex).getInverseBindPose());

        // set that as the neck's transform
        pose.getLocal(jointIndex).setTo(subGlobal);
    }

    private void setForce(){
        FORCE = mSkeleton.calculateForce(skeleton);
        if (FORCE < 3400){
            mTotalForce.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            mTotalForce.setText("Ok");
        }
        else{
            mTotalForce.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            mTotalForce.setText("Too Heavy");
        }
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

    /*class LoadAllSkeletons extends AsyncTask<String, String, Void> {

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
            ArrayList<NameValuePair> params = new ArrayList<>();
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
                    Log.d("loaded", description);

                    int[] t = JointsFromString(trunk);
                    int[] s = JointsFromString(shoulder);
                    int[] n = JointsFromString(neck);
                    int[] e = JointsFromString(elbow);
                    Skeleton skel = new Skeleton(t[0], t[1], t[2], s[0], s[1], s[2], s[3], n[0],
                            n[1], n[2], e[0], e[1],
                            Integer.parseInt(user[0]), Integer.parseInt(user[1]),
                            Integer.parseInt(user[2]), title, description, df.parse(created_at));
                    Log.d("loaded", skel.getTitle() + " " + skel.getDescription());
                    mSkeletons.add(skel);
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
    }*/

    private int[] JointsFromString (String initial){
        String[] broken = initial.split(",");
        int[] output = new int[4];
        for (int i = 0; i < 4; i++){
            output[i] = Integer.parseInt(broken[i]);
        }
        return output;
    }

    class MyRenderer implements GLSurfaceView.Renderer {
        private int fps = 0;
        private int lfps = 0;

        private long fpsTime = System.currentTimeMillis();

        public MyRenderer() {
            Config.maxPolysVisible = 5000;
            Config.farPlane = 1500;
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            if (frameBuffer != null) {
                frameBuffer.dispose();
            }

            frameBuffer = new FrameBuffer(gl, w, h);

            cameraController.placeCamera();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            TextureManager.getInstance().flush();
            Resources res = getResources();

            Texture texture = new Texture(res.openRawResource(R.raw.skeleton_texture));
            //texture.keepPixelData(true);
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

            frameBuffer.clear(new RGBColor(255, 255, 255, 255));

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
                    box[1] = Math.min(box[1], skinBB[1]);
                    // y
                    box[2] = Math.min(box[2], skinBB[2]);
                    box[3] = Math.min(box[3], skinBB[3]);
                    // z
                    box[4] = Math.min(box[4], skinBB[4]);
                    box[5] = Math.min(box[5], skinBB[5]);
                }
            }
            return box;
        }
    }
}
