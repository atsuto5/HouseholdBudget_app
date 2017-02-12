package com.kii.sample.balance.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kii.sample.balance.R;
import com.kii.sample.balance.title.TitleFragment;
import com.kii.util.ViewUtil;

/**
 * Created by Atsuto5 on 2016/09/24.
 */
    public class SampleMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewUtil.toNextFragment(getSupportFragmentManager(), SampleTitleFragment.newInstance(), false);
    }
}
