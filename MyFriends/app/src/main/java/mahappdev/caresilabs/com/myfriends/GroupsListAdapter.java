package mahappdev.caresilabs.com.myfriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mahappdev.caresilabs.com.myfriends.models.GroupListRow;
import mahappdev.caresilabs.com.myfriends.models.Groups;

/**
 * Created by Simon on 10/3/2016.
 */

public class GroupsListAdapter extends ArrayAdapter<GroupListRow> {

    public GroupsListAdapter(Context context, List<GroupListRow> objects) {
        super(context, R.layout.list_groups_row, objects);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView  tvTitle;
        TextView  tvSubTitle1;
        TextView  tvSubTitle2;
        ImageView iwIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GroupListRow data = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_groups_row, parent, false);

           viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
           /*  viewHolder.tvSubTitle1 = (TextView) convertView.findViewById(R.id.tvSubTitle1);
            viewHolder.tvSubTitle2 = (TextView) convertView.findViewById(R.id.tvSubTitle2);
            viewHolder.iwIcon = (ImageView) convertView.findViewById(R.id.iwItemIcon);*/

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(data.name);

        return convertView;

    }
}
