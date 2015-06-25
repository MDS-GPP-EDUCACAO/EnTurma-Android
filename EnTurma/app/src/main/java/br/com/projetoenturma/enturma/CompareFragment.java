package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ScrollView;
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

public class CompareFragment extends Fragment {


    Spinner firstYearSpinner, firstStateSpinner, firstNetworkSpinner,firstPublicTypeSpinner, firstLocalSpinner;
    Spinner secondYearSpinner, secondStateSpinner, secondNetworkSpinner,secondPublicTypeSpinner, secondLocalSpinner;
    Spinner gradeSpinner;
    Button requestButton;
    ProgressDialog activityIdicator;
    TextView graphTitle, publicTypeText;
    PagerSlidingTabStrip tabsStrip;
    ViewPager viewPager;
    GraphView graph;
    JSONObject reportResponse;
    TextView graphDescription,averageView,standardView,varianceView,averageView2,standardView2,varianceView2,textStandard, textVariance, textAverage;
    ScrollView compareFormScrollView;
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
        publicTypeText = (TextView) getView().findViewById(R.id.text_type);
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
        activityIdicator.setMessage("Gerando relatório");
        activityIdicator.setCancelable(false);



        graph = (GraphView) getView().findViewById(R.id.graph);
        setupTabPageViewer();
    }

    private void setupTabPageViewer(){
        graphDescription = (TextView) getView().findViewById(R.id.graph_description);
        textVariance = (TextView) getView().findViewById(R.id.text_variance);
        textAverage = (TextView) getView().findViewById(R.id.text_average);
        textStandard = (TextView) getView().findViewById(R.id.text_standard_desviation);
        averageView = (TextView) getView().findViewById(R.id.average);
        standardView = (TextView) getView().findViewById(R.id.standard_desviation);
        varianceView = (TextView) getView().findViewById(R.id.variance);
        averageView2 = (TextView) getView().findViewById(R.id.average2);
        standardView2 = (TextView) getView().findViewById(R.id.standard_desviation2);
        varianceView2 = (TextView) getView().findViewById(R.id.variance2);

        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        viewPager.setAdapter(new GraphsFragmentPagerAdapter(getActivity().getSupportFragmentManager()));
        // Give the PagerSlidingTabStrip the ViewPager
        tabsStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);


        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                graph.removeAllSeries();
                plotData(reportResponse.optJSONObject("first_report"), position, Color.BLUE);
                plotData(reportResponse.optJSONObject("second_report"), position, Color.RED);
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

    private void setupActions(){
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestData();
            }
        });

        showWhenEqual("Publica", firstPublicTypeSpinner, firstNetworkSpinner);
        showWhenEqual("Publica", secondPublicTypeSpinner, secondNetworkSpinner);
    }

    private final void focusOnView(){

        compareFormScrollView = (ScrollView) getView().findViewById(R.id.compare_form_scroll_view);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                compareFormScrollView.scrollTo(0, viewPager.getBottom() + 600);
            }
        });
    }

    private void showWhenEqual(final String value, final Spinner show, final Spinner entry){
        entry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (entry.getSelectedItem().toString().equals(value)) {
                    show.setVisibility(View.VISIBLE);
                    publicTypeText.setVisibility(View.VISIBLE);
                } else {
                    show.setVisibility(View.INVISIBLE);
                    publicTypeText.setVisibility(View.INVISIBLE);
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
                graph.removeAllSeries();
                reportResponse = response;
                plotData(reportResponse.optJSONObject("first_report"),0, Color.BLUE);
                plotData(reportResponse.optJSONObject("second_report"),0, Color.RED);
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

    private void plotData(JSONObject data, int graphOptionSelected, int color) {

        if (data != null) {
            JSONArray dataToPlot = new JSONArray();

            if (graphOptionSelected == 0){
                JSONObject ideb = data.optJSONObject("ideb");
                try {
                    if (ideb.getString("status").equals("available")){
                        dataToPlot = ideb.getJSONArray("ideb");
                        graphDescription.setText(R.string.ideb_description);

                        String average =  String.format("%.2f",ideb.getDouble("ideb_average")) + "pts ";
                        String standard = String.format("%.2f", ideb.getDouble("ideb_standard_deviation"))+ "pts ";
                        String variance = String.format("%.4f", ideb.getDouble("ideb_variance"))+ "pts ";
                        if (color == Color.BLUE){
                            averageView.setText(average);
                            standardView.setText(standard);
                            varianceView.setText(variance);
                        }else{
                            averageView2.setText(average);
                            standardView2.setText(standard);
                            varianceView2.setText(variance);
                        }


                        PlotterManager manager = new PlotterManager( graph, dataToPlot);

                        if (manager.plotPointGraph(ideb.getJSONArray("ideb_years"),color)) {
                            graph.setVisibility(View.VISIBLE);
                            tabsStrip.setVisibility(View.VISIBLE);
                            graphDescription.setVisibility(View.VISIBLE);
                            averageView.setVisibility(View.VISIBLE);
                            standardView.setVisibility(View.VISIBLE);
                            varianceView.setVisibility(View.VISIBLE);
                            averageView2.setVisibility(View.VISIBLE);
                            standardView2.setVisibility(View.VISIBLE);
                            varianceView2.setVisibility(View.VISIBLE);
                            textVariance.setVisibility(View.VISIBLE);
                            textAverage.setVisibility(View.VISIBLE);
                            textStandard.setVisibility(View.VISIBLE);
                            focusOnView();
                        }
                    }else{
                        graph.setVisibility(View.VISIBLE);
                        tabsStrip.setVisibility(View.VISIBLE);
                        graphDescription.setText("Desculpe, dado não disponível.");
                        graphDescription.setVisibility(View.VISIBLE);
                        focusOnView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                JSONObject rates = data.optJSONObject("rates");
                int initialXYear = 0;
                try {
                    if(rates.getString("status").equals("available")){
                        String average = "";
                        String standard = "";
                        String variance = "";
                        try {
                            switch (graphOptionSelected) {
                                case 1:
                                    dataToPlot = rates.getJSONArray("evasion");
                                    graphDescription.setText(R.string.evasion_description);
                                    average += String.format("%.2f",rates.getDouble("evasion_average"));
                                    standard += String.format("%.2f",rates.getDouble("evasion_standard_deviation"));
                                    variance += String.format("%.2f",rates.getDouble("evasion_variance"));

                                    break;
                                case 2:
                                    dataToPlot = rates.getJSONArray("performance");
                                    graphDescription.setText(R.string.performance_description);
                                    average += String.format("%.2f",rates.getDouble("performance_average"));
                                    standard += String.format("%.2f", rates.getDouble("performance_standard_deviation"));
                                    variance += String.format("%.2f",rates.getDouble("performance_variance"));

                                    break;
                                case 3:
                                    dataToPlot = rates.getJSONArray("distortion");
                                    graphDescription.setText(R.string.distortion_description);
                                    average += String.format("%.2f",rates.getDouble("distortion_average"));
                                    standard += String.format("%.2f", rates.getDouble("distortion_standard_deviation"));
                                    variance += String.format("%.2f",rates.getDouble("distortion_variance"));

                                    break;
                                default:
                                    break;
                            }

                            initialXYear = Integer.parseInt(data.getString("year"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        PlotterManager manager = new PlotterManager( graph, dataToPlot);

                        if (color == Color.BLUE){
                            averageView.setText(average + "% ");
                            standardView.setText(standard+ "% ");
                            varianceView.setText(variance+ "% ");
                        }else{
                            averageView2.setText(average+ "% ");
                            standardView2.setText(standard+ "% ");
                            varianceView2.setText(variance+ "% ");
                        }

                        if (manager.plotSimpleLineGraph(initialXYear,color)) {
                            graph.setVisibility(View.VISIBLE);
                            tabsStrip.setVisibility(View.VISIBLE);
                            graphDescription.setVisibility(View.VISIBLE);
                            averageView.setVisibility(View.VISIBLE);
                            standardView.setVisibility(View.VISIBLE);
                            varianceView.setVisibility(View.VISIBLE);
                            averageView2.setVisibility(View.VISIBLE);
                            standardView2.setVisibility(View.VISIBLE);
                            varianceView2.setVisibility(View.VISIBLE);
                            focusOnView();
                        }
                    }else{
                        graph.setVisibility(View.VISIBLE);
                        tabsStrip.setVisibility(View.VISIBLE);
                        graphDescription.setText("Desculpe, não disponível.");
                        graphDescription.setVisibility(View.VISIBLE);
                        focusOnView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

