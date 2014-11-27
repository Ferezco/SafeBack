package com.tri.felipe.safeback.View.Training;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.tri.felipe.safeback.Controller.TrainingController;
import com.tri.felipe.safeback.Model.Training;
import com.tri.felipe.safeback.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Felipe on 14-10-20.
 */
public class TrainingPagerActivity extends FragmentActivity{
    private ViewPager mViewPager;
    private ArrayList<Training> mTrainings;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        setTitle(R.string.info_list_title);
        getActionBar().setHomeButtonEnabled(true);

        mTrainings = TrainingController.get(this).getTrainings();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm){
            @Override
            public Fragment getItem(int position) {
                Training crime = mTrainings.get(position);
                return TrainingFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mTrainings.size();
            }
        });

        UUID crimeId = (UUID)getIntent()
                .getSerializableExtra(TrainingFragment.EXTRA_SINGLE_ID);
        for (int i = 0; i <mTrainings.size(); i++){
            if (mTrainings.get(i).getId().equals((crimeId))){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}