package com.mei.orc.util.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.mei.orc.BuildConfig;

import java.util.List;

/**
 * Created by hkq325800 on 2017/4/14.
 */

public class JumpPermissionManagement {
    /**
     * Build.MANUFACTURER
     */
    private static final String MANUFACTURER_HUAWEI = "Huawei";//华为
    private static final String MANUFACTURER_MEIZU = "Meizu";//魅族
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";//小米
    private static final String MANUFACTURER_SONY = "Sony";//索尼
    private static final String MANUFACTURER_OPPO = "OPPO";
    private static final String MANUFACTURER_LG = "LG";
    private static final String MANUFACTURER_VIVO = "vivo";
    private static final String MANUFACTURER_SAMSUNG = "samsung";//三星
    private static final String MANUFACTURER_LETV = "Letv";//乐视
    private static final String MANUFACTURER_ZTE = "ZTE";//中兴
    private static final String MANUFACTURER_YULONG = "YuLong";//酷派
    private static final String MANUFACTURER_LENOVO = "LENOVO";//联想

    /**
     * 此函数可以自己定义
     *
     * @param activity
     */
    public static void GoToSetting(Activity activity) {
        switch (Build.MANUFACTURER) {
            case MANUFACTURER_HUAWEI:
                Huawei(activity);
                break;
            case MANUFACTURER_MEIZU:
                Meizu(activity);
                break;
            case MANUFACTURER_XIAOMI:
                Xiaomi(activity);
                break;
            case MANUFACTURER_SONY:
                Sony(activity);
                break;
            case MANUFACTURER_OPPO:
                OPPO(activity);
                break;
            case MANUFACTURER_LG:
                LG(activity);
                break;
            case MANUFACTURER_LETV:
                Letv(activity);
                break;
            default:
                ApplicationInfo(activity);
                Log.e("goToSetting", "目前暂不支持此系统");
                break;
        }
    }

    private static void Huawei(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.PACKAGE_NAME);
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        if (isIntentAvailable(activity, intent)) {
            startActivity(activity, intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    private static void Meizu(Activity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.PACKAGE_NAME);
        if (isIntentAvailable(activity, intent)) {
            startActivity(activity, intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    private static void Xiaomi(Activity activity) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.setComponent(componentName);
        intent.putExtra("extra_pkgname", BuildConfig.PACKAGE_NAME);
        if (isIntentAvailable(activity, intent)) {
            startActivity(activity, intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    private static void Sony(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.PACKAGE_NAME);
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        if (isIntentAvailable(activity, intent)) {
            startActivity(activity, intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    private static void OPPO(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.PACKAGE_NAME);
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        if (isIntentAvailable(activity, intent)) {
            startActivity(activity, intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    private static void LG(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.PACKAGE_NAME);
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        if (isIntentAvailable(activity, intent)) {
            startActivity(activity, intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    private static void Letv(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.PACKAGE_NAME);
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        if (isIntentAvailable(activity, intent)) {
            startActivity(activity, intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    /**
     * 只能打开到自带安全软件
     *
     * @param activity
     */
    private static void _360(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", BuildConfig.PACKAGE_NAME);
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        startActivity(activity, intent);
    }

    /**
     * 应用信息界面
     *
     * @param activity
     */
    public static void ApplicationInfo(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= 9) {
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            localIntent.setAction(Intent.ACTION_VIEW);
//            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
//        }
        if (isIntentAvailable(activity, localIntent)) {
            activity.startActivity(localIntent);
        } else {
            SystemConfig(activity);
        }

    }

    /**
     * 系统设置界面
     *
     * @param activity
     */
    public static void SystemConfig(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(activity, intent);
    }

    /**
     * 检查Intent是否可用
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() > 0) {
            return true;
        }
        return false;
    }

    private static void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
