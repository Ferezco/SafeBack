package com.tri.felipe.safeback.Controller;

import android.content.Context;

import com.tri.felipe.safeback.Model.Article;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Felipe on 14-11-17.
 */
public class ArticleController {

    private ArrayList<Article> mArticles;
    private static ArticleController sArticleController;
    private Context mAppContext;

    private ArticleController(Context appContext) {
        mAppContext = appContext;
        mArticles = new ArrayList<Article>();

    }

    public static ArticleController get(Context c) {
        if (sArticleController == null) {
            sArticleController = new ArticleController(c.getApplicationContext());
        }
        return sArticleController;
    }

    public ArrayList<Article> getArticles() {
        return mArticles;
    }

    public Article getArticle(UUID id) {
        for (Article a : mArticles) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }
}
