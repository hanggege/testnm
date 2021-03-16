package com.mei.wood.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mei.orc.Cxt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * Created by 杨强彪 on 2016/10/9.
 *
 * @描述：
 */

public class AppManager {

    private static Stack<Activity> activityStack = new Stack<>();
    private static AppManager instance;
    private static final String TAG = AppManager.class.getSimpleName();

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        return (activityStack != null && !activityStack.isEmpty()) ? activityStack.lastElement() : null;
    }

    @Nullable
    public Activity preActivity() {
        return activityStack.size() > 1 ? activityStack.get(activityStack.size() - 2) : null;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishCurrentActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束后的activity移出管理栈
     */
    public void removeActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i).getClass().equals(cls)) {
                finishActivity(activityStack.get(i));
            }
        }
    }

    /**
     * 获取指定类名的Activity
     */
    public Activity getTargetActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (TextUtils.equals(activity.getClass().getName(), cls.getName())) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 是否有传入的cls类
     *
     * @param classes 类表
     * @return 在栈中存在的传入项目，取存在的类
     */
    public List<Activity> hasActivitys(Class<?>... classes) {
        List<Activity> list = new LinkedList<>();
        for (Class<?> aClass : classes) {
            Activity activity = getTargetActivity(aClass);
            if (activity != null) list.add(activity);
        }
        return list;
    }


    /**
     * 结束除当前class的所有Activity
     */
    public synchronized void finishOtherAllActivity(Class<?> classes) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity != null && !activity.getClass().getName().equals(classes.getName())) {
                iterator.remove();
                activity.finish();
            }
        }
    }


    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack.size() > 0) {
            for (int i = 0; i < activityStack.size(); i++) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void appExit(Context context) {
        try {
            if (context == null) {
                context = Cxt.get();
            }
            finishAllActivity();
//            ActivityManager activityMgr = (ActivityManager) context
//                    .getSystemService(Context.ACTIVITY_SERVICE);
//            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**----------------------  栈信息  --------------------------*/

    /**
     * 获取栈名称列表
     */
    public List<String> getActivityNameList() {
        List<String> activityNameList = new ArrayList<>();
        for (Activity activity : activityStack) {
            activityNameList.add(activity.getClass().getSimpleName());
        }
        return activityNameList;
    }

    public List<String> getActivityNameList(Context context) {
        List<String> names = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : list) {
            names.add(resolveInfo.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 打印栈信息
     */
    public void printActivityNameList() {
        for (Activity activity : activityStack) {
            Log.d(TAG, activity.getClass().getSimpleName());
        }
    }
}
