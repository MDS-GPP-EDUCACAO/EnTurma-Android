package br.com.projetoenturma.enturma;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class ReportActivity extends ActionBarActivity {

    Spinner yearSpinner, stateSpinner, gradeSpinner, networkSpinner, localSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setupInitialValue();
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
        String[] itemGrade = new String[]{"1", "2", "3","4","5","6","7","8","9"};
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
}
