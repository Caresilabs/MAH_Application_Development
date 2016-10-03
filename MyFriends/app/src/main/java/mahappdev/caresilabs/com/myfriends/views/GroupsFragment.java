package mahappdev.caresilabs.com.myfriends.views;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mahappdev.caresilabs.com.myfriends.GroupsListAdapter;
import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.models.GroupListRow;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    @BindView(R.id.lwGroups)
    ListView lwGroups;

    private GroupsListAdapter groupsAdapter;

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

                }
            }
        });

        return view;
    }

    private void initList() {
        lwGroups.setAdapter(groupsAdapter = new GroupsListAdapter(getContext(), new ArrayList()));
        lwGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //launchEditItem(getActivity(), FragmentType.DETAILS_INCOME, (IncomeModel) incomeAdapter.getItem(position));
            }
        });
    }

    public void refreshGroups(List<Map<String, String>> groups) {
        groupsAdapter.clear();

        List<GroupListRow> rows = new ArrayList<>();
        for (Map<String, String> group : groups) {
            GroupListRow row = new GroupListRow();
            row.name = group.get("group");
            rows.add(row);
        }

        groupsAdapter.addAll(rows);
    }

}
