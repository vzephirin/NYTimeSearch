package mbds.ht.nytimesearch.activities;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import mbds.ht.nytimesearch.R;
import mbds.ht.nytimesearch.models.QueryClass;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterDialogFragment extends DialogFragment {
    private Button btOk;
    private Spinner mSort;
    private TextView edit;
    CheckBox fashion;
    CheckBox sport;
    CheckBox arts;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    QueryClass queryMap;
    CompoundButton.OnCheckedChangeListener checkListener;

    public FilterDialogFragment() {
        // Required empty public constructor
    }


    public static FilterDialogFragment newInstance(int title) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;


    }
    public interface EditCustomDialogListener {
        void onFinishEditDialog(QueryClass _queryclass);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        myCalendar = Calendar.getInstance();

        // mSort = (EditText) view.findViewById(R.id.txtSort);
        mSort = (Spinner) view.findViewById(R.id.txt_spinow);
        btOk = (Button) view.findViewById(R.id.btnSearch);
        edit =(TextView) view.findViewById(R.id.txt_date_begin);
        fashion = (CheckBox)  view.findViewById(R.id.checkbox_FS_ST);
        sport = (CheckBox) view.findViewById(R.id.checkbox_sport);
        arts = (CheckBox) view.findViewById(R.id.checkbox_arts);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




        // Fetch arguments from bundle and set title
       // String title = getArguments().getString("title", "Enter Name");
       // getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        //   mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> arr=new ArrayList<>();
                EditCustomDialogListener listener = (EditCustomDialogListener) getActivity();

                String textSearch= String.valueOf(mSort.getSelectedItem());
               // queryMap.put("sort",textSearch);
                if (arts.isChecked()){
                    arr.add("Arts");

                   // queryMap.put("Arts","Arts");
                }
                if (sport.isChecked()){
                   // queryMap.put("Sports","Sports");
                    arr.add("Sports");
                }
                if (fashion.isChecked()){
                    //queryMap.put("FS","Fashion , Style");
                    arr.add("Fashion");
                    arr.add("Style");
                }
                SimpleDateFormat sdfquery = new SimpleDateFormat("yyyyMMdd", Locale.US);
               // queryMap.put("begin_date",sdfquery.format(myCalendar.getTime()));
                QueryClass _queryCl=new QueryClass(sdfquery.format(myCalendar.getTime()),textSearch,arr);
                listener.onFinishEditDialog(_queryCl);
                // Close the dialog and return back to the parent activity
                dismiss();
                //   return true;
            }
        });


// This actually applies the listener to the checkboxes
// by calling `setOnCheckedChangeListener` on each one


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container);
    }
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edit.setText(sdf.format(myCalendar.getTime()));
    }


}