package com.mei.orc;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mei.orc.util.json.JsonExtKt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity启动器
 */
public final class ActivityLauncher {
    public static final String method_default = "bind";

    public static void bind(@NonNull Activity target) {
        innerFill(target);
    }

    public static <T> T bind(@NonNull Activity target, Class<T> tClass, String regex) {
        String link = target.getIntent().getDataString();
        if (!TextUtils.isEmpty(link) && link.matches(regex)) {
            return JsonExtKt.json2Obj(getOneID(link, regex), tClass);
        } else {
            innerFill(target);
            return null;
        }
    }

    public static void bind(@NonNull Fragment target) {
        if (target.getArguments() != null)
            innerFill(target);
    }

    public static void bind(@NonNull androidx.fragment.app.Fragment target) {
        if (target.getArguments() != null)
            innerFill(target);
    }

    public static void bind(@NonNull BroadcastReceiver target, @NonNull Intent intent) {
        innerFill(target, intent, Intent.class);
    }

    /**
     * 还没考虑齐全，暂时不放出
     */
//    public static void bind(@NonNull Service target, @NonNull Intent intent) {
//        innerFill(target, intent, Intent.class);
//    }
    private static void innerFill(@NonNull Object target) {
        Class<?> targetClass = target.getClass();
        Class<?> starterClass = getStarterClass(targetClass);
        if (starterClass == null) return;
        Method method = getMethod(starterClass, method_default, targetClass);
        if (method == null) return;
        invokeMethod(method, target);
    }

    private static void innerFill(@NonNull Object target, Object otherArg, Class<?> otherClass) {
        Class<?> targetClass = target.getClass();
        Class<?> starterClass = getStarterClass(targetClass);
        if (starterClass == null) return;
        Method method = getMethod(starterClass, method_default, targetClass, otherClass);
        if (method == null) return;
        invokeMethod(method, target, otherArg);
    }

    private static Class<?> getStarterClass(Class<?> targetClass) {
        String clsName = targetClass.getName();
        String starterName = clsName + "Launcher";
        try {
            return Class.forName(starterName);
        } catch (ClassNotFoundException e) {
            // No binding for this class
            return null;
        }
    }

    private static Method getMethod(Class<?> starterClass, String methodName, Class<?>... classes) {
        try {
            return starterClass.getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find " + methodName + " method for " + starterClass.getName(), e);
        }
    }

    private static void invokeMethod(@NonNull Method method, @NonNull Object... args) {
        try {
            method.invoke(null, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + method + "because of illegal access", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to invoke " + method + " because of illegal argument", e);
        } catch (InvocationTargetException e) {
            throwInvokeMethodError(e);
        }
    }

    private static void throwInvokeMethodError(InvocationTargetException e) {
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        }
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        throw new RuntimeException("Unable to create binding instance.", cause);
    }

    private static String getOneID(String url, String patternStr) {
        String Id = "";
        if (!TextUtils.isEmpty(url)) {
            if (url.matches(patternStr)) {
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(url);
                if (matcher.find() && matcher.groupCount() == 1) {
                    Id = matcher.group(1);
                }
            }
        }
        return Id;
    }
}
