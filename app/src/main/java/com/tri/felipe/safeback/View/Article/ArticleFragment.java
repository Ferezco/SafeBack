package com.tri.felipe.safeback.View.Article;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tri.felipe.safeback.Controller.ArticleController;
import com.tri.felipe.safeback.Controller.JSONParser;
import com.tri.felipe.safeback.Model.Article;
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
import java.util.Date;


public class ArticleFragment extends Fragment{

    public static final String EXTRA_SINGLE_ID =
            "com.tri.felipe.safeback.single_id";

    private static final String url_articles = "http://104.131.178.112:8000/safeback/article";
    RecyclerView mArticleList;
    ProgressDialog pDialog;

    // JSON
    private static final String TAG_ARTICLE = "fields";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DATE = "date";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_URL = "url";

    JSONObject mArticle = null;
    JSONParser jParser = new JSONParser();

    private ArrayList<Article> mArticles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticles = ArticleController.get(getActivity()).getArticles();
        if (mArticles.size() == 0) {
            new LoadArticles().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);
        mArticleList = (RecyclerView) rootView.findViewById(R.id.article_list);
        mArticleList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mArticleList.setLayoutManager(llm);
        ArticleAdapter aa = new ArticleAdapter(ArticleController.get(getActivity()).getArticles());
        mArticleList.setAdapter(aa);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavigationActivity) activity).onSectionAttached(1);
    }


    private class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

        private ArrayList<Article> articleList;

        public ArticleAdapter(ArrayList<Article> articleList){
            this.articleList = articleList;
        }

        @Override
        public void onBindViewHolder(ArticleViewHolder articleViewHolder, int i) {
            final Article article = articleList.get(i);
            articleViewHolder.mTitle.setText(article.getTitle());
            if (i %2 == 0) {
                articleViewHolder.mImage.setBackgroundResource(R.drawable.header_img3);
            }else {
                articleViewHolder.mImage.setBackgroundResource(R.drawable.header_img2);
            }
            articleViewHolder.mBody.setText(article.getDescription().substring(0,175) + "...");
            if (!article.getUrl().equals("N/A")){
                articleViewHolder.mShare.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),"sharing is caring", Toast.LENGTH_SHORT).show();
                    }
                });
            } else{
                articleViewHolder.mShare.setVisibility(View.GONE);
                articleViewHolder.mExpand.setGravity(Gravity.LEFT);

            }

            articleViewHolder.mExpand.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ArticleActivity.class);
                    i.putExtra(ArticleFragment.EXTRA_SINGLE_ID, article.getId());
                    startActivity(i);
                }
            });
        }
        @Override
        public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_article, viewGroup, false);

            return new ArticleViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return articleList.size();
        }

        public class ArticleViewHolder extends RecyclerView.ViewHolder{
            protected FrameLayout mImage;
            protected TextView mTitle;
            protected TextView mBody;
            protected TextView mShare;
            protected TextView mExpand;

            public ArticleViewHolder(View itemView) {
                super(itemView);
                mImage  = (FrameLayout) itemView.findViewById(R.id.card_header_image);
                mTitle = (TextView) itemView.findViewById(R.id.article_title);
                mBody = (TextView) itemView.findViewById(R.id.article_text_preview);
                mShare = (TextView) itemView.findViewById(R.id.article_share_button);
                mExpand = (TextView) itemView.findViewById(R.id.article_expand_button);

            }
        }
    }
    class LoadArticles extends AsyncTask<String, String, Void> {
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
            JSONArray json = jParser.makeHttpRequest(url_articles, "GET", params);
            Log.d("All training:", json.toString());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            try{
                for (int i = 0; i < json.length(); i++){
                    mArticle = json.getJSONObject(i).getJSONObject(TAG_ARTICLE);
                    Log.d("single training:", mArticles.toString());

                    String title = mArticle.getString(TAG_TITLE);
                    String created_at = mArticle.getString(TAG_DATE);
                    String description = mArticle.getString(TAG_DESCRIPTION);
                    String url = mArticle.getString(TAG_URL);

                    Article a = new Article(title, description, url, df.parse(created_at));
                    mArticles.add(a);

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
            ArticleAdapter adapter = new ArticleAdapter(mArticles);
            mArticleList.setAdapter(adapter);
        }
    }
}

