
package com.simple.lightnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.MainActivity;
import com.simple.lightnote.activities.SimpleNoteEditActivity;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.Note;
import com.simple.lightnote.utils.DateUtils;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.ToastUtils;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 列表ListView的Adapter
 */
public class RecycleViewNoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Cursor cursor;
    Context mContext;
    private static ArrayList<Note> list;

    public RecycleViewNoteListAdapter(ArrayList<Note> note) {
        this.list = note;
    }

    public RecycleViewNoteListAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    public void setList(ArrayList<Note> note) {
        this.list = note;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notelist_2, parent, false);
        return new RecyclerViewHolder(view, this, mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!ListUtils.isEmpty(list)) {
            Note note = list.get(position);
            ((RecyclerViewHolder) holder).tv_title.setText(note.getNoteContent());

            StringBuilder sb = new StringBuilder();
            Long lastModifyTime = note.getLastModifyTime();
            if (lastModifyTime != null) {

                String dateByTimestamp = DateUtils.getDateByTimestamp(lastModifyTime, "MM/dd HH:mm");
                sb.append("<font color='#228B22'>").append(dateByTimestamp).append("</font>  ");
            }
            sb.append(note.getNoteContent());
            ((RecyclerViewHolder) holder).tv_content.setText(Html.fromHtml(sb.toString()));


            ((RecyclerViewHolder) holder).ll_container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ToastUtils.showSequenceToast(mContext, "onDelete position:" + position);
                    Note note1 = list.remove(position);
                    removeEntity(note1);
                    notifyItemRemove(position);
                    return false;
                }
            });
        }
    }

    /**
     * 从数据库中指定的删除数据
     *
     * @param note1
     */
    private void removeEntity(final Note note1) {


        Observable.just(note1).map(new Func1<Note, Void>() {
            @Override
            public Void call(Note note) {

                DaoMaster daoMaster;
                DaoSession daoSession;
                NoteDao noteDao;

                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "lightnote", null);
                SQLiteDatabase db = helper.getWritableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                noteDao = daoSession.getNoteDao();
                noteDao.deleteByKey(note.getId());
                return null;
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        listener.onDelete(note1);
                    }
                });


    }

    public void notifyItemRemove(int position) {

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }


    @Override
    public int getItemCount() {
        if (!ListUtils.isEmpty(list)) {
            return list.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        RecyclerView.Adapter<ViewHolder> mAdapter;
        Context mContext;
        RelativeLayout ll_container;
        LinearLayout ll_action;
        Button action1, action2, action3;
        TextView tv_content;
        TextView tv_title;

        public RecyclerViewHolder(View view, final RecyclerView.Adapter<ViewHolder> adapter, Context context) {
            super(view);
            this.mAdapter = adapter;
            this.mContext = context;
            tv_content = (TextView) view.findViewById(R.id.item_content);
            tv_title = (TextView) view.findViewById(R.id.item_title);
            action1 = (Button) view.findViewById(R.id.button1);
            action2 = (Button) view.findViewById(R.id.button2);
            action3 = (Button) view.findViewById(R.id.button3);
            ll_container = (RelativeLayout) view.findViewById(R.id.ll_container);
            ll_action = (LinearLayout) view.findViewById(R.id.ll_action);


            action1.setOnClickListener(this);
            action2.setOnClickListener(this);
            action3.setOnClickListener(this);
            ll_container.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button1:
                    ToastUtils.showToast(mContext, "onClick1");
                    break;
                case R.id.button2:
                    ToastUtils.showToast(mContext, "onClick2");
                    break;
                case R.id.button3:
                    ToastUtils.showToast(mContext, "onClick3");
                    break;
                case R.id.ll_container:
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        getLayoutPosition();
                        Note note = list.get(adapterPosition);
                        String s = JSON.toJSONString(note);
                        Intent intent = new Intent(mContext, SimpleNoteEditActivity.class);
                        intent.putExtra("clickItem", s);
                        mContext.startActivity(intent);
                    }

                    break;
                default:
                    break;
            }

        }


    }

    MainActivity.ActionListener listener;

    public void setActionListener(MainActivity.ActionListener listener) {
        this.listener = listener;
    }

}
