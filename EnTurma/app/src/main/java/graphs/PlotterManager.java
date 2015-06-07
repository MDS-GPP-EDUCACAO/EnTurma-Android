package graphs;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;

public class PlotterManager {

    private int initialXYear;
    private GraphView[] graphsToPlot;
    private JSONArray[] dataToPlot;


    public PlotterManager(int initialXYear, GraphView[] graphsToPlot, JSONArray[] dataToPlot){
        this.initialXYear = initialXYear;
        this.graphsToPlot = graphsToPlot;
        this.dataToPlot = dataToPlot;
    }

    public boolean plotSimpleLineGraph(){
        if (this.graphsToPlot.length == dataToPlot.length){
            for (int i = 0; i < graphsToPlot.length; i++) {
                JSONArray currentDataToPlot = this.dataToPlot[i];
                DataPoint[] currentDataPoint = new DataPoint[currentDataToPlot.length()];
                for (int j = 0; j <currentDataToPlot.length(); j++) {
                    try {
                        currentDataPoint[j] = new DataPoint((double)initialXYear+j,(double)currentDataToPlot.get(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                LineGraphSeries<DataPoint> currentSerie = new LineGraphSeries<DataPoint>(currentDataPoint);
                this.graphsToPlot[i].addSeries(currentSerie);
            }
        }
        return true;
    }


}
