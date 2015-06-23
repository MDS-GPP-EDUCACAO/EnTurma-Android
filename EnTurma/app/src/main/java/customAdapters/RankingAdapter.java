package customAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.projetoenturma.enturma.R;

/**
 * Created by gabriel on 6/22/15.
 */
public class RankingAdapter extends BaseAdapter {

    Context context;
    List<Map<String,String>> data = new ArrayList<Map<String, String>>();
    private static LayoutInflater inflater = null;

    public RankingAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Map<String,String>> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        System.out.println("Size" + data.size());
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        if (cell == null){
            cell = inflater.inflate(R.layout.ranking_row,null);
        }
        TextView rankingPosition = (TextView) cell.findViewById(R.id.ranking_position);
        TextView state = (TextView) cell.findViewById(R.id.state);
        TextView score = (TextView) cell.findViewById(R.id.score);

        rankingPosition.setText(position + 1 + "°");

        if(position == 0){
            rankingPosition.setTextColor(Color.rgb(255, 204 , 102 ));
            state.setTextColor(Color.rgb(255, 204, 102));
            score.setTextColor(Color.rgb(255, 204, 102));
        } else{
            rankingPosition.setTextColor(Color.BLACK);
            state.setTextColor(Color.BLACK);
            score.setTextColor(Color.BLACK);
        }


        Map<String,String> currentCellData = data.get(position);

        state.setText(currentCellData.get("stateName"));
        score.setText(currentCellData.get("stateScore"));

        return cell;
    }

}
