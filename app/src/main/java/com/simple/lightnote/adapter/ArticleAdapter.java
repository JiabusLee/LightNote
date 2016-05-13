package com.simple.lightnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simple.lightnote.R;
import com.simple.lightnote.model.ArticleInfo;
import com.simple.lightnote.net2.ViewHolder;

import java.util.List;

public class ArticleAdapter extends BaseAdapter {
    Context context;
    List<ArticleInfo> articleInfoList;

    public ArticleAdapter(Context context, List<ArticleInfo> articleInfoList) {
    }

    @Override
    public int getCount() {
        return articleInfoList == null ? 0 : articleInfoList.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_article, null);
        }

        ImageView imageView = ViewHolder.get(convertView, R.id.img);
        TextView titleTxt = ViewHolder.get(convertView, R.id.title);

        ArticleInfo info = articleInfoList.get(position);

        titleTxt.setText(info.desc);

        Glide.with(context).load("http://ww4.sinaimg.cn/large/7a8aed7bjw1ezrtpmdv45j20u00spahy.jpg").into(imageView);

        return convertView;
    }
}