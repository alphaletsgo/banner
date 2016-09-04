package cn.isif.plug.banner;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.isif.alibs.utils.log.ALog;
import cn.isif.plug.banner.bean.BannerTest;
import cn.isif.plug.bannerview.BannerView;
import cn.isif.plug.bannerview.exception.ClassTypeException;
import cn.isif.plug.bannerview.listener.OnBannerClickListener;
import cn.isif.plug.bannerview.listener.OnBannerFlingListener;


public class MainActivity extends FragmentActivity {
    BannerView bannerView = null;
    String[] urls = {"http://upload-images.jianshu.io/upoad_images/1651102-bc70681801c32bc0.jpg", "http://upload-images.jianshu.io/upload_images/912344-9577b0ebf32e4126.jpg"
            , "http://upload-images.jianshu.io/upload_images/1374103-27b82baec4715fe6.jpg", "http://upload-images.jianshu.io/upload_images/2059011-9f6bae280d9631ca.jpg",
            "http://upload-images.jianshu.io/upload_images/1843996-af01025bb4e4ad6e.jpg", "http://p0.so.qhimg.com/t013428ca2e6dfc71c0.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerView = (BannerView) this.findViewById(R.id.banner);
        bannerView.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onBannerClickListener(int position, View view) {
                ALog.d("onBannerClickListener" + position);
            }
        });

        bannerView.setOnBannerFlingListener(new OnBannerFlingListener() {
            @Override
            public void onBannerFlingListener(float velocityX, float velocityY) {
                ALog.d("onBannerPageFlingListener"+velocityX);
                ALog.d("onBannerPageFlingListener"+velocityY);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    bannerView.setData(getBannerData());
                } catch (ClassTypeException e) {
                    e.printStackTrace();
                }
            }
        }, 2 * 1000);
    }

    public List<Object> getBannerData() {
        List<Object> bannerBeen = new ArrayList<>();
        int i = 0;
        for (String u : urls) {
            BannerTest bt = new BannerTest();
            bt.oot = "好多文字测试 [ " + i + " ]";
            bt.path = u;
            bannerBeen.add(bt);
            i++;
        }
        return bannerBeen;
    }
}
