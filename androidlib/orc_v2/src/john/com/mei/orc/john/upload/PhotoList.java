package com.mei.orc.john.upload;

import com.mei.orc.util.string.StringUtilKt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * create by guoyufeng 3/6/15 15:07
 */
public class PhotoList extends LinkedList<Photo> {
    private OnListChangeListener onListChangeListener;
    private int hashStamp;

    public PhotoList() {
    }

    public void setOnListChangeListener(OnListChangeListener listener) {
        resetHashStamp();
        this.onListChangeListener = listener;
    }

    public void resetHashStamp() {
        this.hashStamp = hashCode();
    }

    public PhotoList(Photo[] finishedUpload) {
        for (Photo photo : finishedUpload) {
            add(photo);
        }
    }

    public List<Photo> getNewPhotos() {
        ArrayList<Photo> photos = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            Photo photo = get(i);
            if (photo.getId() == 0) {
                photos.add(photo);
            }
        }
        return photos;
    }

    @Override
    public boolean add(Photo object) {
        boolean result = super.add(object);
        notifyChange();
        return result;
    }

    @Override
    public void add(int location, Photo object) {
        super.add(location, object);
        notifyChange();
    }

    @Override
    public boolean remove(Object object) {
        boolean result = super.remove(object);
        notifyChange();
        return result;
    }

    @Override
    public Photo remove(int location) {
        Photo result = super.remove(location);
        notifyChange();
        return result;
    }

    public void notifyChange() {
        if (onListChangeListener != null) {
            onListChangeListener.onPhotoListChanged(hashCode() != hashStamp);
        }
    }

    public String getPhotoIdListStr() {
        return StringUtilKt.concateWithSplit(",", this);
    }

    public interface OnListChangeListener {

        void onPhotoListChanged(boolean changed);
    }
}