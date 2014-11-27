package com.tri.felipe.safeback.View.Training;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tri.felipe.safeback.Controller.JSONParser;
import com.tri.felipe.safeback.Controller.TrainingController;
import com.tri.felipe.safeback.Model.Training;
import com.tri.felipe.safeback.R;
import com.tri.felipe.safeback.View.NavigationActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Felipe on 2014-10-16.
 */
public class TrainingListFragment extends ListFragment {

    private ProgressDialog pDialog;
    private static String url_all_training = "http://104.131.178.112:8000/safeback/training";

    // JSON
    private static final String TAG_TRAINING = "fields";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DATE = "date";
    private static final String TAG_DESCRIPTION = "description";
    JSONObject mTrainings = null;
    JSONParser jParser = new JSONParser();

    private ArrayList<Training> mTraining;

    private static final String TAG = "TrainingListFragment";
    private static final Integer[] iconResource = {R.drawable.ic_action_sort_by_size,
            R.drawable.ic_action_video, R.drawable.ic_action_sort_by_size};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.info_list_title);
        mTraining = TrainingController.get(getActivity()).getTrainings();
        if (mTraining.size() == 0) {
            new LoadAllTraining().execute();
        }
        TrainingAdapter adapter = new TrainingAdapter(mTraining);
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Get the training from the adapter
        Training c = (Training) (getListAdapter().getItem(position));

        //start TrainingPagerActivity
        Intent i = new Intent(getActivity(), TrainingPagerActivity.class);
        i.putExtra(TrainingFragment.EXTRA_SINGLE_ID, c.getId());
        startActivity(i);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((TrainingAdapter) getListAdapter()).notifyDataSetChanged();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationActivity) activity).onSectionAttached(2);
    }


    private class TrainingAdapter extends ArrayAdapter<Training> {

        public TrainingAdapter(ArrayList<Training> trainings) {
            super(getActivity(), 0, trainings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_training, null);
            }

            Training c = getItem(position);

            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.training_list_item_iconImageView);
            iconImageView.setImageResource(iconResource[c.getType()]);

            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.training_list_item_titleTextView);
            titleTextView.setText(c.getTitle());

            TextView descriptionTextView =
                    (TextView) convertView.findViewById(R.id.training_list_item_descriptionTextView);
            descriptionTextView.setText(c.getDescription().substring(0, 40) + "...");

            return convertView;
        }
    }


    class LoadAllTraining extends AsyncTask<String, String, Void> {
        InputStream inputStream = null;
        String result = "";

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
            JSONArray json = jParser.makeHttpRequest(url_all_training, "GET", params);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            Log.d("All training:", json.toString());
            try{
                for (int i = 0; i < json.length(); i++){
                    mTrainings = json.getJSONObject(i).getJSONObject(TAG_TRAINING);
                    Log.d("single training:", mTrainings.toString());
                    String title = mTrainings.getString(TAG_TITLE);
                    Integer category = mTrainings.getInt(TAG_CATEGORY);
                    String created_at = mTrainings.getString(TAG_DATE);
                    Log.d("date:", created_at);
                    String description = mTrainings.getString(TAG_DESCRIPTION);
                    Training t = new Training(title, category, description, df.parse(created_at));
                    mTraining.add(t);
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
            //Collections.sort(mTraining);
            TrainingAdapter adapter = new TrainingAdapter(mTraining);
            setListAdapter(adapter);
        }
    }
}
