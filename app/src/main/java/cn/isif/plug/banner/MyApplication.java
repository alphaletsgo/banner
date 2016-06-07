package cn.isif.plug.banner;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by dell on 2016/6/7-14:59.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
