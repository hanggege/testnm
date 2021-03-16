package com.mei.wood.extensions

import android.annotation.SuppressLint
import com.trello.rxlifecycle3.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.ResourceObserver

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/3/2.
 */
fun <T : Any> Observable<T>.subscribeBy(onNext: (T) -> Unit): ResourceObserver<T> {
    val resource = object : com.mei.wood.rx.ObserverSilentSubscriber<T>() {
        override fun onNext(t: T) {
            onNext(t)
        }
    }
    subscribe(resource)
    return resource
}

fun <T : Any> Observable<T>.appNetTransformer(): Observable<T> {
    return this.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

@SuppressLint("CheckResult")
fun <T : Any> Observable<T>.appLifeTransformer(former: Any): Observable<T> {
    if (former is LifecycleProvider<*>) {
        this.compose(former.bindToLifecycle())
    }
    return this
}