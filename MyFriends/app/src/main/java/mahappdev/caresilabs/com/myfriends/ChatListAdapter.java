package mahappdev.caresilabs.com.myfriends;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import mahappdev.caresilabs.com.myfriends.models.DataModel;

/**
 * Created by Simon on 10/3/2016.
 */

public class ChatListAdapter extends ArrayAdapter<DataModel.ChatModel> {

    public ChatListAdapter(Context context, List<DataModel.ChatModel> objects) {
        super(context, R.layout.list_chat_row, objects);
    }

    private RadioButton currentRadioBtn;

    // Can't use the viewholder pattern because of different gravity settings.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataModel.ChatModel data = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_chat_row, parent, false);


        TextView tvChatMemberName = (TextView) convertView.findViewById(R.id.tvChatMemberName);
        TextView tvChatMessage = (TextView) convertView.findViewById(R.id.tvChatMessage);
        ImageView ivChatImage = (ImageView) convertView.findViewById(R.id.ivChatImage);
        LinearLayout chatBubbleLayout = (LinearLayout) convertView.findViewById(R.id.chatBubbleLayout);

        tvChatMemberName.setText(data.member);
        tvChatMessage.setText(data.message);

        if (data.image != null) {
            ivChatImage.setImageBitmap(BitmapFactory.decodeFile(data.image));
        }

        if (data.isUser) {
            chatBubbleLayout.setGravity(Gravity.RIGHT);
        }

        return convertView;
    }

}
