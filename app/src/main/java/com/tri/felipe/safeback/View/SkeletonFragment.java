package com.tri.felipe.safeback.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.tri.felipe.safeback.Model.JointAngle;
import com.tri.felipe.safeback.Model.Skeleton;
import com.tri.felipe.safeback.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;
import raft.jpct.bones.Quaternion;
import raft.jpct.bones.SkeletonPose;


public class SkeletonFragment extends Fragment {//implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final int PICK_SKELETON_REQUEST = 1;
    static final String LOAD = "LOAD_SAVED";
    private static final int GRANULARITY = 25;
    private static String KEY_PREF_UNIT_TYPE = "prefUnitType";
    private static String IS_METRIC = "Metric";
    private static int[] MAX_STAT = {150, 200};
    private final MyRenderer mRenderer = new MyRenderer();
    private SkeletonController control;
    //3D Skeleton
    private GLSurfaceView mGLView;
    private World world = null;
    private CameraOrbitController cameraController;
    private AnimatedGroup skeleton = null;
    private long frameTime = System.currentTimeMillis();
    private long aggregatedTime = 0;
    private FrameBuffer frameBuffer = null;
    private int FRONT_VIEW = 0;
    //Views
    private Button mJoint;
    private TextView mBottomText;
    private ImageButton mExpandButton;
    private RelativeLayout mUserPanel;
    private EditText mSkeletonName;
    private EditText mSkeletonDescription;
    private AlertDialog.Builder SkeletonDialog;
    private TextView mTotalForce;
    private RelativeLayout mSkeletonLayout;
    private ArrayList<TextView> mTraitTexts;
    private ArrayList<SeekBar> mSeekBars;
    private ArrayList<TextView> mCountTexts;
    private ArrayList<TextView> mUserText;
    private ArrayList<SeekBar> mUserSeek;
    private ArrayList<EditText> mUserEdit;
    //if user panel is expanded or not
    private boolean USER_EXPANDED = false;
    //true: Metric, false: Imperial
    private boolean METRIC;
    //current joint being shown
    private int CURRENT_JOINT = 0;
    private int NUM_JOINTS = 4;
    private float FORCE;
    private Skeleton mSkeleton;
    private ArrayList<JointAngle> mCurrentJoint;
    private ArrayList<Skeleton> mSkeletons;
    private String[] LABELS = {"Neck", "Shoulder", "Trunk", "Elbow"};
    private String[][] TRAITS = {
                    {"Neck Axial Twist", "Neck Lateral Bending", "Neck Extension"},
                    {"L Shoulder Abduction", "R Shoulder Abduction",
                    "L Shoulder  Extension", "R Shoulder Extenson"},
                    {"Trunk Rotation", "Trunk Lateral Bending", "Trunk Extension"},
                    {"L Elbow Extension", "R Elbow Extension"}};
    private String[] USER_TRAITS = {"User Weight: ", "User Height: ", "Item Weight: "};
    private String[][] UNITS = {{"lbs", "kg"}, {"Inches", "cm"}, {"lbs", "kg"}};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        control = SkeletonController.get(getActivity());
        mSkeletons = control.get(getActivity()).getSkeletons();
        new LoadAllSkeletons().execute();
        Log.d("Skeletons loaded", Integer.toString(mSkeletons.size()));
        mSkeleton = new Skeleton();
        setHasOptionsMenu(true);
        METRIC = getUnitType();
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

        //mGLView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tri_splash));

        float[] boundingBox = mRenderer.calcBoundingBox();
        float height = (boundingBox[3]); // ninja height
        new Light(world).setPosition(new SimpleVector(0, -height/2, height));

        cameraController = new CameraOrbitController(world.getCamera());
        cameraController.cameraAngle = 0;
        cameraController.placeCamera();
        applyAllRotations();

