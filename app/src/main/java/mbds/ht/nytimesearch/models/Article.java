package mbds.ht.nytimesearch.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2/14/2018.
 */

public class Article implements Parcelable {
    private String webUrl;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    private String headline;
    private String thumbNail;

    public String getSpinset() {
        return spinset;
    }

    private String spinset;
    public Article(JSONObject jsonObject){
        try{this.webUrl=jsonObject.getString("web_url");
            this.spinset=jsonObject.getString("snippet");
        this.headline=jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length()>0) {
                 JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "https://www.nytimes.com/"+multimediaJson.getString("url");
            }
            else{
                this.thumbNail ="";
            }
        }
        catch (JSONException e)
        {e.printStackTrace();}
    }
    public static ArrayList<Article> fromJasonArray(JSONArray array) {
        ArrayList<Article> arrayArticle = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                arrayArticle.add(new Article(array.getJSONObject(i)));
            }
            Log.d("mes","mess");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayArticle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(webUrl);
        parcel.writeString(thumbNail);
        parcel.writeString(headline);
        parcel.writeString(spinset);
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>()
    {
        @Override
        public Article createFromParcel(Parcel source)
        {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size)
        {
            return new Article[size];
        }
    };

    public Article (Parcel in) {
        this.webUrl = in.readString();
        this.thumbNail= in.readString();
        this.headline = in.readString();
        this.spinset= in.readString();

    }
    public Article(){}

}

