package mbds.ht.nytimesearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import mbds.ht.nytimesearch.R;
import mbds.ht.nytimesearch.models.Article;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Article article = getIntent().getExtras().getParcelable("article");
        Toast.makeText(this, "Hi, " +  article.getWebUrl(), Toast.LENGTH_SHORT).show();
        if (article != null) {

            //final String url = getIntent().getStringExtra("url");
            final WebView wwArticle = (WebView) findViewById(R.id.wwArticle);
            wwArticle.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            wwArticle.loadUrl(article.getWebUrl());
        }

    }
}
