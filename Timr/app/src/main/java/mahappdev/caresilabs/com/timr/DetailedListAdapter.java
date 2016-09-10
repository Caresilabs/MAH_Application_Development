package mahappdev.caresilabs.com.timr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mahappdev.caresilabs.com.timr.models.TimeItem;

/**
 * Created by Simon on 9/10/2016.
 */
public class DetailedListAdapter<T extends TimeItem> extends ArrayAdapter<T> {

    public DetailedListAdapter(Context context, List<T> objects) {
        super(context, R.layout.detailed_list_row, objects);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView tvTitle;
        TextView tvSubTitle1;
        TextView tvSubTitle2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TimeItem data = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.detailed_list_row, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvSubTitle1 = (TextView) convertView.findViewById(R.id.tvSubTitle1);
            viewHolder.tvSubTitle2 = (TextView) convertView.findViewById(R.id.tvSubTitle2);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(data.title + " - " + Math.max(0, data.toTime - data.fromTime) + "min");
        viewHolder.tvSubTitle1.setText(data.category);

        String from = ((int) (data.fromTime / 60) + ":" + (data.fromTime % 60));
        String to = ((int) (data.toTime / 60) + ":" + (data.toTime % 60));
        viewHolder.tvSubTitle2.setText(from + " - " + to);
        return convertView;

    }
}
