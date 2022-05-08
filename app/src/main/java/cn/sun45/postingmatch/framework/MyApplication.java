package cn.sun45.postingmatch.framework;

import android.app.Application;

import cn.sun45.postingmatch.framework.logic.BaseLogic;

/**
 * Created by Sun45 on 2022/1/30
 * application
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    public static final boolean testing = true;

    public static MyApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        this.application = this;

        BaseLogic.init();
    }
}
