package mbds.ht.nytimesearch.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import mbds.ht.nytimesearch.R;
import mbds.ht.nytimesearch.adapters.ArticleArrayAdapter;
import mbds.ht.nytimesearch.adapters.ItemClickListenerInterface;
import mbds.ht.nytimesearch.models.Article;
import mbds.ht.nytimesearch.models.QueryClass;
import mbds.ht.nytimesearch.services.EndlessRecyclerViewScrollListener;

public class SearchActivity extends AppCompatActivity implements ItemClickListenerInterface, FilterDialogFragment.EditCustomDialogListener {
    EditText etSearch;
    Button btnSearch;
    RecyclerView rvResult;
    ArrayList<Article> arrayArticle;
    ArticleArrayAdapter adapter;
    RequestParams params;
    String queryt = null;
    HashMap<String, String> queryference =null;
    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }

    private void setupViews() {
        //etSearch = (EditText) findViewById(R.id.etQuery);
        //btnSearch=(Button) findViewById(R.id.btnSearch);

        rvResult = (RecyclerView) findViewById(R.id.gvResults);
        arrayArticle = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, arrayArticle);
        params = new RequestParams();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvResult.setAdapter(adapter);
        adapter.setClickListener(this);

        //rvResult.setLayoutManager(linearLayoutManager);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvResult.setLayoutManager(gridLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                loadNextDataFromApi(page);

            }
        };
        rvResult.addOnScrollListener(scrollListener);

    }

    public void loadNextDataFromApi(int offset) {
        Toast.makeText(this, "Searching for test " + queryt, Toast.LENGTH_LONG).show();
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        onArticleSearchSroll(queryt, offset);
    }

    public void onArticleSearch(String query, int page) {
        //if (queryference==null){}
        // String query = etSearch.getText().toString();

        if (isOnline()) {
           // Toast.makeText(this, "requete test " + query, Toast.LENGTH_LONG).show();
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

            params.put("api-key", "945373ce2cf842b3a4433c1097bf4840");
            params.put("page", 0);
           if(query!=null) params.put("query", query);
          //  Toast.makeText(this, "Searching for " + params.toString(), Toast.LENGTH_LONG).show();
           /*
            if (queryference!=null) {
                if (queryference.get("desc") != null) params.put("fq", queryference.get("desc"));
                if (queryference.get("begin_date") != null)
                    params.put("begin_date", queryference.get("begin_date"));
                if (queryference.get("sort") != null) params.put("sort", queryference.get("sort"));
            }
            */


            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    Log.d("erreurJson", "test");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        JSONArray articleJSONArray;
                        if (response != null) {
                            // Get the docs json array
                            articleJSONArray = response.getJSONObject("response").getJSONArray("docs");
                            // Parse json array into array of model objects
                            final ArrayList<Article> articles = Article.fromJasonArray(articleJSONArray);

                            // Parse json array into array of model objects
                            // Remove all books from the adapter


                            // Remove all books from the adapter
                            arrayArticle.clear();
                            adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
// 3. Reset endless scroll listener when performing a new search
                            scrollListener.resetState();
                            // Load model objects into the adapter
                            for (Article article : articles) {
                                arrayArticle.add(article); // add book through the adapter
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("Debug", "test");
                        }
                    } catch (JSONException e) {
                        // Invalid JSON format, show appropriate error.t
                        e.printStackTrace();
                    }


                }
            });
        } else {
            Toast.makeText(this, "Mauvaise connection ", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryt = query;
                onArticleSearch(query, 0);
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Log.d("test", "onCreateOptionsMenu: stop ");
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Intent intent = new Intent(this, SettingActivity.class);
            // startActivity(intent);
            FragmentManager fm = getSupportFragmentManager();
            FilterDialogFragment dialogFragment = new FilterDialogFragment();
            dialogFragment.show(fm, "Sample Fragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
       // Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
        Article article = arrayArticle.get(position);
        //intent.putExtra("url", article.getWebUrl());
        //intent.putExtra("article",article);sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_share);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl().toString());
        intent.setType("text/plain");
        int requestCode = 100;

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
// Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent

        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);


        CustomTabsIntent customTabsIntent = builder.build();

        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.addDefaultShareMenuItem();



// and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(this, Uri.parse(article.getWebUrl().toString()));



        //startActivity(intent);

        //startActivity(intent);

    }

    @Override
    public void onFinishEditDialog(QueryClass _queryclass) {
       // Toast.makeText(this, "Hi, " + inputQueqy.toString(), Toast.LENGTH_SHORT).show();
         params=new RequestParams();
        String _param="news_desk:"+ _queryclass.getDeskValue().toString().replace('[','(').replace(']',')');
        //  RequestParams params=new RequestParams();
        if(_queryclass.getDeskValue().toArray().length>0){
            params.put("fq",_param);
        }
        String sort=_queryclass.getSort().toString().toLowerCase();
        params.put("begin_date",_queryclass.getDate().toString());
        params.put("sort",sort);

        onArticleSearch(null,0);
        Log.d("param",_param);
        Log.d("param",_queryclass.getDate().toString());
        Log.d("param",sort);
        queryt=null;
       // params.put("sort",sort););

        Toast.makeText(this, "Hi, " + _param, Toast.LENGTH_SHORT).show();
       // bindingData(params,null);

    }


    public void onArticleSearchSroll(String query, int page) {
        // String query = etSearch.getText().toString();
        if (isOnline()) {
            Toast.makeText(this, "Searching for test" + query, Toast.LENGTH_LONG).show();
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
            params.put("api-key", "945373ce2cf842b3a4433c1097bf4840");
            params.put("page", page);
           // params.put("query", query);
            if (query!=null) params.put("query", query);
            /*if (queryference!=null) {
                if (queryference.get("desc") != null) params.put("fq", queryference.get("desc"));
                if (queryference.get("begin_date") != null)
                    params.put("begin_date", queryference.get("begin_date"));
                if (queryference.get("sort") != null) params.put("sort", queryference.get("sort"));
            }
           */
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    Log.d("erreurJson", "test");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {


                    try {
                        JSONArray articleJSONArray;
                        if (response != null) {
                            // Get the docs json array
                            articleJSONArray = response.getJSONObject("response").getJSONArray("docs");
                            // Parse json array into array of model objects
                            final ArrayList<Article> articles = Article.fromJasonArray(articleJSONArray);

                            // Parse json array into array of model objects
                            // Remove all books from the adapter


                            // Remove all books from the adapter
                            //arrayArticle.clear();
                            // Load model objects into the adapter
                            for (Article article : articles) {
                                arrayArticle.add(article); // add book through the adapter
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("Debug", "test");
                        }
                    } catch (JSONException e) {
                        // Invalid JSON format, show appropriate error.t
                        e.printStackTrace();
                    }


                }
            });

        } else {
            Toast.makeText(this, "Mauvaise connection ", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
       // Toast.makeText(this, "test entries", Toast.LENGTH_SHORT).show();

      /* Runtime runtime = Runtime.getRuntime();
        try {
            Toast.makeText(this, "test ", Toast.LENGTH_SHORT).show();
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

     //   return false;
        ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
    }


