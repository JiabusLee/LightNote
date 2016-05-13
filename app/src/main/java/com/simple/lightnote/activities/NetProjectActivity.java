package com.simple.lightnote.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.adapter.FuliAdapter;
import com.simple.lightnote.model.ArticleInfo;
import com.simple.lightnote.model.FuLi;
import com.simple.lightnote.net2.ReceiveData;
import com.simple.lightnote.net2.RestClient;
import com.simple.lightnote.net2.ViewHolder;
import com.simple.lightnote.utils.LogUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by homelink on 2016/5/13.
 */
public class NetProjectActivity extends BaseActivity {
    private static final String TAG = "NetProjectActivity";
    @Bind(R.id.main_simple_listview)
    ListView listview;


    List<ArticleInfo> articleInfoList;
    List<FuLi> fuliList;
    private FuliAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_simple_listview);
        ButterKnife.bind(this);
        adapter = new FuliAdapter(this,fuliList);
        listview.setAdapter(adapter);
    /*    RestClient.api().articles(10,20).enqueue(new Callback<ReceiveData.ArticleListResponse>() {
            @Override
            public void onResponse(Call<ReceiveData.ArticleListResponse> call, Response<ReceiveData.ArticleListResponse> response) {
                articleInfoList = response.body().results;
                Log.e(TAG, "onResponse: " +articleInfoList );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ReceiveData.ArticleListResponse> call, Throwable t) {
                Toast.makeText(NetProjectActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });*/
        RestClient.api().fulis(20,1).enqueue(new Callback<ReceiveData.FuliResponse>() {
            @Override
            public void onResponse(Call<ReceiveData.FuliResponse> call, Response<ReceiveData.FuliResponse> response) {
                LogUtils.e(TAG,"fulis"+response);
                fuliList = response.body().results;
                adapter.setList(fuliList);
                Log.e(TAG, "onResponse: " +articleInfoList );
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onFailure(Call<ReceiveData.FuliResponse> call, Throwable t) {
                LogUtils.e(TAG,t);
            }
        });
    }


    private class ArticleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return articleInfoList==null?0:articleInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null ){
                convertView = LayoutInflater.from(NetProjectActivity.this).inflate(R.layout.item_article,null);
            }

            ImageView imageView = ViewHolder.get(convertView,R.id.img);
            TextView titleTxt = ViewHolder.get(convertView,R.id.title);

            ArticleInfo info = articleInfoList.get(position);

            titleTxt.setText(info.desc);

            Glide.with(NetProjectActivity.this).load("http://ww4.sinaimg.cn/large/7a8aed7bjw1ezrtpmdv45j20u00spahy.jpg").into(imageView);

            return convertView;
        }
    }
}
