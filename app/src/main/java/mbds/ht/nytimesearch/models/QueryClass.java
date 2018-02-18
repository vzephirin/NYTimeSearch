package mbds.ht.nytimesearch.models;

/**
 * Created by Administrator on 2/18/2018.
 */

import java.util.ArrayList;

/**
 * Created by SAUL on 2/17/2018.
 */

public class QueryClass {

    private String date;
    private String sort;
    private ArrayList<String> deskValue;

    public String getDate() {
        return date;
    }

    public String getSort() {
        return sort;
    }

    public ArrayList<String> getDeskValue() {
        return deskValue;
    }

    public QueryClass(String date, String sort, ArrayList<String> deskValue) {
        this.date = date;
        this.sort = sort;
        this.deskValue = deskValue;
    }
}