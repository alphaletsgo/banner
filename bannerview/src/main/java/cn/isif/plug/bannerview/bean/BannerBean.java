package cn.isif.plug.bannerview.bean;

import android.support.annotation.NonNull;

/**
 * Created by dell on 2016/6/6-8:54.
 */
public class BannerBean {
    @NonNull
    public Object url = "";//图片地址
    @NonNull
    public Object title = "";//标题

    @NonNull
    public Object getUrl() {
        return url;
    }

    public void setUrl(@NonNull Object url) {
        this.url = url;
    }

    @NonNull
    public Object getTitle() {
        return title;
    }

    public void setTitle(@NonNull Object title) {
        this.title = title;
    }
}
