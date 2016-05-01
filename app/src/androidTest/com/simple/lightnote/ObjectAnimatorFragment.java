package com.simple.lightnote;

import com.androidleaf.animation.activity.R;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

@SuppressLint("NewApi")
public class ObjectAnimatorFragment extends Fragment implements OnClickListener{

	private ListView mListViewFront;
	private ListView mListViewReverse;
	private Button mButtonFlip;
	private Button mButtonAlpha;
	private Button mButtonScale;
	private Button mButtonTranslate;
	private Button mButtonRotate;
	private Button mButtonSet;
	private ImageView mImageView;
	
	private int screenWidth = 0;
	private int screenHeight = 0;
	
	String[] frontStrs = {
			"Front Page 1",
			"Front Page 2",
			"Front Page 3",
			"Front Page 4",
			"Front Page 5",
			"Front Page 6",
	};
	
	String[] reverseStrs = {
			"Reverse Page 1",
			"Reverse Page 2",
			"Reverse Page 3",
			"Reverse Page 4",
			"Reverse Page 5",
			"Reverse Page 6",
	};
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		DisplayMetrics metrics = new DisplayMetrics();
	 	getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
	 	float density = metrics.density;
	 	//screenWidth = (int)(metrics.widthPixels * density + 0.5f);
	 	//screenHeight = (int)(metrics.heightPixels * density + 0.5f);
	 	screenWidth = metrics.widthPixels;
	 	screenHeight = metrics.heightPixels;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_objectanimator, container, false);
		mListViewFront = (ListView) rootView.findViewById(R.id.front_page_listview);
		mListViewReverse = (ListView) rootView.findViewById(R.id.reverse_page_listview);
		mButtonFlip = (Button)rootView.findViewById(R.id.button_flip);
		mButtonFlip.setOnClickListener(this);
		mButtonAlpha = (Button)rootView.findViewById(R.id.button_alpha);
		mButtonAlpha.setOnClickListener(this);
		mButtonScale = (Button)rootView.findViewById(R.id.button_scale);
		mButtonScale.setOnClickListener(this);
		mButtonTranslate = (Button)rootView.findViewById(R.id.button_translate);
		mButtonTranslate.setOnClickListener(this);
		mButtonRotate = (Button)rootView.findViewById(R.id.button_rotate);
		mButtonRotate.setOnClickListener(this);
		mButtonSet = (Button)rootView.findViewById(R.id.button_set);
		mButtonSet.setOnClickListener(this);
		mImageView = (ImageView)rootView.findViewById(R.id.objectanimator_imageview);
		mImageView.setOnClickListener(this);
		initData();
		return rootView;
	}

	public void initData(){
		ArrayAdapter<String> frontListData = new ArrayAdapter<String>(getActivity(), R.layout.layout_objectanimator_item,R.id.objectanimtor_item_textview, frontStrs);
		ArrayAdapter<String> reverseListData = new ArrayAdapter<String>(getActivity(), R.layout.layout_objectanimator_item,R.id.objectanimtor_item_textview, reverseStrs);
		
		mListViewFront.setAdapter(frontListData);
		mListViewReverse.setAdapter(reverseListData);
		mListViewReverse.setRotationX(-90.0f);
	
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
		case R.id.button_flip:
			flip();
			break;
		case R.id.button_alpha:
			alphaAnimator();
			break;
		case R.id.button_scale:
			scaleAnimator();
			break;
		case R.id.button_translate:
			translateAniamtor();
			break;
		case R.id.button_rotate:
			rotateAniamtor();
			break;
		case R.id.button_set:
			setAnimator();
			break;
		case R.id.objectanimator_imageview:
			mListViewFront.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	/**
	 * ��ת����Ч��
	 */
	public void flip(){
		final ListView visibleView;
		final ListView invisibleView;
		if(mListViewFront.getVisibility() == View.GONE){
			visibleView = mListViewReverse;
			invisibleView = mListViewFront;
		}else{
			visibleView = mListViewFront;
			invisibleView = mListViewReverse;
		}
		//����ListView��Visible��Gone�Ķ���
		ObjectAnimator visibleToInVisable = ObjectAnimator.ofFloat(visibleView, "rotationX", 0.0f,90.0f);
		//���ò�ֵ��
		visibleToInVisable.setInterpolator(new AccelerateInterpolator());
		visibleToInVisable.setDuration(500);
		
		//����ListView��Gone��Visible�Ķ���
		final ObjectAnimator invisibleToVisible = ObjectAnimator.ofFloat(invisibleView, "rotationX", -90.0f,0.0f);
		//���ò�ֵ��
		invisibleToVisible.setInterpolator(new DecelerateInterpolator());
		invisibleToVisible.setDuration(500);
		
		visibleToInVisable.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				super.onAnimationEnd(animation);
				visibleView.setVisibility(View.GONE);
				invisibleToVisible.start();
				invisibleView.setVisibility(View.VISIBLE);
			}
		});
		visibleToInVisable.start();
	}
	
	/**
	 * ���䶯��Ч��
	 */
	public void alphaAnimator(){
		ListView alphaListView = null;
		if(mListViewFront.getVisibility() == View.GONE){
			alphaListView = mListViewReverse;
		}else{
			alphaListView = mListViewFront;
		}
		//1��ͨ�����ofFloat()��������ObjectAnimator���󣬲�����Ŀ�������Ҫ�ı��Ŀ���������ʼֵ�ͽ���ֵ��
		ObjectAnimator mAnimatorAlpha = ObjectAnimator.ofFloat(alphaListView, "alpha", 1.0f,0.0f);
		//2�����ö����ĳ���ʱ�䡢�Ƿ��ظ����ظ��������ԣ�
		mAnimatorAlpha.setRepeatMode(Animation.REVERSE);
		mAnimatorAlpha.setRepeatCount(3);
		mAnimatorAlpha.setDuration(1000);
		//3�������
		mAnimatorAlpha.start();
	}
	
	/**
	 * �����Ч��
	 */
	public void scaleAnimator(){
		ListView scaleListView = null;
		if(mListViewFront.getVisibility() == View.GONE){
			scaleListView = mListViewReverse;
		}else{
			scaleListView = mListViewFront;
		}
		
		ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(scaleListView, "scaleX", 1.0f,0.0f);
		mAnimatorScaleX.setRepeatMode(Animation.REVERSE);
		mAnimatorScaleX.setRepeatCount(3);
		mAnimatorScaleX.setDuration(1000);
		
		ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(scaleListView, "scaleY", 1.0f,0.0f);
		mAnimatorScaleY.setRepeatMode(Animation.REVERSE);
		mAnimatorScaleY.setRepeatCount(3);
		mAnimatorScaleY.setDuration(1000);
		
		mAnimatorScaleX.start();
		mAnimatorScaleY.start();
	}
	
	/**
	 * λ�ƶ���Ч��
	 */
	public void translateAniamtor(){
		ListView translateListView = null;
		if(mListViewFront.getVisibility() == View.GONE){
			translateListView = mListViewReverse;
		}else{
			translateListView = mListViewFront;
		}
		
		ObjectAnimator mAnimatorTranslateX = ObjectAnimator.ofFloat(translateListView, "translationX", 0.0f,screenWidth/2);
		mAnimatorTranslateX.setRepeatMode(Animation.REVERSE);
		mAnimatorTranslateX.setRepeatCount(3);
		mAnimatorTranslateX.setDuration(1000);
		
		ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(translateListView, "translationY", 0.0f,screenHeight/2);
		mAnimatorTranslateY.setRepeatMode(Animation.REVERSE);
		mAnimatorTranslateY.setRepeatCount(3);
		mAnimatorTranslateY.setDuration(1000);
		
		mAnimatorTranslateX.start();
		mAnimatorTranslateY.start();
	}
	
	/**
	 * ��ת����Ч��
	 */
	public void rotateAniamtor(){
		ListView rotateListView = null;
		if(mListViewFront.getVisibility() == View.GONE){
			rotateListView = mListViewReverse;
		}else{
			rotateListView = mListViewFront;
		}
		ObjectAnimator mAnimatorRotate = ObjectAnimator.ofFloat(rotateListView, "rotation", 0.0f,360.0f);
		mAnimatorRotate.setRepeatMode(Animation.REVERSE);
		mAnimatorRotate.setRepeatCount(2);
		mAnimatorRotate.setDuration(2000);
		
		mAnimatorRotate.start();
	}
	/**
	 * ��������
	 */
	public void setAnimator(){
		ListView setListView = null;
		if(mListViewFront.getVisibility() == View.GONE){
			setListView = mListViewReverse;
		}else{
			setListView = mListViewFront;
		}
		setListView.setVisibility(View.GONE);
		if(mImageView.getVisibility() == View.GONE){
			mImageView.setVisibility(View.VISIBLE);
		}
		//���뷽ʽ���ö���
		codeAnimatorSet(mImageView);
		
		//��ViewPropertyAnimatorʵ�ֶ���
		//viewPropertyAnimator(setListView);
		
		//����XML�ļ��еĶ���
		/*AnimatorSet mAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.property_animation_animatorset);
		mAnimatorSet.setTarget(mImageView);
		mAnimatorSet.start();*/
	}
	
	/**
	 * ʹ�ñ��뷽ʽʵ�ֶ���Ч��
	 * @param mImageView
	 */
	public void codeAnimatorSet(ImageView mImageView){
		AnimatorSet mAnimatorSet = new AnimatorSet();
		
		ObjectAnimator mAnimatorSetRotateX = ObjectAnimator.ofFloat(mImageView, "rotationX", 0.0f,360.0f);
		mAnimatorSetRotateX.setDuration(3000);
		
		ObjectAnimator mAnimatorSetRotateY = ObjectAnimator.ofFloat(mImageView, "rotationY", 0.0f,360.0f);
		mAnimatorSetRotateY.setDuration(3000);
		
		ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(mImageView, "scaleX", 1.0f,0.5f);
		mAnimatorScaleX.setRepeatCount(1);
		mAnimatorScaleX.setRepeatMode(Animation.REVERSE);
		mAnimatorScaleX.setDuration(1500);
		
		ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(mImageView, "scaleY", 1.0f,0.5f);
		mAnimatorScaleY.setRepeatCount(1);
		mAnimatorScaleY.setRepeatMode(Animation.REVERSE);
		mAnimatorScaleY.setDuration(1500);
		
		mAnimatorSet.play(mAnimatorSetRotateY).with(mAnimatorScaleX);
		mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
		mAnimatorSet.play(mAnimatorSetRotateY).before(mAnimatorSetRotateX);
		
		mAnimatorSet.start();
	}
	
	public void viewPropertyAnimator(ListView mListViewHolder){
		mListViewHolder.animate().cancel();
		mListViewHolder.animate().rotationX(360.0f).setDuration(3000).start();
	}
	
}
