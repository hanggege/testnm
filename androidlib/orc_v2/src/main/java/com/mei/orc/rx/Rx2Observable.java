package com.mei.orc.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by joker on 2017/9/19.
 */

@SuppressWarnings("unchecked")
public abstract class Rx2Observable<T> extends Observable<T> {
    @CheckReturnValue
    @SchedulerSupport("none")
    public static <T> Observable<T> createNonNull(ObservableOnSubscribe<T> source) {
        ObjectHelper.requireNonNull(source, "source is null");
        Rx2Observable<T> ob = (Rx2Observable<T>) RxJavaPlugins.onAssembly(new ObservableCreate<T>(source));
        return ob.filter(new Predicate<T>() {
            @Override
            public boolean test(@NonNull T t) throws Exception {
                return t != null;
            }
        });
    }

}
