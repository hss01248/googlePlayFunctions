package com.hss01248.google_play_functions;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hss01248.googleplay.GooglePlayUpdater;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/11/24 11:06 AM
 * @Version 1.0
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void checkUpdate(View view) {
        GooglePlayUpdater.checkUpdate(true);

    }
}
