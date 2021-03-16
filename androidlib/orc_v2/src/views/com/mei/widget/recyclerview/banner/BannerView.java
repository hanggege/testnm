package com.mei.widget.recyclerview.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.mei.orc.R;
import com.mei.orc.util.click.DoubleClickKt;
import com.mei.widget.recyclerview.manager.PagerScaleLayoutManger;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/9
 */
@SuppressWarnings("unchecked")
public class BannerView extends FrameLayout {
    private BannerAdapter bannerAdapter;
    private PagerScaleLayoutManger bannerLayoutManager;
    private RecyclerView bannerRecycler;
    private int duration;
    private boolean needReverse;//是否向前滚动
    private List listData;
    private BannerHolder holder;

    private boolean isStart = false;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            DoubleClickKt.isNotOnDoubleClick(duration / 2, "BannerView" + this.hashCode(), new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    // 这里会出现 -1的值时，出现了快速移动到0
                    int indexCount = bannerLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (indexCount < 0)
                        indexCount = bannerLayoutManager.findFirstVisibleItemPosition();
                    if (bannerLayoutManager.getItemCount() > 1 && indexCount > 0)
                        bannerRecycler.smoothScrollToPosition(needReverse ? indexCount - 1 : indexCount + 1);
                    if (duration > 0) {
                        postDelayed(timerRunnable, duration);
                    }
                    return null;
                }
            });
        }
    };

    public BannerView(Context context) {
        this(context, null, 0);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        duration = typeArray.getInt(R.styleable.BannerView_duration, 4000);
        typeArray.recycle();
        listData = new ArrayList();
        bannerRecycler = new RecyclerView(context);
        bannerRecycler.setLayoutManager(bannerLayoutManager = new PagerScaleLayoutManger(getContext(), RecyclerView.HORIZONTAL, 1));
        bannerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isAttachedToWindow()) {
                    startRunnable();
                } else {
                    endRunnable();
                }
            }
        });
    }


    private <T> BannerAdapter getBannerAdapter(List<T> list, BannerHolder<T> holder) {
        if (bannerAdapter == null) {
            bannerAdapter = new BannerAdapter<T>(holder, list);
        }
        return bannerAdapter;
    }


    private void startRunnable() {
        try {
            if (isStart) return;
            if (duration > 0) {
                postDelayed(timerRunnable, duration);
                isStart = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endRunnable() {
        try {
            removeCallbacks(timerRunnable);
            isStart = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRunnable();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        endRunnable();
    }

    /**
     * 设置轮播时长,单位（秒）
     */
    public BannerView setDuration(int duration) {
        this.duration = duration * 1000;
        return this;
    }

    /**
     * 是否向前滚动
     */
    public BannerView setReverse(boolean reverse) {
        this.needReverse = reverse;
        return this;
    }

    /**
     * 滑动监听
     */
    public BannerView setSelectedListener(PagerScaleLayoutManger.onSelectedListener
                                                  selectedListener) {
        bannerLayoutManager.setSelectedListener(selectedListener);
        return this;
    }

    /**
     * 构建，在最后一步
     */
    @SuppressWarnings("unchecked")
    public <T> void buildPager(List<T> list, BannerHolder<T> holder) {
        listData.clear();
        listData.addAll(list);
        this.holder = holder;
        removeAllViews();
        if (list.size() == 1) {
            View item = holder.createView(getContext());
            addView(item);
            holder.updateUI(item, 0, list.get(0));
        } else {
            addView(bannerRecycler, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            bannerRecycler.setAdapter(getBannerAdapter(listData, holder));
            bannerRecycler.getAdapter().notifyDataSetChanged();
            bannerRecycler.scrollToPosition(list.size() * 1000);
            startRunnable();
        }
    }

    public <T> void refreshList(List<T> list, T changeItem) {
        if (listData.isEmpty()) return;
        listData.clear();
        listData.addAll(list);
        if (list.size() == 1) {
            View item = holder.createView(getContext());
            addView(item);
            holder.updateUI(item, 0, list.get(0));
        } else {
            int curPos = bannerLayoutManager.getCurrentPosition();
            int prePos = Math.max(0, curPos - 50);
            int count = Math.min(100, bannerAdapter.getItemCount());
            bannerAdapter.notifyItemRangeChanged(prePos, count, changeItem);
            startRunnable();
        }
    }

    public void notifyItem() {
        if (listData.size() > 0) {
            if (listData.size() == 1) {
                holder.updateUI(this, 0, listData.get(0));
            } else if (bannerRecycler.getAdapter() != null)
                bannerRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    public void setOrientation(@RecyclerView.Orientation int orientation) {
        bannerRecycler.setLayoutManager(bannerLayoutManager = new PagerScaleLayoutManger(getContext(), orientation, 1));
    }

    public BannerView setScrollDuration(float second) {
        bannerLayoutManager.setScrollDuration(second);
        return this;
    }


}


