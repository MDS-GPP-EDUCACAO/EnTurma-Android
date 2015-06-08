package graphs;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;

public class PlotterManager {

    private int initialXYear;
    private GraphView graphToPlot;
    private JSONArray dataToPlot;


    public PlotterManager(int initialXYear, GraphView graphToPlot, JSONArray dataToPlot){
        this.initialXYear = initialXYear;
        this.graphToPlot = graphToPlot;
        this.dataToPlot = dataToPlot;
    }

    public boolean plotSimpleLineGraph(){
                JSONArray currentDataToPlot = this.dataToPlot;
                DataPoint[] currentDataPoint = new DataPoint[currentDataToPlot.length()];
                for (int j = 0; j <currentDataToPlot.length(); j++) {
                    try {
                        Number dataX = (Number)currentDataToPlot.get(j);

                        currentDataPoint[j] = new DataPoint((double)initialXYear+j,dataX.doubleValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                LineGraphSeries<DataPoint> currentSerie = new LineGraphSeries<DataPoint>(currentDataPoint);
                this.graphToPlot.addSeries(currentSerie);


        return true;
    }


}
