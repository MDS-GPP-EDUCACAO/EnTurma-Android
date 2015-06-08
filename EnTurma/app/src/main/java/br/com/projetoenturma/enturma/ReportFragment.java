package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.jjoe64.graphview.GraphView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import graphs.PlotterManager;
import rest.request.RESTFull;


public class ReportFragment extends Fragment {

    Spinner yearSpinner, stateSpinner, gradeSpinner, networkSpinner,publicTypeSpinner, localSpinner;
    Button sendButton;
    ProgressDialog activityIdicator;
    PagerSlidingTabStrip tabsStrip;
    ViewPager viewPager;
    TextView graphDescription;
    GraphView graph;
    JSONObject reportResponse;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReportFragment newInstance(int sectionNumber) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ReportFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
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
        yearSpinner = (Spinner) getView().findViewById(R.id.year);
        stateSpinner = (Spinner) getView().findViewById(R.id.state);
        gradeSpinner = (Spinner) getView().findViewById(R.id.grade);
        networkSpinner = (Spinner) getView().findViewById(R.id.network);
        publicTypeSpinner = (Spinner) getView().findViewById(R.id.public_type);
        localSpinner = (Spinner) getView().findViewById(R.id.local);
        sendButton = (Button) getView().findViewById(R.id.send_report);


        setupActions();

        activityIdicator = new ProgressDialog(getActivity());
        activityIdicator.setMessage("Procurando pelo relatório");
        activityIdicator.setCancelable(false);


        graph = (GraphView) getView().findViewById(R.id.graph);

        setupTabPageViewer();

        graph.setVisibility(View.INVISIBLE);
        tabsStrip.setVisibility(View.INVISIBLE);
        graphDescription.setVisibility(View.INVISIBLE);


    }

    private void setupTabPageViewer(){
        graphDescription = (TextView) getView().findViewById(R.id.graph_description);

        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        viewPager.setAdapter(new GraphsFragmentPagerAdapter(getActivity().getSupportFragmentManager()));
        // Give the PagerSlidingTabStrip the ViewPager
        tabsStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
        graphDescription.setText("Plotar IDEB ");


        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

                plotData(reportResponse, position);

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

        graph.setVisibility(View.INVISIBLE);
        tabsStrip.setVisibility(View.INVISIBLE);
        graphDescription.setVisibility(View.INVISIBLE);

    }

    private void setupActions() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestData();
            }
        });

        networkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (networkSpinner.getSelectedItem().toString().equals("Publica")) {
                    publicTypeSpinner.setVisibility(View.VISIBLE);
                } else {
                    publicTypeSpinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void requestData(){

        Map<String,String> params = new HashMap();
        params.put("year", yearSpinner.getSelectedItem().toString());
        params.put("state", stateSpinner.getSelectedItem().toString());
        params.put("grade", gradeSpinner.getSelectedItem().toString());
        params.put("test_type", networkSpinner.getSelectedItem().toString());
        params.put("public_type", publicTypeSpinner.getSelectedItem().toString());
        params.put("local", localSpinner.getSelectedItem().toString());

        RESTFull full = new RESTFull(params);

        activityIdicator.show();
        full.requestReport(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                activityIdicator.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Sucesso!", Toast.LENGTH_LONG).show();
                //plot graph with response object
                try {
                    reportResponse = new JSONObject(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(reportResponse.toString());

                plotData(reportResponse, 0);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                activityIdicator.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erro: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }

    private void plotData(JSONObject data, int graphOptionSelected) {

        if (data != null) {

            graph.removeAllSeries();


            JSONObject rates = data.optJSONObject("rates");
            JSONArray dataToPlot = new JSONArray();
            int initialXYear = 0;
            try {
                if (rates.getString("status").equals("available")) {

                    switch (graphOptionSelected) {

                        case 0:
                            graphDescription.setText(R.string.ideb_description);
                            break;
                        case 1:
                            dataToPlot = rates.getJSONArray("evasion");
                            graphDescription.setText(R.string.evasion_description);

                            break;
                        case 2:
                            dataToPlot = rates.getJSONArray("performance");
                            graphDescription.setText(R.string.performance_description);

                            break;
                        case 3:
                            dataToPlot = rates.getJSONArray("distortion");
                            graphDescription.setText(R.string.distortion_description);

                            break;
                        default:
                            break;

                    }

                    initialXYear = Integer.parseInt(data.getString("year"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            PlotterManager manager = new PlotterManager(initialXYear, graph, dataToPlot);



            if (manager.plotSimpleLineGraph()) {
                graph.setVisibility(View.VISIBLE);
                tabsStrip.setVisibility(View.VISIBLE);
                graphDescription.setVisibility(View.VISIBLE);

            }
        }
    }
}
