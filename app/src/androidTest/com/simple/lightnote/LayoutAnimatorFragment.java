package com.simple.lightnote;

import com.androidleaf.animation.activity.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

/**
 * @author AndroidLeaf
 *ViewGroup��Layout�Ķ���ʵ��
 *��Ҫ֪ʶ�㣺
 *���� LayoutTransition ����� setAnimator() ����4�������ж�����ʽ�����ò����� Animator
 *��������� LayoutTransition ����
 *	(1)APPEARING ���� Ԫ��������������ʱ��Ҫ������ʾ��
 *	(2)CHANGE_APPEARING ���� ����������Ҫ����һ���µ�Ԫ�أ�����Ԫ�صı仯��Ҫ������ʾ��
 *	(3)DISAPPEARING ���� Ԫ������������ʧʱ��Ҫ������ʾ��
 *	(4)CHANGE_DISAPPEARING ���� ����������ĳ��Ԫ��Ҫ��ʧ������Ԫ�صı仯��Ҫ������ʾ��
 */
public class LayoutAnimatorFragment extends Fragment implements OnClickListener{

	private Button mButtonAdd;
	private Button mButtonReset;
	private GridLayout mGridLayout;
	private int buttonNumbers = 1;
	
	private LayoutTransition mLayoutTransition;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_layoutanimator, container, false);
		initViews(rootView);
		return rootView;
	}
	
	public void initViews(View rootView){
		
		mButtonAdd = (Button)rootView.findViewById(R.id.layout_animator_addbutton);
		mButtonAdd.setOnClickListener(this);
		mButtonReset = (Button)rootView.findViewById(R.id.layout_animator_resetbutton);
		mButtonReset.setOnClickListener(this);
		mGridLayout = (GridLayout)rootView.findViewById(R.id.layout_animator_gridview);
		mLayoutTransition = new LayoutTransition();
		//ΪGridLayout����mLayoutTransition����
		mGridLayout.setLayoutTransition(mLayoutTransition);
		mLayoutTransition.setStagger(LayoutTransition.CHANGE_APPEARING, 30);
		mLayoutTransition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 30);
		//����ÿ��������ʱ��
		mLayoutTransition.setDuration(300);
		//��ʼ���Զ���Ķ���Ч��
		customLayoutTransition();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_animator_addbutton:
			addButton();
			break;
		case R.id.layout_animator_resetbutton:
			resetButton();
			break;
		default:
			break;
		}
	}
	
	/**
	 * ���Button
	 */
	public void addButton(){
		Button mButton = new Button(getActivity());
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(50, 50);
		mButton.setLayoutParams(mLayoutParams);
		mButton.setText(String.valueOf(buttonNumbers++));
		mButton.setTextColor(Color.rgb(0, 0, 0));
		if(buttonNumbers % 2 == 1){
			mButton.setBackgroundColor(Color.rgb(45, 118, 87));
		}else{
			mButton.setBackgroundColor(Color.rgb(225, 24, 0));
		}
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mGridLayout.removeView(v);
			}
		});
		
		mGridLayout.addView(mButton, Math.min(1, mGridLayout.getChildCount()));
	}
	
	/**
	 * ɾ�����е�Button,����GridLayout
	 */
	public void resetButton(){
		mGridLayout.removeAllViews();
		buttonNumbers = 1;
	}
	
	public void customLayoutTransition(){
		
		/**
		 * Add Button
		 * LayoutTransition.APPEARING
		 * ���һ��Buttonʱ�����ø�Button�Ķ���Ч��
		 */
		ObjectAnimator mAnimatorAppearing = ObjectAnimator.ofFloat(null, "rotationY", 90.0f,0.0f)
				.setDuration(mLayoutTransition.getDuration(LayoutTransition.APPEARING));
		//ΪLayoutTransition���ö�������������
		mLayoutTransition.setAnimator(LayoutTransition.APPEARING, mAnimatorAppearing);
		mAnimatorAppearing.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				 View view = (View) ((ObjectAnimator) animation).getTarget();
	             view.setRotationY(0.0f);
			}
		});
		
		/**
		 * Add Button
		 * LayoutTransition.CHANGE_APPEARING
		 * �����һ��Buttonʱ����������Button�Ķ���Ч��
		 */
		
		PropertyValuesHolder pvhLeft =
	                PropertyValuesHolder.ofInt("left", 0, 1);
        PropertyValuesHolder pvhTop =
                PropertyValuesHolder.ofInt("top", 0, 1);
        PropertyValuesHolder pvhRight =
                PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom =
                PropertyValuesHolder.ofInt("bottom", 0, 1);
        
		PropertyValuesHolder mHolderScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f,0.0f,1.0f);
		PropertyValuesHolder mHolderScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f,0.0f,1.0f);
		ObjectAnimator mObjectAnimatorChangeAppearing = ObjectAnimator.ofPropertyValuesHolder(this, pvhLeft,
				pvhTop,pvhRight,pvhBottom,mHolderScaleX,mHolderScaleY).setDuration(mLayoutTransition
						.getDuration(LayoutTransition.CHANGE_APPEARING));
		mLayoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, mObjectAnimatorChangeAppearing);
		mObjectAnimatorChangeAppearing.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				 View view = (View) ((ObjectAnimator) animation).getTarget();
	             view.setScaleX(1f);
	             view.setScaleY(1f);
			}
		});
		
		/**
		 * Delete Button
		 * LayoutTransition.DISAPPEARING
		 * ��ɾ��һ��Buttonʱ�����ø�Button�Ķ���Ч��
		 */
		ObjectAnimator mObjectAnimatorDisAppearing = ObjectAnimator.ofFloat(null, "rotationX", 0.0f,90.0f)
				.setDuration(mLayoutTransition.getDuration(LayoutTransition.DISAPPEARING));
		mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, mObjectAnimatorDisAppearing);
		mObjectAnimatorDisAppearing.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				 View view = (View) ((ObjectAnimator) animation).getTarget();
	             view.setRotationX(0.0f);
			}
		});
		
		/**
		 * Delete Button
		 * LayoutTransition.CHANGE_DISAPPEARING
		 * ��ɾ��һ��Buttonʱ����������Button�Ķ���Ч��
		 */
		//Keyframe �����а���һ��ʱ��/����ֵ�ļ�ֵ�ԣ����ڶ���ĳ��ʱ�̵Ķ���״̬��
		Keyframe mKeyframeStart = Keyframe.ofFloat(0.0f, 0.0f);
		Keyframe mKeyframeMiddle = Keyframe.ofFloat(0.5f, 180.0f);
		Keyframe mKeyframeEndBefore = Keyframe.ofFloat(0.999f, 360.0f);
		Keyframe mKeyframeEnd = Keyframe.ofFloat(1.0f, 0.0f);
		
		PropertyValuesHolder mPropertyValuesHolder = PropertyValuesHolder.ofKeyframe("rotation", 
				mKeyframeStart,mKeyframeMiddle,mKeyframeEndBefore,mKeyframeEnd);
		ObjectAnimator mObjectAnimatorChangeDisAppearing = ObjectAnimator.ofPropertyValuesHolder(this, pvhLeft,pvhTop,pvhRight,pvhBottom,mPropertyValuesHolder)
				.setDuration(mLayoutTransition.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
		mLayoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mObjectAnimatorChangeDisAppearing);
		mObjectAnimatorChangeDisAppearing.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				 View view = (View) ((ObjectAnimator) animation).getTarget();
	              view.setRotation(0.0f);
			}
		});
	}
}
