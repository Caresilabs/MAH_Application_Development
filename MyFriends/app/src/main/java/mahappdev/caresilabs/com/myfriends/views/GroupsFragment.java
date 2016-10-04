package mahappdev.caresilabs.com.myfriends.views;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mahappdev.caresilabs.com.myfriends.GroupsListAdapter;
import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.controllers.MainController;
import mahappdev.caresilabs.com.myfriends.models.DataModel;
import mahappdev.caresilabs.com.myfriends.models.GroupListRow;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment implements GroupsListAdapter.IGroupListListener {

    @BindView(R.id.lwGroups)
    ListView lwGroups;

    private GroupsListAdapter groupsAdapter;

    private MainController controller;

    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
       // args.putString(ARG_PARAM1, param1);
       // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_groups, container, false);
        ButterKnife.bind(this, view);

        initList();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupsAdapter.getCount() >= 20) {
                    Snackbar.make(view, "Can't add more than 20 groups!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    final EditText edittext = new EditText(getActivity());

                    alert.setTitle("New Group");
                    alert.setMessage("Enter a group name.");

                    alert.setView(edittext);

                    alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String name = edittext.getText().toString();
                            controller.updateSubscription(name, true);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

                    alert.show();
                }
            }
        });

        if (controller != null)
            controller.refreshGroups();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initList() {
        lwGroups.setAdapter(groupsAdapter = new GroupsListAdapter(getContext(), new ArrayList(), this));
    }

    public void refreshGroups(Collection<DataModel.GroupModel> groups, String myName, String currentRoom) {
        if (groupsAdapter == null)
            return;

        groupsAdapter.clear();

        final List<GroupListRow> rows = new ArrayList<>();
        for (DataModel.GroupModel group : groups) {
            GroupListRow row = new GroupListRow();
            row.name = group.name;
            row.users = group.members.size() + " of 20";
            row.isJoined = group.members.containsKey(myName);
            row.isCurrent = group.name.equals(currentRoom);
            rows.add(row);
        }

        groupsAdapter.addAll(rows);
    }

    @Override
    public void onActiveChanged(CharSequence text) {
        controller.setActiveRoom(text.toString());
    }

    @Override
    public void onSubscribedChanged(CharSequence text, boolean isChecked) {
        controller.updateSubscription(text.toString(), isChecked);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }
}
