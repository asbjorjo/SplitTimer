package org.asbjorjo.splittimer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class IntermediateActivity extends AppCompatActivity {
    private SplitTimerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
        if (application.getIntermediates() == null) {
            application.setIntermediates(new ArrayList<String>());
        }

        ListView listView = (ListView) findViewById(R.id.intermediate_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, application.getIntermediates());
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
    }

    public void addIntermediate(View view) {
        EditText text = (EditText) findViewById(R.id.intermediate_input_description);

        application.getIntermediates().add(text.getText().toString());

        ListView listView = (ListView) findViewById(R.id.intermediate_list);
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
        setResult(RESULT_OK);
    }
}