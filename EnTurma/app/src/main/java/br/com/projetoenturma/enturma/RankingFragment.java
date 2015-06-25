package br.com.projetoenturma.enturma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    ProgressDialog activityIdicator;
    private boolean exceptionFlag;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabsStrip;
    private Map<String, List<Map<String, String>>> allRankedStates;
    private static final String[] KEYS = {"ideb","evasion","peformance","distortion"};

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
        this.requestButton = (Button) getView().findViewById(R.id.ranking_request);
        this.grade = (Spinner) getView().findViewById(R.id.grade);
        this.year = (Spinner) getView().findViewById(R.id.year);

        activityIdicator = new ProgressDialog(getActivity());
        activityIdicator.setMessage("Gerando Ranking");
        activityIdicator.setCancelable(false);

        setupActions();

        this.rankingListView.setAdapter(new RankingAdapter(getActivity().getApplicationContext()));

        setupTabPageViewer();


    }

    private void setupActions(){
        this.requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                requestData();
            }
        });
    }

    private void setupTabPageViewer(){

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

                System.out.println("Pager" + position);
                showTableViewAtKey(KEYS[position]);
                if(position != 0){
                    setExceptionIdeb(false);
                } else {
                    if (exceptionFlag){
                        setExceptionIdeb(true);
                    }else {
                        setExceptionIdeb(false);
                    }
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

        tabsStrip.setVisibility(View.INVISIBLE);
        rankingListView.setVisibility(View.INVISIBLE);
    }

    private void showTableViewAtKey(String key){
        List<Map<String,String>> currentListViewData = allRankedStates.get(key);
        ((RankingAdapter)this.rankingListView.getAdapter()).setData(currentListViewData);
        ((RankingAdapter)this.rankingListView.getAdapter()).notifyDataSetChanged();
        this.rankingListView.setVisibility(View.VISIBLE);
        tabsStrip.setVisibility(View.VISIBLE);
    }


    public void requestData(){
        Map<String,String> params = new HashMap();
        params.put("year", year.getSelectedItem().toString());
        params.put("grade", grade.getSelectedItem().toString());


        RESTFull rest = new RESTFull(params);
        activityIdicator.show();
        rest.requestRanking(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.request_success, Toast.LENGTH_LONG).show();
                activityIdicator.dismiss();
                //plot graph with response object
                try {
                    parseJSONToRaking(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                activityIdicator.dismiss();
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity().getApplicationContext(), R.string.request_failure, Toast.LENGTH_LONG).show();
                Log.d("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }

    public void parseJSONToRaking(JSONObject response) throws JSONException {
        JSONObject ideb = response.getJSONObject("ideb_list");
        JSONArray evasion = response.getJSONArray("evasion_list");
        JSONArray distortion = response.getJSONArray("distortion_list");
        JSONArray performance = response.getJSONArray("peformance_list");

        List<Map<String, String>> evasionParsed = this.serializeDataToMap(evasion, "evasion");
        List<Map<String, String>> distortionParsed = this.serializeDataToMap(distortion, "distortion");
        List<Map<String, String>> performanceParsed = this.serializeDataToMap(performance, "peformance");
        List<Map<String, String>> idebParsed = new ArrayList<Map<String, String>>();

        if (ideb.getString("status").equals("avaliable")){
            idebParsed = this.serializeDataToMap(ideb.getJSONArray("ideb"),"score");
            exceptionFlag = false;
            setExceptionIdeb(false);
        } else {
            exceptionFlag = true;
            setExceptionIdeb(true);
        }

        allRankedStates = new HashMap<>();
        allRankedStates.put("evasion", evasionParsed);
        allRankedStates.put("peformance", performanceParsed);
        allRankedStates.put("distortion", distortionParsed);
        allRankedStates.put("ideb", idebParsed);

        showTableViewAtKey(KEYS[0]);
    }

    public List<Map<String, String>> serializeDataToMap(JSONArray data, String key) throws JSONException {
        List<Map<String,String>> parsedData = new ArrayList<>();

        for(int i = 0; i<data.length(); i++){
            JSONObject currentObject = (JSONObject) data.get(i);

            Map<String,String> currentParsedData = new HashMap<>();
            currentParsedData.put("stateName",this.getStateFromID(currentObject.getInt("state_id")));
            currentParsedData.put("stateScore", Double.toString(currentObject.getDouble(key)));
            currentParsedData.put("graphType",key);
            parsedData.add(currentParsedData);
        }

        return parsedData;
    }

    public String getStateFromID(int id){
        String[] states = {"AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO"};
        return states[id-1];
    }

    public void setExceptionIdeb(boolean status){

        TextView textIdeb = (TextView) getView().findViewById(R.id.textIdeb);

        if(status){
            textIdeb.setVisibility(View.VISIBLE);
        } else {
            textIdeb.setVisibility(View.INVISIBLE);
        }
    }
}
