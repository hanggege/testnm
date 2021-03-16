package com.joker.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/28.
 */

public class TdUtil {

    /**
     * 连接多个字符串
     */
    public static String concate(Object... args) {
        if (args.length < 4) {
            String result = "";
            for (Object s : args) {
                result += s;
            }
            return result;
        }
        StringBuilder tmp = new StringBuilder();
        for (Object s : args) {
            tmp.append(s);
        }
        return tmp.toString();
    }

    /**
     * 按正方形裁切图片
     */
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        Bitmap newBitmap = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
        return createBitmapThumbnail(newBitmap);
    }

    /**
     * 图片压缩
     */
    private static Bitmap createBitmapThumbnail(Bitmap bitMap) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 99;
        int newHeight = 99;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
                matrix, true);
        return newBitMap;
    }
}
