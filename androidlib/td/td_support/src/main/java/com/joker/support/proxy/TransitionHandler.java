package com.joker.support.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/2/9.
 */

public class TransitionHandler implements InvocationHandler {
    private Object object;

    public TransitionHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(object, args);
        return result;
    }
}
