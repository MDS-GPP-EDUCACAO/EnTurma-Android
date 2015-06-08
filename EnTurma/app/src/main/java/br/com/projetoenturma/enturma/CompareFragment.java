package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rest.request.RESTFull;

public class CompareFragment extends Fragment {


    Spinner firstYearSpinner, firstStateSpinner, firstNetworkSpinner,firstPublicTypeSpinner, firstLocalSpinner;
    Spinner secondYearSpinner, secondStateSpinner, secondNetworkSpinner,secondPublicTypeSpinner, secondLocalSpinner;
    Spinner gradeSpinner;
    Button requestButton;
    ProgressDialog activityIdicator;
    TextView graphTitle;
    PagerSlidingTabStrip tabsStrip;
    ViewPager viewPager;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompareFragment newInstance(int sectionNumber) {
        CompareFragment fragment = new CompareFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CompareFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compare, container, false);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    @Override
    public void onStart() {
        super.onStart();
        firstYearSpinner = (Spinner) getView().findViewById(R.id.first_year);
        firstStateSpinner = (Spinner) getView().findViewById(R.id.first_state);
        firstNetworkSpinner = (Spinner) getView().findViewById(R.id.first_network);
        firstPublicTypeSpinner = (Spinner) getView().findViewById(R.id.first_public_type);
        firstLocalSpinner = (Spinner) getView().findViewById(R.id.first_local);

        secondYearSpinner = (Spinner) getView().findViewById(R.id.second_year);
        secondStateSpinner = (Spinner) getView().findViewById(R.id.second_state);
        secondNetworkSpinner = (Spinner) getView().findViewById(R.id.second_network);
        secondPublicTypeSpinner = (Spinner) getView().findViewById(R.id.second_public_type);
        secondLocalSpinner = (Spinner) getView().findViewById(R.id.second_local);

        gradeSpinner = (Spinner) getView().findViewById(R.id.grade);


        requestButton = (Button) getView().findViewById(R.id.send_report);

        setupActions();

        activityIdicator = new ProgressDialog(getActivity());
        activityIdicator.setMessage("Procurando pelo relatório");
        activityIdicator.setCancelable(false);

        graphTitle = (TextView) getView().findViewById(R.id.graph_title);

        setupTabPageViewer();


    }

    private void setupTabPageViewer(){

        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        viewPager.setAdapter(new GraphsFragmentPagerAdapter(getActivity().getSupportFragmentManager()));
        // Give the PagerSlidingTabStrip the ViewPager
        tabsStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
        graphTitle.setText( "Plotar IDEB ");


        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

                switch (position) {

                    case 0:
                        graphTitle.setText("Plotar IDEB ");

                        break;
                    case 1:
                        graphTitle.setText("Plotar Evasão ");

                        break;
                    case 2:
                        graphTitle.setText("Plotar Rendimento ");

                        break;
                    case 3:
                        graphTitle.setText("Plotar Distorção ");

                        break;
                    default:
                        graphTitle.setText("Plotar IDEB ");

                        break;

                }
            }
            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

    }

    private void setupActions(){
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestData();
            }
        });

        showWhenEqual("Publica", firstPublicTypeSpinner, firstNetworkSpinner);
        showWhenEqual("Publica", secondPublicTypeSpinner, secondNetworkSpinner);
    }

    private void showWhenEqual(final String value, final Spinner show, final Spinner entry){
        entry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (entry.getSelectedItem().toString().equals(value)) {
                    show.setVisibility(View.VISIBLE);
                } else {
                    show.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void requestData(){
        Map<String,String> params = new HashMap();
        params.put("first_year", firstYearSpinner.getSelectedItem().toString());
        params.put("first_state", firstStateSpinner.getSelectedItem().toString());
        params.put("first_test_type", firstNetworkSpinner.getSelectedItem().toString());
        params.put("first_public_type", firstPublicTypeSpinner.getSelectedItem().toString());
        params.put("first_local", firstLocalSpinner.getSelectedItem().toString());
        params.put("second_year", secondYearSpinner.getSelectedItem().toString());
        params.put("second_state", secondStateSpinner.getSelectedItem().toString());
        params.put("second_test_type", secondNetworkSpinner.getSelectedItem().toString());
        params.put("second_public_type", secondPublicTypeSpinner.getSelectedItem().toString());
        params.put("second_local", secondLocalSpinner.getSelectedItem().toString());
        params.put("grade", gradeSpinner.getSelectedItem().toString());

        RESTFull full = new RESTFull(params);

        activityIdicator.show();
        full.requestCompareReport(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                activityIdicator.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), R.string.request_success, Toast.LENGTH_LONG).show();
                //plot graph with response object
                System.out.println(response.toString());
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                activityIdicator.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), R.string.request_failure, Toast.LENGTH_LONG).show();
                Log.d("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }
}

