package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
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


public class ReportFragment extends Fragment {

    Spinner yearSpinner, stateSpinner, gradeSpinner, networkSpinner,publicTypeSpinner, localSpinner;
    Button sendButton;
    ProgressDialog activityIdicator;
    PagerSlidingTabStrip tabsStrip;
    ViewPager viewPager;
    TextView graphDescription,averageView,standardView,varianceView, textStandard, textVariance, textAverage;
    GraphView graph;
    JSONObject reportResponse;
    ScrollView reportFormScrollView;

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
        textVariance = (TextView) getView().findViewById(R.id.text_variance);
        textAverage = (TextView) getView().findViewById(R.id.text_average);
        textStandard = (TextView) getView().findViewById(R.id.text_standard_desviation);


        setupActions();

        activityIdicator = new ProgressDialog(getActivity());
        activityIdicator.setMessage("Procurando pelo relatório");
        activityIdicator.setCancelable(false);


        graph = (GraphView) getView().findViewById(R.id.graph);

        setupTabPageViewer();

        graph.setVisibility(View.INVISIBLE);
        tabsStrip.setVisibility(View.INVISIBLE);
        graphDescription.setVisibility(View.INVISIBLE);
        averageView = (TextView) getView().findViewById(R.id.average);
        standardView = (TextView) getView().findViewById(R.id.standard_desviation);
        varianceView = (TextView) getView().findViewById(R.id.variance);


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

    private final void focusOnView(){

        reportFormScrollView = (ScrollView) getView().findViewById(R.id.report_form_scroll_view);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                reportFormScrollView.scrollTo(0, viewPager.getBottom()+100);
            }
        });
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
                graph.removeAllSeries();
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

            System.out.println("Olá");
            graph.removeAllSeries();
            JSONArray dataToPlot = new JSONArray();

            if (graphOptionSelected == 0){
                JSONObject ideb = data.optJSONObject("ideb");
                try {
                    if (ideb.getString("status").equals("available")){
                        dataToPlot = ideb.getJSONArray("ideb");
                        graphDescription.setText(R.string.ideb_description);

                        String average = "" + String.format("%.2f",ideb.getDouble("ideb_average"));
                        String standard = "" + String.format("%.2f", ideb.getDouble("ideb_standard_deviation"));
                        String variance = "" + String.format("%.4f", ideb.getDouble("ideb_variance"));

                        averageView.setText(average + "pts ");
                        standardView.setText(standard+ "pts ");
                        varianceView.setText(variance+ "pts " );

                        PlotterManager manager = new PlotterManager( graph, dataToPlot);

                        if (manager.plotSimpleBarGraph(ideb.getJSONArray("ideb_years"),Color.BLUE)) {
                            graph.setVisibility(View.VISIBLE);
                            tabsStrip.setVisibility(View.VISIBLE);
                            graphDescription.setVisibility(View.VISIBLE);
                            averageView.setVisibility(View.VISIBLE);
                            standardView.setVisibility(View.VISIBLE);
                            varianceView.setVisibility(View.VISIBLE);
                            textVariance.setVisibility(View.VISIBLE);
                            textAverage.setVisibility(View.VISIBLE);
                            textStandard.setVisibility(View.VISIBLE);
                            focusOnView();
                        }
                    }else{
                        graph.setVisibility(View.VISIBLE);
                        tabsStrip.setVisibility(View.VISIBLE);
                        graphDescription.setText("Desculpe, mais não temo esse dado disponível.");
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

                        averageView.setText(average+ "% ");
                        standardView.setText(standard + "% ");
                        varianceView.setText(variance+ "% ");

                        if (manager.plotSimpleLineGraph(initialXYear, Color.BLUE)) {
                            graph.setVisibility(View.VISIBLE);
                            tabsStrip.setVisibility(View.VISIBLE);
                            graphDescription.setVisibility(View.VISIBLE);
                            averageView.setVisibility(View.VISIBLE);
                            standardView.setVisibility(View.VISIBLE);
                            varianceView.setVisibility(View.VISIBLE);
                            focusOnView();
                        }
                    }else{
                        graph.setVisibility(View.VISIBLE);
                        tabsStrip.setVisibility(View.VISIBLE);
                        graphDescription.setText("Desculpe, mais não temo esse dado disponível.");
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
