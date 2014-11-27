package com.tri.felipe.safeback.View.Article;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tri.felipe.safeback.Controller.ArticleController;
import com.tri.felipe.safeback.Controller.TrainingController;
import com.tri.felipe.safeback.Model.Article;
import com.tri.felipe.safeback.Model.Training;
import com.tri.felipe.safeback.R;
import com.tri.felipe.safeback.View.Training.SingleImageActivity;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-17.
 */
public class ArticleActivity extends Activity {
    private Article mArticle;
    private ImageView mHeaderImage;
    private TextView mTitle;
    private TextView mDate;
    private TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_training);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.app_name);
        UUID articleId = (UUID) getIntent()
                .getSerializableExtra(ArticleFragment.EXTRA_SINGLE_ID);
        mArticle = ArticleController.get(this).getArticle(articleId);
        mHeaderImage = (ImageView) findViewById(R.id.training_photo);
        mHeaderImage.setImageResource(R.drawable.header_img2);

        mTitle = (TextView) findViewById(R.id.training_title);
        mTitle.setText(mArticle.getTitle());

        mDate = (TextView) findViewById(R.id.training_date);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        mDate.setText(df.format("EEEE, LLLL d, y", mArticle.getDate()));

        mDescription = (TextView) findViewById(R.id.training_description);
        mDescription.setText(mArticle.getDescription());
    }
}