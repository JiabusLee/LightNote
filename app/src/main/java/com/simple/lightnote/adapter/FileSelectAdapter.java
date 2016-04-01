package com.simple.lightnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.NotePreViewActivity;

import java.io.File;
import java.util.List;

/**
 * Created by homelink on 2016/3/9.
 */
public class FileSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   static Context mContext;
    static  List<File> mList;
    private static final String TAG = "FileSelectAdapter";

    public FileSelectAdapter(List<File> list) {
        mList=list;
    }

    public void setmList(List<File> list){
        mList=list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filelist, parent, false);
        return new FileSelectHolder(itemView,this) ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mList!=null){
            File file = mList.get(position);
            ((FileSelectHolder)holder).textView.setText(file.getName());
        }

    }

    @Override
    public int getItemCount() {
        if(mList!=null&&mList.size()>0){
            return mList.size();
        }else{
            return 0;
        }
    }

    public static class FileSelectHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public FileSelectHolder(View view,RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
            super(view);
            textView = (TextView) view.findViewById(R.id.item_tv_fileName);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getLayoutPosition() + " clicked.");
//                    ToastUtils.showToast(mContext, "Element " + getLayoutPosition() + " clicked.");
                    Intent intent=new Intent(mContext, NotePreViewActivity.class);
                    intent.putExtra("filePath",mList.get(getLayoutPosition()).getAbsolutePath());
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
