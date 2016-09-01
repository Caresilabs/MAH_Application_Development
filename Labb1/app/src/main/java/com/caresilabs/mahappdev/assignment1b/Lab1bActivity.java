package com.caresilabs.mahappdev.assignment1b;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Lab1bActivity extends AppCompatActivity {

    private EditText tfName;
    private EditText tfPhone;
    private EditText tfEmail;

    private TextView tvSummary;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1e);

        initComponents();
        initListeners();
    }

    private void initListeners() {
        Button bSummary = (Button) findViewById(R.id.bSummary);
        bSummary.setOnClickListener(new SummaryButtonListener());

        Button bChangeColor = (Button) findViewById(R.id.bChangeColor);
        bChangeColor.setOnClickListener(new ChangeColorButtonListener());
    }

    private void initComponents() {
        tfName = (EditText) findViewById(R.id.tfName);
        tfPhone = (EditText) findViewById(R.id.tfPhone);
        tfEmail = (EditText) findViewById(R.id.tfEmail);

        tvSummary = (TextView) findViewById(R.id.tvSummary);
        tvName = (TextView) findViewById(R.id.tvName);
    }

    private class ChangeColorButtonListener implements View.OnClickListener {
        private boolean isRed = true;
        @Override
        public void onClick(View v) {
            if (isRed) {
                tvName.setTextColor(getResources().getColor(R.color.orange));
            } else {
                tvName.setTextColor(getResources().getColor(R.color.red));
            }

            isRed = !isRed;
        }
    }

    private class SummaryButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tvSummary.setText(getSummary());
        }
    }

    private String getSummary() {
        return String.format("Name=%s, Phone=%s, Email=%s", tfName.getText(), tfPhone.getText(), tfEmail.getText());
    }
}
