package org.asbjorjo.splittimer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class IntermediateActivity extends AppCompatActivity {
    private SplitTimerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
    }

    public void addIntermediate(View view) {
        List<String> intermediates = application.getIntermediates();
        EditText text = (EditText) findViewById(R.id.intermediate_input_description);

        if (intermediates == null) {
            intermediates = new ArrayList<>();
        }
        intermediates.add(text.getText().toString());
    }
}
