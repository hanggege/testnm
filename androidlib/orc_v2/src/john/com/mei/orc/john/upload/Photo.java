package com.mei.orc.john.upload;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.mei.orc.Cxt;

public class Photo {
    private int id;
    private String uri = "";
    private String localUri = "";

    public Photo(int id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public Photo(String uri) {
        this.uri = uri;
    }

    public Photo(int id, String uri, String localPath) {
        this.id = id;
        this.uri = uri;
        this.localUri = localPath;
    }

    public String getLocalPath() {
        if (uri == null) {
            return "";
        }
        if (uri.startsWith("file://")) {
            return uri.replace("file://", "");
        } else if (uri.startsWith("content://")) {
            return content2Path(uri);
        }
//        else {
//            throw new RuntimeException("can not decode local path from uri:" + uri);
//        }
        return "";
    }

    private String content2Path(String contentUri) {
        Cursor cursor = null;
        try {
            String[] data = {MediaStore.Images.Media.DATA};
            cursor = Cxt.get().getContentResolver().query(Uri.parse(contentUri), data, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLocalUri() {
        return localUri;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}