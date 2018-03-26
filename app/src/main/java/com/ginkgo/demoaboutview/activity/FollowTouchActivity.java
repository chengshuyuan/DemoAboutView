package com.ginkgo.demoaboutview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ginkgo.demoaboutview.R;
import com.ginkgo.demoaboutview.view.FollowTouchView;

public class FollowTouchActivity extends AppCompatActivity {

    private FollowTouchView mFollowTouchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_touch);
        mFollowTouchView = (FollowTouchView)this.findViewById(R.id.followtouchview);
    }
}
