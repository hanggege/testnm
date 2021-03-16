package com.mei.widget.recyclerview.banner;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

@SuppressWarnings("unchecked")
public class BannerAdapter<T> extends RecyclerView.Adapter<BannerViewHolder> {
    private com.mei.widget.recyclerview.banner.BannerHolder<T> bannerHolder;
    private List<T> listData;

    public BannerAdapter(@NonNull com.mei.widget.recyclerview.banner.BannerHolder<T> holder, List<T> list) {
        this.bannerHolder = holder;
        this.listData = list;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BannerViewHolder<T>(parent, bannerHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.updateUI(position, listData.get(position % listData.size()));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.updateCertainItem(position, payloads.get(0));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size() > 1 ? Integer.MAX_VALUE : listData.size();
    }
}

class BannerViewHolder<T> extends RecyclerView.ViewHolder {

    private com.mei.widget.recyclerview.banner.BannerHolder<T> bannerHolder;
    private View parentView;

    public BannerViewHolder(View parent, @NonNull BannerHolder<T> holder) {
        super(holder.createView(parent.getContext()));
        this.parentView = parent;
        bannerHolder = holder;
    }

    public void updateUI(int position, T t) {
        itemView.setLayoutParams(new ViewGroup.LayoutParams(parentView.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));
        bannerHolder.updateUI(itemView, position, t);
    }

    public void updateCertainItem(int position, T t) {
        itemView.setLayoutParams(new ViewGroup.LayoutParams(parentView.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));
        bannerHolder.updateCertainUI(itemView, position, t);
    }
}
