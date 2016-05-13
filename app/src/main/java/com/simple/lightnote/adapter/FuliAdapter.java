package com.simple.lightnote.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simple.lightnote.R;
import com.simple.lightnote.model.FuLi;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by homelink on 2016/5/13.
 */
public class FuliAdapter extends BaseAdapter {
    public List<FuLi> lists;
    public Context mContext;

    public FuliAdapter(Context context, List<FuLi> lists) {
        this.mContext = context;
        this.lists = lists;

    }

    @Override
    public int getCount() {
        return lists != null ? lists.size() : 0;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_fuli, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder= (ViewHolder) convertView.getTag();

        FuLi fuLi = lists.get(position);
        holder.desc.setText(fuLi.desc);
        holder.from.setText(fuLi.source);
        holder.title.setText(fuLi.type);
        holder.time.setText(fuLi.createdAt);
        Glide.with(mContext).load(fuLi.url).into(holder.img);

        return convertView;
    }

    public void setList(List<FuLi> fuliList) {
        this.lists=fuliList;
    }

    public static class ViewHolder {
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.from)
        TextView from;
        @Bind(R.id.desc)
        TextView desc;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.img)
        ImageView img;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }


}
