package com.simple.lightnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.SimpleNoteEditActivity;
import com.simple.lightnote.model.Note;
import com.simple.lightnote.utils.ScreenUtils;
import com.simple.lightnote.utils.ToastUtils;

import java.util.List;
/**
 * 列表ListView的Adapter
 *
 */
public class NoteListAdapter extends BaseAdapter implements OnTouchListener,OnClickListener{
	private Context mContext;
	private List<Note> noteList;

	OnLongClickListener longClickListener;
	OnClickListener clickListener;
	public void setOnLongClickListener(OnLongClickListener longClickListener){
		this.longClickListener=longClickListener;
	}
	public void setOnClickListener(OnClickListener clickListener){
		this.clickListener=clickListener;
	}

	public void setNoteList(List<Note> noteList) {
		this.noteList = noteList;
	}
	public NoteListAdapter(Context context, List<Note> list) {
		mContext=context;
		this.noteList=list;
		screenWidth = ScreenUtils.getScreenMetrics(mContext)[0];
		
	}
	@Override
	public int getCount() {
		
		return noteList.size();
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
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(mContext, R.layout.item_notelist_2,null);
			holder.tv_introduce = (TextView) convertView.findViewById(R.id.item_content);
			holder.action1=(Button) convertView.findViewById(R.id.button1);
			holder.action2=(Button) convertView.findViewById(R.id.button2);
			holder.action3=(Button) convertView.findViewById(R.id.button3);
			holder.ll_container=(RelativeLayout) convertView.findViewById(R.id.ll_container);
			
		
			holder.ll_action=(LinearLayout) convertView.findViewById(R.id.ll_action);
			
			holder.ll_container.setTag(position);
			
			
			holder.action1.setOnClickListener(this);
			holder.action2.setOnClickListener(this);
			holder.action3.setOnClickListener(this);

			holder.ll_container.setOnClickListener(this);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		LayoutParams layoutParams = holder.ll_container.getLayoutParams();
		layoutParams.width=screenWidth;
		holder.ll_container.setLayoutParams(layoutParams);
		
		
		
		Note note = noteList.get(position);
		holder.tv_introduce.setText(note.getNoteTitle());
		convertView.setOnLongClickListener(longClickListener);
		convertView.setOnClickListener(clickListener);
		convertView.setOnTouchListener(this);
		return convertView;
	}

	boolean isVisible=false;
	int lastVisibleId;
	private int screenWidth;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ViewHolder holder = (ViewHolder) v.getTag();	
		if(isVisible){
			//隐藏上一个显示的
			holder.ll_action.scrollTo(screenWidth,0);
			isVisible=false;
			return true;
		}
		if(ScreenUtils.inRangeOfView(holder.ll_action, event)){
			isVisible=true;
		}

		return false;
	}
	static class ViewHolder{
		RelativeLayout ll_container;
		LinearLayout ll_action;
		Button action1,action2,action3;
		TextView tv_introduce;
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
			int position =(Integer) v.getTag();
			String jsonString = JSON.toJSONString(noteList.get(position));
			intent.putExtra("res", jsonString);
			mContext.startActivity(intent);
			break;
		default:
			break;
		}
	}
	
}
