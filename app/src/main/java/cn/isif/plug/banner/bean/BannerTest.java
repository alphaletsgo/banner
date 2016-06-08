package cn.isif.plug.banner.bean;

import cn.isif.plug.bannerview.bean.anno.Banner;

/**
 * Created by dell on 2016/6/6-15:15.
 */
public class BannerTest {
    public String name;
    @Banner("url")
    public String path;
    @Banner("title")
    public String oot;
    public int number;
    public boolean ifs;
}
