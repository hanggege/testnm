package com.mei.wood;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mei.orc.http.retrofit.RetrofitClient;
import com.mei.orc.ui.toast.UIToast;
import com.mei.orc.util.handler.GlobalUIHandler;
import com.mei.orc.util.ui.UIUtilKt;
import com.mei.wood.ui.MeiCustomBarActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/28
 * 统计当前启动过程中请求的接口
 */
public class ForClientRequestActivity extends MeiCustomBarActivity {


    RecyclerView recyclerView;

    TextView requestResult;

    ScrollView requestContainer;

    RequestAdapter adapter;


    private void findView() {
        recyclerView = findViewById(R.id.recycler_view);
        requestResult = findViewById(R.id.request_result);
        requestContainer = findViewById(R.id.request_container);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.for_client_request);
        findView();
        setTitle("当前启动过程中请求的接口");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new RequestAdapter());

        findViewById(R.id.clear_request).setOnClickListener(v -> {
            RetrofitClient.requestUrlList.clear();
            adapter.notifyDataSetChanged();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickLeft(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (requestContainer.getVisibility() == View.VISIBLE) {
            adapter.notifyDataSetChanged();
            UIUtilKt.setViewGone(true, requestContainer);
        } else {
            super.onBackPressed();
        }
    }


    class RequestAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(15, 6, 15, 6);
            textView.setId(android.R.id.text2);
            textView.setTextIsSelectable(true);
            return new BaseViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            holder.setText(android.R.id.text2, getItem(position));
            holder.itemView.setOnClickListener(v -> {
                String url = getItem(position).substring(getItem(position).indexOf("http"));
                Request request = new Request.Builder().url(url).build();
                showLoading(true);
                getApiSpiceMgr().getOkClient().newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                GlobalUIHandler.postUi(() -> {
                                    UIToast.toast(getActivity(), "请求失败");
                                    showLoading(false);
                                });

                            }

                            @Override
                            public void onResponse(Call call, Response response) {
                                GlobalUIHandler.postUi(() -> {
                                    showLoading(false);
                                    UIToast.toast(getActivity(), "请求成功");
                                    if (response.body() != null) {
                                        try {
                                            requestResult.setText(JsonFormatUtil.formatJson(response.body().string()));
                                            UIUtilKt.setViewVisibleOrGone(true, requestContainer);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        });

            });
        }

        @Override
        public int getItemCount() {
            return RetrofitClient.requestUrlList.size();
        }

        public String getItem(int position) {
            return RetrofitClient.requestUrlList.get(getItemCount() - 1 - position);
        }
    }


}
