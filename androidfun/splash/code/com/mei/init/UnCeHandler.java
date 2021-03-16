package com.mei.init;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.mei.orc.john.model.JohnUser;
import com.mei.orc.ui.toast.UIToast;
import com.mei.splash.ui.SplashActivity;
import com.mei.wood.util.AppManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by joker on 16/1/11.
 */
public class UnCeHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    public static final String TAG = "CatchExcep";
    Application application;
    // 用于格式化日期,作为日志文件名的一部分
    @SuppressLint("SimpleDateFormat")
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap();

    public UnCeHandler(Application application) {
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        JohnUser.getSharedUser().loadPersistent();
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    UIToast.toast(application.getApplicationContext(), "很抱歉");
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        //使用Toast来显示异常信息
        android.os.Process.killProcess(android.os.Process.myPid());
        AppManager.getInstance().finishAllActivity();
        Intent intent = new Intent(application.getApplicationContext(), SplashActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(
                application.getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //退出程序
        AlarmManager mgr = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用

        saveCrashInfoFile(ex);
        return true;
    }

    /**
     * 保存错误的信息
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        sb.append("------------------------start------------------------------\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append(getCrashInfo(ex));
        sb.append("\n------------------------end------------------------------");
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash/";
                File dir = new File(path);
                if (!dir.exists()) dir.mkdirs();
                // 创建新的文件
                if (!dir.exists()) dir.createNewFile();
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                // 答出log日志到控制台
                LogcatCrashInfo(path + fileName);
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saveCatchInfo2File() an error occured while writing file... Exception:");
        }
        return null;
    }

    /**
     * 得到程序崩溃的详细信息
     */
    public String getCrashInfo(Throwable ex) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        ex.setStackTrace(ex.getStackTrace());
        ex.printStackTrace(printWriter);
        printWriter.close();
        return result.toString();
    }

    /**
     * 将捕获导致崩溃的错误信息保存到SDCard 和输出到logcat
     *
     * @param fileName
     */
    private void LogcatCrashInfo(String fileName) {
        if (!new File(fileName).exists()) {
            return;
        }
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
            while (true) {
                s = reader.readLine();
                if (s == null)
                    break;
                Log.e(TAG, s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally { // 关闭流
            try {
                reader.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据时间删除信息
     */
    private void autoClear() {

    }
}
