package graphs;


import android.graphics.Color;
import android.util.Log;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.Timer;

public class PlotterManager {

    private int initialXYear;
    private GraphView graphToPlot;
    private JSONArray dataToPlot;


    public PlotterManager( GraphView graphToPlot, JSONArray dataToPlot){
        this.graphToPlot = graphToPlot;
        this.dataToPlot = dataToPlot;
    }

    private DataPoint[] loadCurrentDataPoint(int initialXYear){

        JSONArray currentDataToPlot = this.dataToPlot;
        DataPoint[] currentDataPoint = new DataPoint[currentDataToPlot.length()];

        for (int j = 0; j <currentDataToPlot.length(); j++) {
            try {
                Number dataY = (Number)currentDataToPlot.get(j);

                currentDataPoint[j] = new DataPoint((double)initialXYear+j,dataY.doubleValue());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return  currentDataPoint;

    }

    public boolean setupSimpleLineGraph(int initialXYear, int color){

        DataPoint[] currentDataPoint = loadCurrentDataPoint(initialXYear);

        LineGraphSeries<DataPoint> currentSerie = new LineGraphSeries<DataPoint>(currentDataPoint);
        this.graphToPlot.addSeries(currentSerie);
        currentSerie.setThickness(8);
        currentSerie.setDataPointsRadius(4);
        currentSerie.setDrawBackground(true);

        currentSerie.setColor(color);

        this.plotSimpleLineGraph();

        return true;
    }

    private void plotSimpleLineGraph(){

        this.graphToPlot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String year = Double.toString(value);
                    String yearFixed = "";
                    if (year.length() >= 4) {
                        yearFixed = year.substring(0, 4);
                    }
                    return yearFixed;
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + "%";
                }
            }
        });

    }

    private DataPoint[] loadCurrentIDEBDataPoint(JSONArray idebYears){

        JSONArray currentDataToPlot = this.dataToPlot;
        DataPoint[] currentDataPoint = new DataPoint[currentDataToPlot.length()];

        for (int i = 0; i < currentDataPoint.length; i++) {
            try {
                Number dataY = (Number) currentDataToPlot.get(i);
                Double dataX = Double.parseDouble(idebYears.getString(i));
                currentDataPoint[i] = new DataPoint(dataX, dataY.doubleValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return currentDataPoint;

    }

    public boolean setupSimpleLineGraphIDEB(JSONArray idebYears, int color){

        JSONArray currentDataToPlot = this.dataToPlot;
        DataPoint[] currentDataPoint = loadCurrentIDEBDataPoint(idebYears);


        LineGraphSeries<DataPoint> currentSerie = new LineGraphSeries<DataPoint>(currentDataPoint);
        this.graphToPlot.addSeries(currentSerie);
        currentSerie.setColor(color);

        this.setupSimpleLineGraphIDEB();

        return true;
    }

    private void setupSimpleLineGraphIDEB(){

        this.graphToPlot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String year = Double.toString(value);
                    String yearFixed = "";
                    if (year.length() >= 4) {
                        yearFixed = year.substring(0, 4);
                    }
                    return yearFixed;
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " pts";
                }
            }
        });

    }

    private DataPoint[] loadCurrentPointDataIDEB(JSONArray idebYears){
        JSONArray currentDataToPlot = this.dataToPlot;
        DataPoint[] currentDataPoint = loadCurrentIDEBDataPoint(idebYears);

        for (int i = 0; i < currentDataPoint.length; i++) {
            try {
                Number dataY = (Number) currentDataToPlot.get(i);
                Double dataX = Double.parseDouble(idebYears.getString(i));
                currentDataPoint[i] = new DataPoint(dataX, dataY.doubleValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return currentDataPoint;
    }

    public boolean setupPointGraph(JSONArray idebYears, int color){
        DataPoint[] currentDataPoint = this.loadCurrentPointDataIDEB(idebYears);

        PointsGraphSeries<DataPoint> currentSerie = new PointsGraphSeries<DataPoint>(currentDataPoint);
        this.graphToPlot.addSeries(currentSerie);
        currentSerie.setColor(color);

        this.plotPointGraph();

        return true;
    }

    public void plotPointGraph(){

        this.graphToPlot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String year = Double.toString(value);
                    String yearFixed = "";
                    if (year.length() >= 4) {
                        yearFixed = year.substring(0, 4);
                    }
                    return yearFixed;
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " pts";
                }
            }
        });

    }

    private DataPoint[] loadCurrentPointBars(JSONArray idebYears){
        JSONArray currentDataToPlot = this.dataToPlot;
        DataPoint[] currentDataPoint = new DataPoint[currentDataToPlot.length()];


        for (int i = 0; i < currentDataPoint.length; i++) {
            try {
                Number dataY = (Number) currentDataToPlot.get(i);
                Double dataX = Double.parseDouble(idebYears.getString(i));
                currentDataPoint[i] = new DataPoint(dataX, dataY.doubleValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return currentDataPoint;

    }

    public boolean setupBarsGraph(JSONArray idebYears, int color){

        DataPoint[] currentDataPoint = loadCurrentPointBars(idebYears);


        BarGraphSeries<DataPoint> currentSerie = new BarGraphSeries<DataPoint>(currentDataPoint);
        this.graphToPlot.addSeries(currentSerie);
        currentSerie.setColor(color);
        currentSerie.setSpacing(50);

        this.plotBarsGraph();

        return true;
    }

    public void plotBarsGraph(){

        this.graphToPlot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String year = Double.toString(value);
                    String yearFixed = "";
                    if (year.length() >= 4) {
                        yearFixed = year.substring(0, 4);
                    }
                    return yearFixed;
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " pts";
                }
            }
        });
    }


}
