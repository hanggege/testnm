package com.mei.orc.http.retrofit;

import com.mei.orc.http.exception.CanceledException;
import com.mei.orc.http.response.DataResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.observers.ResourceObserver;

/**
 * Created by joker on 16/8/26.
 */
public abstract class RxRequest<RESULT extends DataResponse, R> {
    private Class<R> retrofitedInterfaceClass;
    private Class<RESULT> responseClass;
    private R service;
    private ResourceObserver observer;

    public RxRequest(Class<RESULT> responseClass, Class<R> retrofitedInterfaceClass) {
        this.responseClass = responseClass;
        this.retrofitedInterfaceClass = retrofitedInterfaceClass;
    }

    protected R getService() {
        return service;
    }

    protected void setService(R service) {
        this.service = service;
    }

    protected Class<R> getRetrofitedInterfaceClass() {
        return retrofitedInterfaceClass;
    }

    protected void setObserver(ResourceObserver observer) {
        this.observer = observer;
    }

    protected void cancel() {
        try {
            observer.onError(new CanceledException());
            observer.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract String createCacheKey();

    protected abstract Observable<RESULT> loadDataFromNetwork() throws Exception;

    protected Map<String, Object> hashJavaBeanToMap(Object ojt) {
        Class<?> cls = ojt.getClass();
        Field[] field = cls.getDeclaredFields();

        HashMap<String, Object> mapbean = new HashMap<String, Object>();
        field:
        for (int i = 0; i < field.length; i++) {
            Field f = field[i];
            f.setAccessible(true);
            try {
                if (f.get(ojt) != null) {
                    Annotation[] annotations = f.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof FieldFace) {
                            mapbean.put(((FieldFace) annotation).value(), f.get(ojt));
                            continue field;
                        }
                    }
                    mapbean.put(f.getName(), f.get(ojt));
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mapbean.remove("serialVersionUID");

        return mapbean;
    }


}
