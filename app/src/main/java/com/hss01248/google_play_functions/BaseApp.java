package com.hss01248.google_play_functions;

import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.ThreadUtils;


/**
 * @Despciption todo
 * @Author hss
 * @Date 5/10/24 5:49 PM
 * @Version 1.0
 */
public class BaseApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();


        //crash();
    }

    private void crash() {
        ThreadUtils.getMainHandler().postDelayed(new Runnable(){

            @Override
            public void run() {
                int i = 1/0;
            }
        },2000);
    }
}
