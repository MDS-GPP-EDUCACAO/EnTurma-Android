package customAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Map;

import br.com.projetoenturma.enturma.R;

/**
 * Created by gabriel on 6/22/15.
 */
public class RankingAdapter extends BaseAdapter {

    Context context;
    String[] data;
    private static LayoutInflater inflater = null;

    public RankingAdapter(Context context, String[] data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.data.length;
    }

    @Override
    public Object getItem(int position) {
        return this.data[position];
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
        rankingPosition.setText(data[position]);
        state.setText(data[position]);
        score.setText(data[position]);

        System.out.println(data[position]);

        return cell;
    }

}
