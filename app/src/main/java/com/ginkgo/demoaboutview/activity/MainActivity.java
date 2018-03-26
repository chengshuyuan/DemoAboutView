package com.ginkgo.demoaboutview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.ginkgo.demoaboutview.R;
import com.ginkgo.demoaboutview.adapter.ExamplesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mExamplesListView;
    private ExamplesListAdapter mExamplesListAdapter;
    private List<String> mExampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExamplesListView = (ListView) this.findViewById(R.id.lv_demo_examples);

    }
}
