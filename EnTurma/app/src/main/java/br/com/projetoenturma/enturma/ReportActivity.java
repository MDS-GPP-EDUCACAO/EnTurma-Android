package br.com.projetoenturma.enturma;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.loopj.android.http.*;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ReportActivity extends ActionBarActivity {

    Spinner yearSpinner, stateSpinner, gradeSpinner, networkSpinner, localSpinner;
    Button sendButton, clearButton;
    static final String URLserver = "http://localhost:3000/";
    private LinearLayout animatedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setupActions();
        setupInitialValue();
        animatedLayout = (LinearLayout) findViewById(R.id.graphs);
    }

    public void setupActions() {
        sendButton = (Button) findViewById(R.id.send_report);
        clearButton = (Button) findViewById(R.id.clear);


        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                requestData();
                showGraphs();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                clearGraphs();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupInitialValue(){
        yearSpinner = (Spinner) findViewById(R.id.year);
        stateSpinner = (Spinner) findViewById(R.id.state);
        gradeSpinner = (Spinner) findViewById(R.id.grade);
        networkSpinner = (Spinner) findViewById(R.id.network);
        localSpinner = (Spinner) findViewById(R.id.local);

        String[] itemYear = new String[]{"2008", "2009", "2010","2011","2012","2013"};
        String[] itemState = new String[]{"AC", "DF", "SP","MG","RJ","SC"};
        String[] itemGrade = new String[]{"1º ano", "2º ano", "3","4","5","6","7","8","9"};
        String[] itemNetwork = new String[]{"Total","Pública", "Privada"};
        String[] itemLocal = new String[]{"Total", "Urbana", "Rural"};
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemYear);
        ArrayAdapter<String> adapterState = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemState);
        ArrayAdapter<String> adapterGrade = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemGrade);
        ArrayAdapter<String> adapterNetwork = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemNetwork);
        ArrayAdapter<String> adapterLocal = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemLocal);

        yearSpinner.setAdapter(adapterYear);
        stateSpinner.setAdapter(adapterState);
        gradeSpinner.setAdapter(adapterGrade);
        networkSpinner.setAdapter(adapterNetwork);
        localSpinner.setAdapter(adapterLocal);
    }

    public void requestData(){


        String requestFromUser = "/report/request_report.json?utf8=%E2%9C%93&year=" +
                yearSpinner.getSelectedItem().toString() +
                "&state=" +
                yearSpinner.getSelectedItem().toString()+
                "&grade=" +
                gradeSpinner.getSelectedItem().toString()+
                "&test_type=" +
                networkSpinner.getSelectedItem().toString()+
                "&local=" +
                localSpinner.getSelectedItem().toString();

        GraphView graph = (GraphView) findViewById(R.id.ideb_graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(2008, 0),
                new DataPoint(2009, 5),
                new DataPoint(2010, 0),
                new DataPoint(2011, 2),
        });
        graph.addSeries(series);


        try{
            requestFromUser = URLEncoder.encode(requestFromUser, "UTF-8");
        }catch (UnsupportedEncodingException e){

            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://127.0.0.1:3000/report/request_report.json?utf8=%E2%9C%93&year=2008&state=AC&grade=1%C2%B0+ano&test_type=Total&public_type=Total&local=Total", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                System.out.println(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("omg android", statusCode + " " + throwable.getMessage());
            }
        });


    }

    public void showGraphs()
    {
        sendButton = (Button) findViewById(R.id.send_report);
        if (animatedLayout.getVisibility() == View.GONE)
        {
            clearButton.setVisibility(View.VISIBLE);
            animatedLayout.setVisibility(View.VISIBLE);

        }
    }

    public void clearGraphs()
    {
        if (animatedLayout.getVisibility() == View.VISIBLE)
        {
            animatedLayout.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
        }

    }


}
