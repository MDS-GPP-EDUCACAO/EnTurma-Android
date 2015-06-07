package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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
                System.out.println(response.toString());
                plotData(response);
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

    private void plotData(JSONObject data){
        JSONObject rates = data.optJSONObject("rates");
        JSONArray[] datas = new JSONArray[3];
        int initialXYear = 0;
        try {
            if (rates.getString("status").equals("available")){
                datas[0] = rates.getJSONArray("evasion");
                datas[1] = rates.getJSONArray("performance");
                datas[2] = rates.getJSONArray("distortion");
                initialXYear = Integer.parseInt(data.getString("year"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GraphView[] graphViews = new GraphView[]{
                (GraphView) getView().findViewById(R.id.evasion_graph),
                (GraphView) getView().findViewById(R.id.performance_graph),
                (GraphView) getView().findViewById(R.id.distortion_graph)
        };

        PlotterManager manager = new PlotterManager(initialXYear,graphViews,datas);

        if (manager.plotSimpleLineGraph()){
            LinearLayout graphs = (LinearLayout) getView().findViewById(R.id.graphs);
            graphs.setVisibility(View.VISIBLE);
        }
    }
}
