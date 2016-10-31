package com.huxin.common;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huxin.common.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Entity entity = new Entity();
        binding.setEntiy(entity);
        entity.testImageUrl.set(Contract.testImageUrl);
//        setContentView(R.layout.activity_main);
    }
}
