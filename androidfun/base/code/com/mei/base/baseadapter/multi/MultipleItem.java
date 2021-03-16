package com.mei.base.baseadapter.multi;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by joker on 16/8/11.
 */
public class MultipleItem<T> implements MultiItemEntity {
    private int itemType;
    private T t;

    public MultipleItem(MultiItemType type, T t) {
        this.itemType = type.getType();
        this.t = t;
    }

    public T getData() {
        return t;
    }

    public void setData(T t) {
        this.t = t;
    }

    public MultiItemType getMulItemType() {
        return MultiItemType.ofType(this.itemType);
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(MultiItemType itemType) {
        this.itemType = itemType.getType();
    }
}
