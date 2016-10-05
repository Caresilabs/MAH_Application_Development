package mahappdev.caresilabs.com.myfriends;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import mahappdev.caresilabs.com.myfriends.models.ChatListRow;
import mahappdev.caresilabs.com.myfriends.models.GroupListRow;

/**
 * Created by Simon on 10/3/2016.
 */

public class ChatListAdapter extends ArrayAdapter<ChatListRow> {

    public ChatListAdapter(Context context, List<ChatListRow> objects) {
        super(context, R.layout.list_chat_row, objects);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView    tvChatMemberName;
        TextView    tvChatMessage;
    }

    private RadioButton currentRadioBtn;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       /* // Get the data item for this position
        final ChatListRow data = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_chat_row, parent, false);

            viewHolder.tvChatMemberName = (TextView) convertView.findViewById(R.id.tvChatMemberName);
            viewHolder.tvChatMessage = (TextView) convertView.findViewById(R.id.tvChatMessage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvChatMemberName.setText(data.name);
        viewHolder.tvChatMessage.setText(data.message);

        if (data.isUser) {
            viewHolder.tvChatMemberName.setGravity(Gravity.RIGHT);
            viewHolder.tvChatMessage.setGravity(Gravity.RIGHT);
        }*/

        final ChatListRow data = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_chat_row, parent, false);

        TextView    tvChatMemberName;
        TextView    tvChatMessage;
        LinearLayout chatBubbleLayout;

        tvChatMemberName = (TextView) convertView.findViewById(R.id.tvChatMemberName);
        tvChatMessage = (TextView) convertView.findViewById(R.id.tvChatMessage);
        chatBubbleLayout = (LinearLayout) convertView.findViewById(R.id.chatBubbleLayout);

        tvChatMemberName.setText(data.name);
        tvChatMessage.setText(data.message);

        if (data.isUser) {
            chatBubbleLayout.setGravity(Gravity.RIGHT);
        }

        return convertView;
    }

}
