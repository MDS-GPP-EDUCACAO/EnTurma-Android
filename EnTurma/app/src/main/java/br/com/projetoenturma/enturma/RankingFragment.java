package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;

import customAdapters.RankingAdapter;
import rest.request.RESTFull;

/**
 * Created by PedroSales on 13/06/15.
 */
public class RankingFragment extends Fragment{


    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView rankingListView;
    private Button requestButton;
    private Spinner year,grade;

    public static RankingFragment newInstance(int sectionNumber) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public RankingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);
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

        this.rankingListView = (ListView) getView().findViewById(R.id.ranking_list_view);
        this.requestButton = (Button) getView().findViewById(R.id.request_ranking);
        this.grade = (Spinner) getView().findViewById(R.id.state);
        this.year = (Spinner) getView().findViewById(R.id.year);

        String[] data = {"Tst1","Tst2","Tst3","Tst4","Tst5"};

        this.rankingListView.setAdapter(new RankingAdapter(getActivity().getApplicationContext(), data));
    }

    private void setupActions(){
        this.requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestData();
            }
        });
    }

    private void requestData(){
        Map<String,String> params = new HashMap();
        params.put("year", year.getSelectedItem().toString());
        params.put("grade", grade.getSelectedItem().toString());

        RESTFull rest = new RESTFull(params);


    }
}
