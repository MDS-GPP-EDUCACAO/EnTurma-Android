package br.com.projetoenturma.enturma;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

    Spinner yearSpinner, stateSpinner, gradeSpinner, networkSpinner,publicTypeSpinner, localSpinner;
    Button sendButton;
    ProgressDialog activityIdicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        yearSpinner = (Spinner) findViewById(R.id.year);
        stateSpinner = (Spinner) findViewById(R.id.state);
        gradeSpinner = (Spinner) findViewById(R.id.grade);
        networkSpinner = (Spinner) findViewById(R.id.network);
        publicTypeSpinner = (Spinner) findViewById(R.id.public_type);
        localSpinner = (Spinner) findViewById(R.id.local);
        sendButton = (Button) findViewById(R.id.send_report);

        setupActions();

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

    private void setupActions() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestData();
            }
        });

        networkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (networkSpinner.getSelectedItem().toString().equals("Publica")){
                    publicTypeSpinner.setVisibility(View.VISIBLE);
                }else {
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
