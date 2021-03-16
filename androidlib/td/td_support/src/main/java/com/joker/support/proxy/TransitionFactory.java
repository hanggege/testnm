package com.joker.support.proxy;

import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/2/9.
 */

public class TransitionFactory {


    @SuppressWarnings("unchecked")
    @Nullable
    public static <R> R create(String className, RfParams params) {
        R transition = null;
        Object object = newInstance(className, params);
        if (object != null) {
            TransitionHandler handler = new TransitionHandler(object);
            transition = (R) Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), handler);
        }
        return transition;
    }


    public static <R> R newInstance(String className, RfParams params) {
        R result = null;
        try {
            Class<?> cls = Class.forName(className);
            Constructor<?> cst = null;
            if (RfParams.isEmpty(params)) {
                cst = cls.getConstructor();
                cst.setAccessible(true);
                result = (R) cst.newInstance();
            } else {
                cst = cls.getConstructor(params.getClasses());
                cst.setAccessible(true);
                result = (R) cst.newInstance(params.getObjects());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


    public static class RfParams {

        private ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        private ArrayList<Object> objects = new ArrayList<Object>();

        public RfParams(Class<?> cls, Object obj) {
            append(cls, obj);
        }

        public RfParams() {
        }

        public RfParams append(Class<?> cls, Object obj) {
            if (cls == null) {
                throw new NullPointerException("class can't be null!");
            }
            classes.add(cls);
            objects.add(obj);
            return this;
        }

        public Class<?>[] getClasses() {
            Class<?>[] result = new Class<?>[classes.size()];
            return classes.toArray(result);
        }

        public Object[] getObjects() {
            Object[] result = new Object[classes.size()];
            return objects.toArray(result);
        }

        public static boolean isEmpty(RfParams params) {
            return params == null || params.classes == null || params.classes.size() == 0;
        }
    }

}
