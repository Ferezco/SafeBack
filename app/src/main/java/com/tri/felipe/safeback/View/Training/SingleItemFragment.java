package com.tri.felipe.safeback.View.Training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tri.felipe.safeback.Controller.TrainingController;
import com.tri.felipe.safeback.Model.Training;
import com.tri.felipe.safeback.R;

import java.util.UUID;

/**
 * Created by Felipe on 14-10-20.
 */
public class SingleItemFragment extends Fragment {

    private Training mTraining;
    private ImageView mHeaderImage;
    private TextView mTitle;
    private TextView mDate;
    private TextView mDescription;
    public static final String EXTRA_SINGLE_ID =
            "com.tri.felipe.safeback.single_id";

    public static SingleItemFragment newInstance(UUID trainingId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SINGLE_ID, trainingId);

        SingleItemFragment fragment = new SingleItemFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         UUID trainingID = (UUID) getArguments().getSerializable(EXTRA_SINGLE_ID);
        mTraining = TrainingController.get(getActivity()).getTraining(trainingID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_training, container, false);
        mHeaderImage = (ImageView) rootView.findViewById(R.id.training_photo);
        mHeaderImage.setImageResource(R.drawable.header_imgs);
        if (mTraining.getType() == 0){
            mHeaderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), SingleImageActivity.class);
                    i.putExtra(SingleItemFragment.EXTRA_SINGLE_ID, mTraining.getId());
                    startActivity(i);
                }
            });
        }
        mTitle = (TextView) rootView.findViewById(R.id.training_title);
        mTitle.setText(mTraining.getTitle());

        mDate = (TextView) rootView.findViewById(R.id.training_date);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        mDate.setText(df.format("EEEE, LLLL d, y", mTraining.getDate()));

        mDescription = (TextView) rootView.findViewById(R.id.training_description);
        mDescription.setText(mTraining.getDescription());

        return rootView;

    }
}