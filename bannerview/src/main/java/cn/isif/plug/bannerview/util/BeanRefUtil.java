package cn.isif.plug.bannerview.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.isif.plug.bannerview.bean.BannerBean;
import cn.isif.plug.bannerview.bean.anno.Banner;
import cn.isif.plug.bannerview.exception.ClassTypeException;

/**
 * Created by dell on 2016/6/6-9:07.
 */
public class BeanRefUtil {
    /**
     * 获取BannerBean对象
     *
     * @param obj
     * @return
     */
    public static BannerBean getBannerBran(Object obj) throws ClassTypeException {
        BannerBean bannerBean = null;
        Class clz = obj.getClass();
        Field[] fields = clz.getFields();
        for (Field f : fields) {
            boolean isBanner = f.isAnnotationPresent(Banner.class);
            if (!isBanner) continue;

            if (bannerBean == null) bannerBean = new BannerBean();
            try {
                String methodName = getMethodName(f);
                Method m = bannerBean.getClass().getMethod(methodName, Object.class);
                f.setAccessible(true);
                String value = "";
                try {
                    value = f.get(obj).toString();
                }catch (Exception e){
                    value = "";
                }
                m.invoke(bannerBean, value);
            } catch (Exception e) {
                throw new ClassTypeException("Error,ClassType Exception");
            }
        }

        return bannerBean;
    }

    /**
     * 获取字段的方法名
     *
     * @param field
     * @return
     */
    private static String getMethodName(Field field) {
        Banner b = field.getAnnotation(Banner.class);
        String bv = b.value();
        return "set" + bv.substring(0, 1).toUpperCase() + bv.substring(1);
    }
}
