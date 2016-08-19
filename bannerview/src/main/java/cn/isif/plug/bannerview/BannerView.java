package cn.isif.plug.bannerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.isif.plug.bannerview.bean.BannerBean;
import cn.isif.plug.bannerview.exception.ClassTypeException;
import cn.isif.plug.bannerview.listener.OnBannerClickListener;
import cn.isif.plug.bannerview.util.BeanRefUtil;
import cn.isif.plug.bannerview.util.ViewFactor;

/**
 * 可配置的循环的banner
 * 基于Viewpager实现，可自动轮播
 * <p/>
 * Created by dell on 2016/5/31.
 */
public class BannerView extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager = null;
    private TextView bannerText = null;
    private PagerAdapter pagerAdapter = null;
    private int currentPage = 0; //当前页面
    private boolean isPool = true; //是否循环
    private long delayedTime = 3000; //播放时间 默认播放时间
    private boolean isWheel = false; //是否轮播
    private LinearLayout indicatorLayout = null;
    private TextView[] indicatorViews = null;
    private RelativeLayout rootLayout = null;
    private List<View> views = new ArrayList();
    private List<BannerBean> bannerBeans = null;
    private Handler mHandler = new BannerHandler(this);
    private OnBannerClickListener onBannerClickListener = null;
    private Runnable wheelRunnable = new AutoWheel();
    private int realPosition = 0;
    private static final int MSG_WHEEL = 1;
    private
    @DrawableRes
    int defPlaceHold = R.drawable._def;
    private
    @DrawableRes
    int errorPicture = R.drawable.def_error;
    private Context mContext = null;


    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BannerView, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();
        View rootView = View.inflate(context, R.layout.layout_bannerview, this);
        mViewPager = (ViewPager) rootView.findViewById(R.id.mViewPager);
        bannerText = (TextView) rootView.findViewById(R.id.banner_title);
        indicatorLayout = (LinearLayout) rootView.findViewById(R.id.indicatorLayout);
        rootLayout = (RelativeLayout) rootView.findViewById(R.id.root_);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(3);
        if (views != null && views.size() > 0) {
            rootLayout.setVisibility(View.VISIBLE);
        } else {
            rootLayout.setVisibility(View.INVISIBLE);
        }
        setAutoWheel(isWheel);
    }

    //属性配置
    protected void initByAttributes(TypedArray attributes) {
        isPool = attributes.getBoolean(R.styleable.BannerView_isPool, true);
        isWheel = attributes.getBoolean(R.styleable.BannerView_isWheel, false);
        delayedTime = attributes.getInt(R.styleable.BannerView_delayedTime, 3000);
        defPlaceHold = attributes.getResourceId(R.styleable.BannerView_defPlaceHold, R.drawable._def);
        errorPicture = attributes.getResourceId(R.styleable.BannerView_errorPicture, R.drawable.def_error);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        realPosition = position;
        if (isPool) {
            int max = views.size() - 1;
            if (position == 0) {
                realPosition = max - 1;
            } else if (position == max) {
                realPosition = 1;
            }
            realPosition = realPosition - 1;
        } else {
            realPosition = position;
        }
        bannerText.setText(bannerBeans.get(realPosition).title.toString());
    }

    @Override
    public void onPageSelected(int position) {
        realPosition = position;
        if (isPool) {
            int max = views.size() - 1;
            currentPage = realPosition;
            if (position == 0) {
                currentPage = max - 1;
            } else if (position == max) {
                currentPage = 1;
            }
            realPosition = currentPage - 1;
        } else {
            currentPage = position;
        }
        setIndicator(realPosition);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) { // viewPager滚动结束
            mViewPager.setCurrentItem(currentPage, false);
            mHandler.postDelayed(wheelRunnable, delayedTime);
        }
    }

    /**
     * 初始化指示器
     */
    private void initialIndicator() {
        indicatorLayout.removeAllViews();
        indicatorViews = new TextView[views.size()];
        if (isPool) {
            indicatorViews = new TextView[views.size() - 2];
        }
        for (int i = 0; i < indicatorViews.length; i++) {
            View view = View.inflate(mContext, R.layout
                    .layout_indicator, null);
            indicatorViews[i] = (TextView) view.findViewById(R.id.indicator_view);
            indicatorLayout.addView(view);
        }
        setIndicator(0);
    }

    /**
     * 设置指示器
     *
     * @param position
     */
    private void setIndicator(int position) {
        for (int i = 0; i < indicatorViews.length; i++) {
            indicatorViews[i].setBackgroundResource(R.drawable.banner_indicator_circle_light);
        }
        if (indicatorViews.length > position) {
            indicatorViews[position].setBackgroundResource(R.drawable.banner_indicator_circle_red);
        }
    }

    private void hasNext() {
        if (views == null || views.size() <= 0) return;
        int max = views.size() + 1;
        int position = (currentPage + 1) % views.size();
        mViewPager.setCurrentItem(position, true);
        if (position == max) { // 最后一页时回到第一页
            mViewPager.setCurrentItem(1, false);
        }
    }

    /**
     * 设置数据
     *
     * @param banners
     * @throws ClassTypeException
     */
    public void setData(List banners) throws ClassTypeException {
        setData(banners, 0);
    }

    /**
     * 设置数据
     *
     * @param banners
     * @param position
     * @throws ClassTypeException
     */
    public void setData(List banners, int position) throws ClassTypeException {
        List<BannerBean> bannerBeans = null;
        if (banners != null && banners.size() > 0) {
            bannerBeans = new ArrayList<>();
            for (Object b : banners) {
                BannerBean banner = BeanRefUtil.getBannerBran(b);
                if (banner == null) continue;
                bannerBeans.add(banner);
            }
        } else {
            rootLayout.setVisibility(View.INVISIBLE);
        }
        this.bannerBeans = bannerBeans;
        setViews(bannerBeans, position);
    }

    /**
     * 根据数据添加视图到banner上
     *
     * @param bannerBeans
     * @param position
     */
    private void setViews(List<BannerBean> bannerBeans, int position) {
        this.views.clear();//清除所有视图
        if (bannerBeans == null || bannerBeans.size() <= 0) return;

        if (isPool) {
            View viewFirst = ViewFactor.getImageView(mContext, bannerBeans.get(bannerBeans.size() - 1).url.toString(), defPlaceHold, errorPicture);
            views.add(viewFirst);
        }
        for (BannerBean bb : bannerBeans) {
            View viewContent = ViewFactor.getImageView(mContext, bb.url.toString(), defPlaceHold, errorPicture);
            views.add(viewContent);
        }
        if (isPool) {
            View viewEnd = ViewFactor.getImageView(mContext, bannerBeans.get(0).url.toString(), defPlaceHold, errorPicture);
            views.add(viewEnd);
        }

        if (views.size() == 0) {
            rootLayout.setVisibility(View.INVISIBLE);
            return;
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        pagerAdapter = new BannerAdapter();
        mViewPager.setAdapter(pagerAdapter);

        initialIndicator();//初始化指标器
        if (position < 0 || position >= views.size()) position = 0;
        if (isPool) {
            position = position + 1;
        }
        currentPage = position;
        pagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(currentPage);
    }

    /**
     * 返回值表示是否循环滑动
     *
     * @return
     */
    public boolean isPool() {
        return isPool;
    }


    /**
     * 设置自动轮播
     *
     * @param auto
     */
    public void setAutoWheel(boolean auto) {
        isWheel = auto;
        if (auto) {
            mHandler.postDelayed(wheelRunnable, delayedTime);
        }
    }

    /**
     * 设置item点击监听
     *
     * @param onBannerClickListener
     */
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        this.onBannerClickListener = onBannerClickListener;
    }

    public void removeOnBannerClickListener() {
        this.onBannerClickListener = null;
    }

    /**
     * 设置占位图
     * 该方法在setData()方法之前
     *
     * @param defPlaceHold
     */
    public void setDefPlaceHold(@DrawableRes int defPlaceHold) {
        this.defPlaceHold = defPlaceHold;
    }

    /**
     * 设置图片加载失败
     * 该方法在setData()方法之前
     *
     * @param errorPicture
     */
    public void setErrorPicture(@DrawableRes int errorPicture) {
        this.errorPicture = errorPicture;
    }

    /**
     * 返回得到自动延时播放的时间
     *
     * @return
     */
    public long getDelayedTime() {
        return delayedTime;
    }

    /**
     * 设置自动播放的时间
     *
     * @param delayedTime
     */
    public void setDelayedTime(long delayedTime) {
        if (delayedTime >= 1000) {
            this.delayedTime = delayedTime;
        }
    }

    class AutoWheel implements Runnable {
        @Override
        public void run() {
            if (isWheel) {
                mHandler.obtainMessage(MSG_WHEEL).sendToTarget();
            }
        }

    }

    static class BannerHandler extends Handler {
        private WeakReference<BannerView> weakReference;

        public BannerHandler(BannerView bannerView) {
            this.weakReference = new WeakReference<BannerView>(bannerView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference.get() == null) return;
            switch (msg.what) {
                case MSG_WHEEL:
                    weakReference.get().hasNext();
                    this.removeCallbacks(weakReference.get().wheelRunnable);
                    this.postDelayed(weakReference.get().wheelRunnable, weakReference.get().delayedTime);
                    break;
                default:
                    break;
            }
        }
    }

    class BannerAdapter extends PagerAdapter {
        public BannerAdapter() {
            super();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views.get(position);
            //设置item点击监听
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBannerClickListener != null) {
                        onBannerClickListener.onBannerClickListener(realPosition, v);
                    }
                }
            });
            container.addView(view);
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                container.removeView(views.get(position));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return views.size();
        }
    }
}
