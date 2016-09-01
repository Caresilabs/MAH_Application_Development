package com.caresilabs.mahappdev.assignment2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ButtonFragment extends Fragment {

    private Button   btnNext;
    private Button   btnPrev;

    private Controller controller;

    public ButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_button, container, false);

        this.btnNext = (Button)view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.nextClick();
            }
        });

        this.btnPrev = (Button)view.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.previousClick();
            }
        });

        return view;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
