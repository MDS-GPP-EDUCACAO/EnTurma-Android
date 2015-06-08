package graphs;


import android.util.Log;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;

public class PlotterManager {

    private int initialXYear;
    private GraphView graphToPlot;
    private JSONArray dataToPlot;


    public PlotterManager( GraphView graphToPlot, JSONArray dataToPlot){
        this.graphToPlot = graphToPlot;
        this.dataToPlot = dataToPlot;
    }

    public boolean plotSimpleLineGraph(int initialXYear){
        JSONArray currentDataToPlot = this.dataToPlot;
        DataPoint[] currentDataPoint = new DataPoint[currentDataToPlot.length()];
        for (int j = 0; j <currentDataToPlot.length(); j++) {
            try {
                Number dataY = (Number)currentDataToPlot.get(j);

                currentDataPoint[j] = new DataPoint((double)initialXYear+j,dataY.doubleValue());
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        LineGraphSeries<DataPoint> currentSerie = new LineGraphSeries<DataPoint>(currentDataPoint);
        this.graphToPlot.addSeries(currentSerie);
        currentSerie.setThickness(8);
        currentSerie.setDataPointsRadius(4);

        this.graphToPlot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
             @Override
             public String formatLabel(double value, boolean isValueX) {
                 if (isValueX) {
                     // show normal x values
                     String year = Double.toString(value);
                     String yearFixed = year.substring(0,4);
                     return yearFixed;
                 } else {
                     // show currency for y values
                     return super.formatLabel(value, isValueX) + "%";
                 }
             }
        });


        return true;
    }

    public boolean plotSimpleBarGraph(JSONArray idebYears){
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
        System.out.println(currentDataPoint[0].toString());
        System.out.println(currentDataPoint[1].toString());
        BarGraphSeries<DataPoint> currentSerie = new BarGraphSeries<DataPoint>(currentDataPoint);
        this.graphToPlot.addSeries(currentSerie);
        currentSerie.setSpacing(1);
        currentSerie.setDrawValuesOnTop(true);

        this.graphToPlot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String year = Double.toString(value);
                    String yearFixed = year.substring(0, 4);
                    return yearFixed;
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " pts";
                }
            }
        });

        return true;
    }


}
