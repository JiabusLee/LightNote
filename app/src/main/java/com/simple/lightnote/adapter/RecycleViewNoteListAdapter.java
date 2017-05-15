package com.simple.lightnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.SimpleNoteEditActivity;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.interfaces.ActionListener;
import com.simple.lightnote.interfaces.MyItemClickListener;
import com.simple.lightnote.interfaces.MyItemLongClickListener;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.utils.DateUtils;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 列表ListView的Adapter
 * 列表ListView的Adapter
 */
public class RecycleViewNoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MyItemClickListener {
    static int day = 3600 * 24 * 1000;
    Context mContext;
    ActionListener actionListener;
    NoteDao noteDao;
    private List<SimpleNote> list;
    private RecyclerViewHolder recyclerViewHolder;

    public RecycleViewNoteListAdapter(ArrayList<SimpleNote> note) {
        this.list = note;
    }

    public void setNoteDao(NoteDao dao) {
        this.noteDao = dao;
    }

    public void setList(List<SimpleNote> note) {
        this.list = note;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notelist_3, parent, false);
        recyclerViewHolder = new RecyclerViewHolder(view, mContext);
//        recyclerViewHolder.setClickListener(this);
//        recyclerViewHolder.setOnLongClickListener(this);
        recyclerViewHolder.setClickListener(this);
        return recyclerViewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (!ListUtils.isEmpty(list)) {
            SimpleNote note = list.get(position);
            String noteTitle = note.getTitle();
            if (!TextUtils.isEmpty(noteTitle)) {
                ((RecyclerViewHolder) holder).tv_title.setVisibility(View.VISIBLE);
                ((RecyclerViewHolder) holder).tv_title.setText(note.getTitle());
            } else {
                ((RecyclerViewHolder) holder).tv_title.setVisibility(View.GONE);
            }


            StringBuilder sb = new StringBuilder();
            Long lastModifyTime = note.getUpdated();
            if (lastModifyTime != null) {

                String dateByTimestamp = setShowTime(lastModifyTime);
//                String dateByTimestamp = DateUtils.getDateByTimestamp(lastModifyTime, "MM/dd HH:mm");
                sb.append("<font color='#228B22'>").append(dateByTimestamp).append("</font>  ");
            }
            sb.append(note.getContent());
            ((RecyclerViewHolder) holder).tv_content.setText(Html.fromHtml(sb.toString()));


            ((RecyclerViewHolder) holder).ll_container.setOnLongClickListener(__ -> {
                ToastUtils.showSequenceToast(mContext, "onDelete position:" + position);
                SimpleNote note1 = list.remove(position);
                removeEntity(note1);
                notifyItemRemove(position);
                return false;
            });
        }
    }

    private String setShowTime(Long lastModifyTime) {
        String showTime = null;
        long l = System.currentTimeMillis();
        long l1 = l - lastModifyTime;
        String dL = DateUtils.getDateByTimestamp(lastModifyTime, "dd");
        String dC = DateUtils.getDateByTimestamp(l, "dd");
        int i = Integer.valueOf(dL) - Integer.valueOf(dC);

        if (l1 < 1000 * 60 * 3) {
            showTime = "刚刚";
        } else if (l1 < day) {
            boolean b = l / day - lastModifyTime / day < day;
            if (b && i == 0) {
                showTime = DateUtils.getDateByTimestamp(lastModifyTime, "HH:mm");
            } else {
                showTime = "昨天 " + DateUtils.getDateByTimestamp(lastModifyTime, "HH:mm");
            }
        } else if (i == 1) {
            showTime = "昨天 " + DateUtils.getDateByTimestamp(lastModifyTime, "HH:mm");
        } else {
            showTime = DateUtils.getDateByTimestamp(lastModifyTime, "yyyy/MM/dd");
        }

        return showTime;

    }

    /**
     * 从数据库中指定的删除数据
     *
     * @param note
     */
    private void removeEntity(final SimpleNote note) {


        Observable.just(note)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<SimpleNote>() {
                    @Override
                    public void accept(SimpleNote simpleNote) throws Exception {
                        simpleNote.setStatus(SimpleNote.st_delete);
                        noteDao.update(simpleNote);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> actionListener.onDelete(note));


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


    @Override
    public void onClick(View v, int postion) {
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
                //跳转使用NoteID来传递笔记
                if (postion != RecyclerView.NO_POSITION) {
                    SimpleNote note = list.get(postion);
                    Long id = note.get_id();
                    Intent intent = new Intent(mContext, SimpleNoteEditActivity.class);
                    intent.putExtra("noteId", id);
                    mContext.startActivity(intent);
                }

                break;
            default:
                break;
        }

    }

    public void setActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        Context mContext;
        RelativeLayout ll_container;
        LinearLayout ll_action;
        Button action1, action2, action3;
        TextView tv_content;
        TextView tv_title;
        OnClickListener listener;
        MyItemClickListener clickListener;
        MyItemLongClickListener longClickListener;

        public RecyclerViewHolder(View view, Context context) {
            super(view);

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
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
        public void setClickListener(MyItemClickListener l) {
            this.clickListener = l;
        }

        public void setOnLongClickListener(MyItemLongClickListener ll) {
            this.longClickListener = ll;
        }
    }

}
