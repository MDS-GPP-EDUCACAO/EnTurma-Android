package br.com.projetoenturma.enturma;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.*;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import rest.request.RESTFull;


public class ReportActivity extends ActionBarActivity {

    Spinner yearSpinner, stateSpinner, gradeSpinner, networkSpinner, localSpinner;
    Button sendButton;
    ProgressDialog activityIdicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setupActions();
        setupInitialValue();

        activityIdicator = new ProgressDialog(this);
        activityIdicator.setMessage("Procurando pelo relatório");
        activityIdicator.setCancelable(false);
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

    public void setupActions() {
        sendButton = (Button) findViewById(R.id.send_report);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestData();
            }
        });
    }

    public void setupInitialValue(){
        yearSpinner = (Spinner) findViewById(R.id.year);
        stateSpinner = (Spinner) findViewById(R.id.state);
        gradeSpinner = (Spinner) findViewById(R.id.grade);
        networkSpinner = (Spinner) findViewById(R.id.network);
        localSpinner = (Spinner) findViewById(R.id.local);

        String[] itemYear = new String[]{"2008", "2009", "2010","2011","2012","2013"};
        String[] itemState = new String[]{"AC", "DF", "SP","MG","RJ","SC"};
        String[] itemGrade = new String[]{"1° ano", "2° ano", "3° ano","4° ano","5° ano","6° ano","7° ano","8° ano","9° ano"};
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

        Map<String,String> params = new HashMap();
        params.put("year", yearSpinner.getSelectedItem().toString());
        params.put("state", stateSpinner.getSelectedItem().toString());
        params.put("grade", gradeSpinner.getSelectedItem().toString());
        params.put("test_type", networkSpinner.getSelectedItem().toString());
        params.put("public_type", "Total");
        params.put("local", localSpinner.getSelectedItem().toString());

        RESTFull full = new RESTFull(params);

        activityIdicator.show();
        full.requestReport(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                activityIdicator.dismiss();
                Toast.makeText(getApplicationContext(), "Sucesso!", Toast.LENGTH_LONG).show();
                //plot graph with response object
                System.out.println(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                activityIdicator.dismiss();
                Toast.makeText(getApplicationContext(), "Erro: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("omg android", statusCode + " " + throwable.getMessage());
                //Deal with request error
            }
        });
    }
}
