
package com.simple.lightnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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
import com.simple.lightnote.model.Note;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.ToastUtils;

import java.util.ArrayList;
/**
 * 列表ListView的Adapter
 *
 */
public class RecycleViewNoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	private Cursor cursor;
	Context mContext;
	private ArrayList<Note> list;
	public RecycleViewNoteListAdapter(ArrayList<Note> note) {
		this.list=note;
	}

	public RecycleViewNoteListAdapter(Cursor cursor){
		this.cursor=cursor;
	}

	public void setList(ArrayList<Note> note){
		this.list=note;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		mContext=parent.getContext();
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_notelist_2, parent, false);
		return new RecyclerViewHolder(view,this,mContext) ;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if(!ListUtils.isEmpty(list)){
			Note note = list.get(position);
			((RecyclerViewHolder)holder).tv_introduce.setText(note.getNoteContent());
		}
	}

	@Override
	public int getItemCount() {
		if(!ListUtils.isEmpty(list)){
			return list.size();
		}
		return 0;
	}
	public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
		RecyclerView.Adapter<ViewHolder> mAdapter;
		Context mContext;
		RelativeLayout ll_container;
		LinearLayout ll_action;
		Button action1,action2,action3;
		TextView tv_introduce;
		public RecyclerViewHolder(View view,RecyclerView.Adapter<ViewHolder> adapter,Context context) {
			super(view);
			this.mAdapter=adapter;
			this.mContext=context;
			tv_introduce = (TextView) view.findViewById(R.id.item_introduce);
			action1=(Button) view.findViewById(R.id.button1);
			action2=(Button) view.findViewById(R.id.button2);
			action3=(Button) view.findViewById(R.id.button3);
			ll_container=(RelativeLayout) view.findViewById(R.id.ll_container);
			ll_action=(LinearLayout) view.findViewById(R.id.ll_action);
			

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
				Intent intent=new Intent(mContext,SimpleNoteEditActivity.class);
				mContext.startActivity(intent);
				break;
			default:
				break;
			}
					
		}		
		
		
	}
}
