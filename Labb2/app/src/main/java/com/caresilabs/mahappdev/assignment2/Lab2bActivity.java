package com.caresilabs.mahappdev.assignment2;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class Lab2bActivity extends AppCompatActivity {

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1e);

        FragmentManager fragments = getSupportFragmentManager();

        ButtonFragment btnFrag = (ButtonFragment) fragments.findFragmentById(R.id.frguttonFragment);
        ViewerFragment viewFrag = (ViewerFragment) fragments.findFragmentById(R.id.frgViewFragment);

        this.controller = new Controller(viewFrag);

        btnFrag.setController(controller);
    }

}
