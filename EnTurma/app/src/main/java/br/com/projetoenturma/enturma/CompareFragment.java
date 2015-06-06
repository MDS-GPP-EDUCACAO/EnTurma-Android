package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
                Toast.makeText(getActivity().getApplicationContext(), "Sucesso!", Toast.LENGTH_LONG).show();
                //plot graph with response object
                System.out.println(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                activityIdicator.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Erro: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("omg android", statusCode + " " + throwable.getMessage());
                //Deal with request error
            }
        });
    }
}
