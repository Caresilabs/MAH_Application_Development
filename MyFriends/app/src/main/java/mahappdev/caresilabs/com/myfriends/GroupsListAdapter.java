package mahappdev.caresilabs.com.myfriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import mahappdev.caresilabs.com.myfriends.models.GroupListRow;

/**
 * Created by Simon on 10/3/2016.
 */

public class GroupsListAdapter extends ArrayAdapter<GroupListRow> {

    private final IGroupListListener listener;

    public GroupsListAdapter(Context context, List<GroupListRow> objects, IGroupListListener listener) {
        super(context, R.layout.list_groups_row, objects);
        this.listener = listener;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView    tvTitle;
        TextView    tvNumUsers;
        RadioButton rbActive;
        Switch      swSubscribed;
    }

    private RadioButton currentRadioBtn;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final GroupListRow data = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_groups_row, parent, false);

            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvNumUsers = (TextView) convertView.findViewById(R.id.tvNumUsers);

            viewHolder.rbActive = (RadioButton) convertView.findViewById(R.id.rbActive);
            viewHolder.rbActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton rb = (RadioButton) v;

                    if (!viewHolder.swSubscribed.isChecked()) {
                        rb.setChecked(false);
                        return;
                    }

                    if (currentRadioBtn != null && currentRadioBtn != rb) {
                        //if (!rb.getTag().equals(currentRadioBtn.getTag()))
                        currentRadioBtn.setChecked(false);
                    }
                    currentRadioBtn = rb;
                    listener.onActiveChanged(viewHolder.tvTitle.getText());
                }
            });

            viewHolder.swSubscribed = (Switch) convertView.findViewById(R.id.swSubscribed);
            viewHolder.swSubscribed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSubscribedChanged(viewHolder.tvTitle.getText(), viewHolder.swSubscribed.isChecked());
                }
            });
         /*   viewHolder.swSubscribed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onSubscribedChanged(data.name, isChecked);
                }
            });*/

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(data.name);
        viewHolder.tvNumUsers.setText(data.users);

        viewHolder.rbActive.setChecked(data.isCurrent);
        viewHolder.swSubscribed.setChecked(data.isJoined);

        return convertView;
    }

    public interface IGroupListListener {
        void onActiveChanged(CharSequence text);

        void onSubscribedChanged(CharSequence text, boolean isChecked);
    }
}