        /*
         * Controls text in top left corner, indicating wheather the force is within
         * acceptable limits or not
         */
        mCurrentJoint = mSkeleton.getJoints().get(CURRENT_JOINT);
        mTotalForce = (TextView) rootView.findViewById(R.id.total_force);
        mTotalForce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (METRIC) {
                    Toast.makeText(getActivity(),
                            String.format("%.0f Kgs of force are being applied to the lower back", FORCE * 0.10197),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),
                            String.format("%.0f Lbs of force are being applied to the lower back", FORCE * 0.22481),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        /*
         * Identifies text and pannel at the screen indicating user info panel
         */
        mBottomText = (TextView) rootView.findViewById(R.id.skeleton_user_info);

        /*
         * Trait selector, logging values between trait switches and setting
         * correct sliders and information to be shown
         */
        mJoint = (Button) rootView.findViewById(R.id.skeleton_joint_spinner);
        mJoint.setText(LABELS[CURRENT_JOINT]);
        mJoint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_JOINT < 3)
                    CURRENT_JOINT++;
                else
                    CURRENT_JOINT = 0;
                mJoint.setText(LABELS[CURRENT_JOINT]);
                ShowAllSlider();
                updateSkeletonControls();
            }
        });

        /*
         * Captures all touch events so that they don't go through the expanded user
         * panel or skeleton panel
         */
        RelativeLayout user = (RelativeLayout) rootView.findViewById(R.id.expanded_user_panel);
        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayout trait = (LinearLayout) rootView.findViewById(R.id.skeleton_trait_panel);
        trait.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        initializeSkeletonControls(rootView);
        initializeUserControls(rootView);

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
            }
        });
        CalculateForce();
        return rootView;
    }

    private void initializeUserControls(View rootView) {
        mUserText = new ArrayList<>();
        mUserText.add((TextView) rootView.findViewById(R.id.user_weight_text));
        mUserText.add((TextView) rootView.findViewById(R.id.user_height_text));
        mUserText.add((TextView) rootView.findViewById(R.id.box_weight_text));

        mUserSeek = new ArrayList<>();
        mUserSeek.add((SeekBar) rootView.findViewById(R.id.user_weight_slider));
        mUserSeek.add((SeekBar) rootView.findViewById(R.id.user_height_slider));
        mUserSeek.add((SeekBar) rootView.findViewById(R.id.box_weight_slider));

        mUserEdit = new ArrayList<>();
        mUserEdit.add((EditText) rootView.findViewById(R.id.user_weight_EditText));
        mUserEdit.add((EditText) rootView.findViewById(R.id.user_height_EditText));
        mUserEdit.add((EditText) rootView.findViewById(R.id.box_weight_EditText));

        for (int i = 0; i < 3; i++){
            mUserText.get(i).setText(USER_TRAITS[i] + UNITS[i][METRIC? 1 : 0]);
            mUserSeek.get(i).setMax(MAX_STAT[i % 2]);
            mUserSeek.get(i).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int i = 0;
                    while (!seekBar.equals(mUserSeek.get(i)))
                        i++;
                    mSkeleton.setUserStat(progress, i);
                    if (METRIC) {
                        mUserEdit.get(i).setText(Integer.toString(
                                Math.min(progress, MAX_STAT[i % 2])));
                    } else {
                        if (i % 2 == 1)
                            mUserEdit.get(i).setText((Integer.toString(control.
                                    CmToInch(Math.min(progress, MAX_STAT[i % 2])))));
                        else
                            mUserEdit.get(i).setText(Integer.toString(control.
                                    KiloToPound(Math.min(progress, MAX_STAT[i % 2]))));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    CalculateForce();
                }
            });
            mUserEdit.get(i).setText("0");
            mUserEdit.get(i).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    int i = 0;
                    while (!v.equals(mUserEdit.get(i)))
                            i++;
                    if (METRIC) {
                        mUserSeek.get(i).setProgress(Math.min((Integer.parseInt(
                                mUserEdit.get(i).getText().toString())), MAX_STAT[i % 2]));
                    }
                    else{
                        if (i % 2 ==1){
                            mUserSeek.get(i).setProgress(Math.min(
                                    control.InchToCm(Integer.parseInt(
                                            mUserEdit.get(i).getText().toString())), MAX_STAT[i % 2]));
                        }
                        mUserSeek.get(i).setProgress(Math.min(
                                control.PoundToKilo(Integer.parseInt(
                                        mUserEdit.get(i).getText().toString())), MAX_STAT[i % 2]));
                    }
                    CalculateForce();
                }
            });
        }
    }

    private void initializeSkeletonControls(View rootView) {
        mTraitTexts = new ArrayList<>();
        mTraitTexts.add(((TextView) rootView.findViewById(R.id.skeleton_trait1_text)));
        mTraitTexts.add(((TextView) rootView.findViewById(R.id.skeleton_trait2_text)));
        mTraitTexts.add(((TextView) rootView.findViewById(R.id.skeleton_trait3_text)));
        mTraitTexts.add(((TextView) rootView.findViewById(R.id.skeleton_trait4_text)));

        mSeekBars = new ArrayList<>();
        mSeekBars.add((SeekBar) rootView.findViewById(R.id.skeleton_trait1_slider));
        mSeekBars.add((SeekBar) rootView.findViewById(R.id.skeleton_trait2_slider));
        mSeekBars.add((SeekBar) rootView.findViewById(R.id.skeleton_trait3_slider));
        mSeekBars.add((SeekBar) rootView.findViewById(R.id.skeleton_trait4_slider));

        mCountTexts = new ArrayList<>();
        mCountTexts.add((TextView) rootView.findViewById(R.id.skeleton_trait1_value));
        mCountTexts.add((TextView) rootView.findViewById(R.id.skeleton_trait2_value));
        mCountTexts.add((TextView) rootView.findViewById(R.id.skeleton_trait3_value));
        mCountTexts.add((TextView) rootView.findViewById(R.id.skeleton_trait4_value));

        for (int i = 0; i < 4; i++){
            SeekBar s = mSeekBars.get(i);
            s.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int i = 0;
                    while (!seekBar.equals(mSeekBars.get(i)))
                        i++;
                    onSeek(progress, i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            }
        updateSkeletonControls();
    }

    private void onSeek(int progress, int position) {
        JointAngle joint = mCurrentJoint.get(position);
        joint.updatePrevAngle();
        joint.setAngle(progress + joint.getMinAngle());
        mCountTexts.get(position).setText(Integer.toString(joint.getAngle()));
        if(joint.getAngle() < 0)
            applyRotation(joint.getId(), joint.getRotationMatrix(), joint.getAngle()
                    - joint.getPrevAngle(), joint.getNegativePoseDirection());
        else
            applyRotation(joint.getId(), joint.getRotationMatrix(), joint.getAngle()
                    - joint.getPrevAngle(), joint.getPositivePoseDirection());
        CalculateForce();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.skeleton_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View view1;
        switch (item.getItemId()) {
            case R.id.action_save:
                view1 = factory.inflate(R.layout.fragment_skeleton_save_dialog, null);
                mSkeletonName = (EditText) view1.findViewById(R.id.skeleton_name_editText);
                mSkeletonDescription = (EditText) view1.findViewById(R.id.skeleton_description_editText);
                SkeletonDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Save Skeleton Configuration").setView(view1).
                        setPositiveButton("Save", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!mSkeletonName.getText().toString().isEmpty() &&
                                        !mSkeletonDescription.getText().toString().isEmpty()) {
                                    Skeleton s = mSkeleton.copy();
                                    s.setTitle(mSkeletonName.getText().toString());
                                    s.setDescription(mSkeletonDescription.getText().toString());
                                    s.setDate(new Date());
                                    mSkeletons.add(s);
                                    control.get(getActivity()).saveAllSkeletons();
                                    Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getActivity(), "Missing title or description", Toast.LENGTH_SHORT).show();
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
            case R.id.settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_load_skeleton:
                Intent loadSkeletonIntent = new Intent(getActivity(), SavedSkeletonActivity.class);
                startActivityForResult(loadSkeletonIntent, PICK_SKELETON_REQUEST);
                break;
            case R.id.reset_skeleton:
                mSkeleton = new Skeleton();
                applyAllRotations();
                updateSkeletonControls();
                updateUserControls();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SKELETON_REQUEST){
            if (resultCode == Activity.RESULT_OK) {
                UUID id = (UUID) data.getSerializableExtra(LOAD);
                mSkeleton = control.getSkeletonByID(id).copy();
                Log.d("skeleton", id.toString());
                Log.d("skeleton", mSkeleton.getTitle());
            } else {
                mSkeleton = new Skeleton();
            }
            //applyAllRotations();
            skeleton.get(0).getSkeletonPose().updateTransforms();
            skeleton.applySkeletonPose();
            skeleton.applyAnimation();
            int temp = CURRENT_JOINT;
            for (CURRENT_JOINT = 0; CURRENT_JOINT < 4; CURRENT_JOINT++) {
                ShowAllSlider();
                updateSkeletonControls();
            }
            CURRENT_JOINT = temp;
            updateSkeletonControls();
            updateUserControls();
        }
    }

    private boolean getUnitType(){
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        Log.d("Prerence", sharedPrefs.getString(KEY_PREF_UNIT_TYPE, "NULL"));
        return IS_METRIC.equals(sharedPrefs.getString(KEY_PREF_UNIT_TYPE, "NULL"));
    }


    private void applyAllRotations(){
        for (ArrayList<JointAngle> aj : mSkeleton.getJoints().values()){
            for (JointAngle joint : aj){
                applyRotation(joint.getId(), joint.getRotationMatrix(), joint.getAngle()
                        - joint.getPrevAngle(), joint.getNegativePoseDirection());
            }
        }
    }

    private void ShowAllSlider(){
        for (int i = 2; i < NUM_JOINTS; i++) {
            mTraitTexts.get(i).setVisibility(View.VISIBLE);
            mSeekBars.get(i).setVisibility(View.VISIBLE);
            mCountTexts.get(i).setVisibility(View.VISIBLE);
        }
    }

    private void updateUserControls(){
        for (int i = 0; i < 3; i++){
            mUserText.get(i).setText(USER_TRAITS[i] + UNITS[i][METRIC ? 1 : 0]);
            mUserSeek.get(i).setProgress(mSkeleton.getUserStat(i));
            if (METRIC) {
                mUserEdit.get(i).setText(Integer.toString((mSkeleton.getUserStat(i))));
            }
            else{
                if (i % 2  == 1)
                    mUserEdit.get(i).setText(Integer.toString(control.
                            CmToInch(mSkeleton.getUserStat(i))));
                else
                    mUserEdit.get(i).setText(Integer.toString(control.
                        KiloToPound(mSkeleton.getUserStat(i))));
            }
        }
    }

    /**
     * Updates the skeleton controls setting and hiding/showing the appropriate sliders
     */
    private void updateSkeletonControls() {
        mCurrentJoint = mSkeleton.getJoints().get(CURRENT_JOINT);
        for(int i = 0; i < NUM_JOINTS; i++){
            if ((CURRENT_JOINT == 1) || ((CURRENT_JOINT == 0 || CURRENT_JOINT == 2) && i < 3)
                    || (CURRENT_JOINT == 3 && i < 2)) {
                //Log.d("Testing", Integer.toString(i));
                mTraitTexts.get(i).setText(TRAITS[CURRENT_JOINT][i]);
                //logSkeletonStatus();
                mSeekBars.get(i).setProgress(mCurrentJoint.get(i).getAngle() -
                        mCurrentJoint.get(i).getMinAngle());
                //logSkeletonStatus();
                mSeekBars.get(i).setMax(mCurrentJoint.get(i).getMidAngle());
                mCountTexts.get(i).setText(Integer.toString(mCurrentJoint.get(i).getAngle()));
                //logSkeletonStatus();
            }
            else {
                mTraitTexts.get(i).setVisibility(View.INVISIBLE);
                mSeekBars.get(i).setVisibility(View.INVISIBLE);
                mCountTexts.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Adds the skeleton into the GLView
     */
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
        skeleton.getRoot().translate(0f, 0.95f, 18f);
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
        rotateJoint(pose, rotation, id, poseDirection, (float) Math.toRadians(angle));

        pose.updateTransforms();
        skeleton.applySkeletonPose();
        skeleton.applyAnimation();
    }

    private void rotateJoint(SkeletonPose pose, Matrix rotation, int jointIndex, SimpleVector bindPoseDirection, float angle) {
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

    /**
     * Given the current configuration calculates and presents the force being applied onto lower
     * back.
     */
    private void CalculateForce() {
        FORCE = mSkeleton.calculateForce(skeleton);
        if (FORCE < 3400){
            mTotalForce.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            mTotalForce.setText("Load is Safe");
        }
        else{
            mTotalForce.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            mTotalForce.setText("Load is Unsafe");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        METRIC = getUnitType();
        updateUserControls();
    }

    class LoadAllSkeletons extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... args) {
            try {
                control.get(getActivity()).loadAllSkeletons();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPreExecute();
        }
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
            if (frameBuffer != null)
                frameBuffer.dispose();

            frameBuffer = new FrameBuffer(gl, w, h);
            cameraController.placeCamera();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            TextureManager.getInstance().flush();

            Texture texture = new Texture(getResources().openRawResource(R.raw.skeleton_texture));
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

            if (aggregatedTime > 1000)
                aggregatedTime = 0;

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

        /* calculates and returns whole bounding box of skinned group */
        protected float[] calcBoundingBox() {
            float[] box = null;

            for (Animated3D skin : skeleton) {
                float[] skinBB = skin.getMesh().getBoundingBox();

                if (box == null)
                    box = skinBB;
                else {
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
