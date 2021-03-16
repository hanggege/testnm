package com.mei.orc.http.retrofit;


import android.text.TextUtils;

import com.mei.orc.http.exception.ExceptionHandle;
import com.mei.orc.http.exception.RxThrowable;
import com.mei.orc.http.interceptor.MeiHttpInterceptor;
import com.mei.orc.http.interceptor.SpecialInterceptor;
import com.mei.orc.http.listener.RequestListener;
import com.mei.orc.http.log.BetterLogger;
import com.mei.orc.http.log.EmptyLoger;
import com.mei.orc.http.response.DataResponse;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by joker on 16/8/26.
 */
public abstract class RetrofitClient {
    protected static boolean isTestClient = false;
    protected static boolean isHttpScheme = false;
    public static List<String> requestUrlList = new ArrayList<>();//收集所有请求

    public static void setIsTestClient(boolean isTest) {
        isTestClient = isTest;
    }

    public static boolean isTestClient() {
        return isTestClient;
    }

    public static void setIsHttpScheme(boolean isHttpScheme) {
        RetrofitClient.isHttpScheme = isHttpScheme;
    }

    private Retrofit retrofit;
    private boolean isDebug;
    private List<SoftReference<RxRequest>> requestList = new ArrayList<>();
    private ObservableTransformer schedulersTransformer;

    public RetrofitClient() {
    }

    /**
     * project is debug set log visible
     */
    protected RetrofitClient setLogVisible(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public RetrofitClient create() {
        this.retrofit = getRetrofit();
        return this;
    }

    private Retrofit getRetrofit() {
        if (retrofit == null || !TextUtils.equals(retrofit.baseUrl().url().toString(), getBaseUrl())) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getOkHttp())
                    .build();
        }
        return retrofit;
    }

    /**
     * create service
     */
    public <T> T createService(Class<T> cla) {
        Retrofit retrofit = getRetrofit();
        return retrofit.create(cla);
    }

    /**
     * {@link OkHttpClient} client provider
     */
    private OkHttpClient getOkHttp() {
        synchronized (RetrofitClient.class) {
            OkHttpClient.Builder builder = ClientHelper.getClearOkHttpBuilder();
            /** {@link SpecialInterceptor} 这个是用来替换全部请求的，一定要在拦截器的最前面 **/
            builder.addInterceptor(new SpecialInterceptor());
            builder.addInterceptor(new MeiHttpInterceptor());
            Interceptor interceptor = getInterceptor();
            if (interceptor != null) {
                builder.addInterceptor(interceptor);
            }
            HttpLoggingInterceptor.Logger logger = isDebug ? BetterLogger.getInstance() : new EmptyLoger();
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(logger);
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logInterceptor);
            return builder.build();
        }
    }


    /**
     * baseurl provider
     */
    public abstract String getBaseUrl();

    /**
     * okhttp interceptor for add header or QueryParam
     */
    protected abstract Interceptor getInterceptor();

    /**
     * retrofit  return okhttp
     */

    public okhttp3.Call.Factory getOkClient() {
        return getRetrofit().callFactory();
    }

    /**
     * Execute request list
     */
    public void executeList(RequestListener<DataResponse> listener, RxRequest... request) {
        for (RxRequest item : request) {
            execute(item, listener);
        }
    }

    /**
     * Execute a request
     */
    public <RESULT extends DataResponse, R> void execute(final RxRequest<RESULT, R> request, final RequestListener<RESULT> listener) {
        try {
            final ResourceObserver<RESULT> subscriber = new ResourceObserver<RESULT>() {
                @Override
                public void onError(Throwable e) {
                    RxThrowable rxThrowable = ExceptionHandle.Companion.handleException(e);
                    listener.onRequestFailure(rxThrowable);
                    if (!isDisposed()) {
                        dispose();
                    }
                }

                @Override
                public void onComplete() {
                    if (!isDisposed()) {
                        dispose();
                    }
                }

                @Override
                public void onNext(RESULT result) {
                    if (result == null) {
                        listener.onRequestFailure(new RxThrowable(new NullPointerException("空"), 1000));
                    } else {
                        listener.onRequestSuccess(result);
                    }
                }
            };
            request.setService(createService(request.getRetrofitedInterfaceClass()));
            request.setObserver(subscriber);
            requestList.add(new SoftReference<>(request));
            request.loadDataFromNetwork()
                    .doOnComplete(() -> removeRequest(request))
                    .doOnDispose(() -> removeRequest(request))
                    .compose(this.appNetTransformer())
                    .subscribe(subscriber);
        } catch (Exception e) {
            RxThrowable rxThrowable = ExceptionHandle.Companion.handleException(e);
            listener.onRequestFailure(rxThrowable);
        }
    }


    private <T> ObservableTransformer<T, T> appNetTransformer() {
        if (schedulersTransformer == null) {
            schedulersTransformer = (ObservableTransformer<T, T>) upstream -> upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return schedulersTransformer;
    }


    /**
     * cancle request
     */
    public void cancel(RxRequest request) {
        request.cancel();
    }

    /**
     * remove request
     */
    private <RESULT extends DataResponse, R> void removeRequest(RxRequest<RESULT, R> request) {
        synchronized (requestList) {
            Iterator<SoftReference<RxRequest>> iterator = requestList.iterator();
            while (iterator.hasNext()) {
                RxRequest req = iterator.next().get();
                if (req == request) {
                    iterator.remove();
                }
            }
        }

    }

    /**
     * cancel all request
     */
    public void cancelAllRequest() {
        /*
         * 由于在发送onError取消请求之后，会删除requestList，遍历数字继续增加导致无法取到原本位置的数据。
         * 导致在activity刚打开的时候如果有多余一个请求发生并且关闭界面会导致部分请求不能被取消。
         * 创建新的列表来保存之前的数据，然后删除之前的list，全部清空。
         */
        List<SoftReference<RxRequest>> requestCopy = new ArrayList<>(requestList);
        for (int i = 0; i < requestCopy.size(); i++) {
            RxRequest indexRq = requestCopy.get(i).get();
            if (indexRq != null) indexRq.cancel();
        }
        requestCopy.clear();
        requestList.clear();
    }


}
