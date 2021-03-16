package com.mei.orc.util.file;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.mei.orc.Cxt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

/**
 * Created by guoyufeng on 7/5/15.
 */
public class OrcFileUtil {
    public static String tag = OrcFileUtil.class.getSimpleName();
    public static final String CACHE_DIR = "cache";
    public static final String ROOT_DIR = "Android/data/" + getPackageName();


    public enum FileType {
        image,
        temp,
        text
    }

    /**
     * 受保护的目录,意味着清楚缓存的时候不该清除这个目录
     */
    public synchronized static File getProtectDir(FileType fileType, String... subPaths) {
        File file;
        try {
            file = getDir("protect", fileType, subPaths);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized static File getDir(String rootName, FileType fileType, String... subPaths) {
        StringBuilder path = new StringBuilder(Cxt.get().getFilesDir().getPath() + "/" + rootName + "/" + fileType.name());
        if (!TextUtils.isEmpty(path)) {
            for (String dir : subPaths) {
                path.append("/").append(dir);
            }
        }
        File dir = new File(path.toString());
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new RuntimeException("create dir failed: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }

    public static boolean writeString(String string, String fileName) {
        FileOutputStream stream = null;
        try {
            File file = new File(fileName);
            stream = new FileOutputStream(file);
            stream.write(string.getBytes());
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        return false;
    }

    public static String readString(String fileName) {

        FileInputStream stream = null;
        try {
            File file = new File(fileName);
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            stream = new FileInputStream(file);
            try {
                //noinspection ResultOfMethodCallIgnored
                stream.read(bytes);
            } finally {
                stream.close();
            }

            return new String(bytes);
        } catch (Exception ignored) {
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取缓存目录
     */
    public static String getCacheDir() {
        return getDir(CACHE_DIR);
    }

    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    public static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }

    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param path 获取某个目录的可用空间大小
     */
    public static long getAvailableSize(String path) {

        //获取分区总数,每个分区的大小,获取可用的分区
        StatFs stafs = new StatFs(path);
        long blockSize = stafs.getBlockSizeLong();
        long availableBlocks = stafs.getAvailableBlocksLong();
        return blockSize * availableBlocks;
    }

    /**
     * 获取手机总存储
     */
    public static String getTotalSize(Context context) {
        return formatFileSize(context, getTotalSizeNumber(context));
    }

    public static long getTotalSizeNumber(Context context) {
        String path;
        if (OrcFileUtil.isSDCardAvailable()) {
            //获得sd卡可用空间大小
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            path = Environment.getDataDirectory().getAbsolutePath();

        }
        return OrcFileUtil.getTotalSize(path);
    }

    /**
     * @param path 获取某个目录的总空间大小
     */
    public static long getTotalSize(String path) {

        //获取分区总数,每个分区的大小,获取可总的分区
        StatFs stafs = new StatFs(path);

        long blockCount = stafs.getBlockCountLong();
        long blockSize = stafs.getBlockSizeLong();
        return blockSize * blockCount;
    }

    /**
     * 获取已被占用空间
     */
    public static String getUsedSize(Context context) {

        if (OrcFileUtil.isSDCardAvailable()) {
            //获得sd卡可用空间大小
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            long usedSize = getTotalSize(path) - getAvailableSize(path);
            return OrcFileUtil.formatFileSize(context, usedSize);
        } else {
            //获取内存可用的大小
            String path1 = Environment.getDataDirectory().getAbsolutePath();
            long usedROMSize = getTotalSize(path1) - getAvailableSize(path1);
            return formatFileSize(context, usedROMSize);
        }
    }

    /**
     * 获取手机可用空间
     */
    public static String getAvailableSize(Context context) {
        return formatFileSize(context, getAvailableSizeToNumber(context));
    }

    public static long getAvailableSizeToNumber(Context context) {
        if (OrcFileUtil.isSDCardAvailable()) {
            //获得sd卡可用空间大小
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            return getAvailableSize(path);
        } else {
            //获取内存可用的大小
            String path1 = Environment.getDataDirectory().getAbsolutePath();
            return getAvailableSize(path1);
        }
    }

    /**
     * 获取SD下的应用目录
     */
    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                ROOT_DIR +
                File.separator;
    }

    /**
     * 获取应用的cache目录
     */
    public static String getCachePath() {
        File f = Cxt.get().getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

    public static boolean createFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.isFile();
        }
        try {
            return new File(path).createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 得到应用程序的包名
     */
    private static String getPackageName() {
        return Cxt.get().getPackageName();
    }

    public static String formatFileSizeMK(Context context, long size) {
        if (size / 1024f > 1024) {
            return formatFileSize(context, size);
        } else {
            DecimalFormat df = new DecimalFormat("#,##0.## KB");
            return df.format(size / 1024f);
        }
    }

    public static String formatFileSize(Context context, long size) {
        DecimalFormat df = new DecimalFormat("#,##0.## M");
        Float si = size / 1024f / 1024f;
        return df.format(si);
    }
}
