package com.mei.orc.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * 佛祖保佑         永无BUG 权限检查
 * <p>
 * Created by joker on 2017/1/5.
 */

public class PermissionCheck {
    /**
     * 决断一个到多个权限是否有 多个时只要有一个权限没有都返回false;
     */
    public static boolean hasPermission(Context context, String... permission) {
        boolean has = true;
        for (String per : permission) {
            int perm = context.checkCallingOrSelfPermission(per);
            if (perm != PackageManager.PERMISSION_GRANTED) {
                has = false;
            }
        }
        return has;
    }

    public static boolean hasPermission(Context context, List<String> permission) {
        boolean has = true;
        for (String per : permission) {
            int perm = context.checkCallingOrSelfPermission(per);
            if (perm != PackageManager.PERMISSION_GRANTED) {
                has = false;
            }
        }
        return has;
    }

    /**
     * //     * 跳转权限设置界面
     * //
     */
    public static void gotoPermissionSetting(Activity activity) {
        JumpPermissionManagement.GoToSetting(activity);
    }


}
