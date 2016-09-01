package com.caresilabs.mahappdev.assignment2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewerFragment extends Fragment {

    private TextView tvWhatToDo;
    private TextView tvContent;

    public ViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewer, container, false);

        this.tvWhatToDo = (TextView) view.findViewById(R.id.whatToDo);
        this.tvContent = (TextView)view.findViewById(R.id.content);

        return view;
    }

    public void setWhatToDo(String whatToDo) {
        tvWhatToDo.setText(whatToDo);
    }

    public void setContent(String content) {
        tvContent.setText(content);
    }

}
