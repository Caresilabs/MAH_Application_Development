package mahappdev.caresilabs.com.myfriends.views;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.myfriends.ChatListAdapter;
import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.controllers.MainController;
import mahappdev.caresilabs.com.myfriends.models.ChatListRow;
import mahappdev.caresilabs.com.myfriends.models.DataModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    @BindView(R.id.lwChat)
    ListView lwChat;

    @BindView(R.id.etChat)
    EditText etChat;

    private MainController  controller;
    private ChatListAdapter chatListAdaper;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatListAdaper = new ChatListAdapter(getActivity(), new ArrayList<ChatListRow>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        lwChat.setAdapter(chatListAdaper);

        return view;
    }

    @OnClick(R.id.btnChatSend)
    void onChatSend() {
        if (etChat.getText().toString().trim().equals(""))
            return;

        controller.sendChatMessage(etChat.getText().toString());

        etChat.getText().clear();
    }

    public void addChatMessage(DataModel.ChatModel chat) {
        ChatListRow row = new ChatListRow();
        row.name = chat.member;
        row.message = chat.message;
        row.isUser = chat.isUser;
        chatListAdaper.add(row);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
    
}
