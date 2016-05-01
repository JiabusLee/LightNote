package com.simple.lightnote;

import com.androidleaf.animation.activity.R;
import com.androidleaf.animation.customproperty.CustomEvaluator;
import com.androidleaf.animation.customproperty.CustomInterpolator;

import android.R.integer;
import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
@SuppressLint("NewApi")
public class ValueAnimatorFragment extends Fragment implements OnClickListener,OnItemClickListener{

	private Button mButtonMarginValueAnimator;
	private Button mButtonScaleValueAnimator;
	private Button mbButtonRotateValueAnimator;
	private Button mButtonShowListView;
	private ImageView mImageViewTest;
	private ListView mListView;
	
	private int screenWidth = 0;
	
	String[] strResources = {
			"Show ListView 1",
			"Show ListView 2",
			"Show ListView 3",
			"Show ListView 4",
			"Show ListView 5",
			"Show ListView 6",
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// �����Ļ���
	 	DisplayMetrics metrics = new DisplayMetrics();
	 	getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
	 	screenWidth = metrics.widthPixels;
	 	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_valueanimator, container, false);
		mImageViewTest = (ImageView)rootView.findViewById(R.id.value_animator_imageview_test);
		mButtonMarginValueAnimator = (Button)rootView.findViewById(R.id.margin_valueanimator_button);
		mButtonMarginValueAnimator.setOnClickListener(this);
		mButtonScaleValueAnimator = (Button)rootView.findViewById(R.id.scale_valueanimator_button);
		mButtonScaleValueAnimator.setOnClickListener(this);
		mbButtonRotateValueAnimator = (Button)rootView.findViewById(R.id.rotate_valueanimator_button);
		mbButtonRotateValueAnimator.setOnClickListener(this);
		mListView = (ListView)rootView.findViewById(R.id.valueanimator_listview);
		ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_objectanimator_item,R.id.objectanimtor_item_textview, strResources);
		mListView.setAdapter(mArrayAdapter);
		mListView.setOnItemClickListener(this);
		mButtonShowListView = (Button)rootView.findViewById(R.id.valueanimator_showlistview);
		mButtonShowListView.setOnClickListener(this);
		return rootView;
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
		case R.id.margin_valueanimator_button:
			marginValueAnimator();
			break;
		case R.id.scale_valueanimator_button:
			scaleValueAnimator();
			break;
		case R.id.rotate_valueanimator_button:
			rotateValueAniamtor();
			break;
		case R.id.valueanimator_showlistview:
			hideOrShowListViewAnimator(0,800);
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * ʹ��ValueAnimator�ı�Imageview��margin��ֵ
	 */
	public void marginValueAnimator(){
		//1.����ofInt(int...values)��������ValueAnimator����
		ValueAnimator mAnimator = ValueAnimator.ofInt(0,screenWidth - mImageViewTest.getWidth());
		//2.ΪĿ���������Ա仯���ü�����
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// 3.ΪĿ�������������ü���õ�����ֵ
				int animatorValue = (Integer)animation.getAnimatedValue();
				MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mImageViewTest.getLayoutParams();
				marginLayoutParams.leftMargin = animatorValue;
				mImageViewTest.setLayoutParams(marginLayoutParams);
			}
		});
		//4.���ö����ĳ���ʱ�䡢�Ƿ��ظ����ظ����������
		mAnimator.setDuration(2000);
		mAnimator.setRepeatCount(3);
		mAnimator.setRepeatMode(ValueAnimator.REVERSE);
		//5.ΪValueAnimator����Ŀ����󲢿�ʼִ�ж���
		mAnimator.setTarget(mImageViewTest);
		mAnimator.start();
	}
	
	/**
	 * ʹ��ValueAnimatorʵ��ͼƬ��Ŷ���
	 */
	public void scaleValueAnimator(){
		//1.����Ŀ�����������Ա仯�ĳ�ʼֵ�ͽ���ֵ
		PropertyValuesHolder mPropertyValuesHolderScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f,0.0f);
		PropertyValuesHolder mPropertyValuesHolderScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f,0.0f);
		ValueAnimator mAnimator = ValueAnimator.ofPropertyValuesHolder(mPropertyValuesHolderScaleX,mPropertyValuesHolderScaleY);
		//2.ΪĿ���������Ա仯���ü�����
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// 3.����������ȡ���Ա仯��ֵ�ֱ�ΪImageViewĿ���������X��Y������ֵ
				float animatorValueScaleX =  (Float) animation.getAnimatedValue("scaleX");
				float animatorValueScaleY = (Float) animation.getAnimatedValue("scaleY");
				mImageViewTest.setScaleX(animatorValueScaleX);
				mImageViewTest.setScaleY(animatorValueScaleY);
				
			}
		});
		//4.ΪValueAnimator�����Զ����Interpolator
		mAnimator.setInterpolator(new CustomInterpolator());
		//5.���ö����ĳ���ʱ�䡢�Ƿ��ظ����ظ����������
		mAnimator.setDuration(2000);
		mAnimator.setRepeatCount(3);
		mAnimator.setRepeatMode(ValueAnimator.REVERSE);
		//6.ΪValueAnimator����Ŀ����󲢿�ʼִ�ж���
		mAnimator.setTarget(mImageViewTest);
		mAnimator.start();
	}
	
	/**
	 * ʹ��ValueAnimatorʵ��ͼƬ��ת����
	 */
	public void rotateValueAniamtor(){
		
		PropertyValuesHolder mPropertyValuesHolder = PropertyValuesHolder.ofFloat("rotation",0.0f,360.f);
		ValueAnimator mAnimator = ValueAnimator.ofPropertyValuesHolder(mPropertyValuesHolder);
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				float aniamtorValue = (Float)animation.getAnimatedValue("rotation");
				
				mImageViewTest.setRotation(aniamtorValue);
			}
		});
		
		mAnimator.setDuration(2000);
		mAnimator.setRepeatCount(3);
		mAnimator.setTarget(mImageViewTest);
		mAnimator.setInterpolator(new LinearInterpolator());
		mAnimator.setRepeatMode(ValueAnimator.RESTART);
		mAnimator.start();
		
	}
	
	/**
	 * ���ػ���ʾListView�Ķ���
	 */
	public void hideOrShowListViewAnimator(final int startValue,final int endValue){
		//1.�������Եĳ�ʼֵ�ͽ���ֵ
		ValueAnimator mAnimator = ValueAnimator.ofInt(0,100);
		//2.ΪĿ���������Ա仯���ü�����
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				int animatorValue = (Integer)animation.getAnimatedValue();
				float fraction = animatorValue/100f;
				IntEvaluator mEvaluator = new IntEvaluator();
				//3.ʹ��IntEvaluator��������ֵ����ֵ��ListView�ĸ�
				mListView.getLayoutParams().height = mEvaluator.evaluate(fraction, startValue, endValue);
				mListView.requestLayout();
			}
		});
		//4.ΪValueAnimator����LinearInterpolator
		mAnimator.setInterpolator(new LinearInterpolator());
		//5.���ö����ĳ���ʱ��
		mAnimator.setDuration(500);
		//6.ΪValueAnimator����Ŀ����󲢿�ʼִ�ж���
		mAnimator.setTarget(mListView);
		mAnimator.start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		hideOrShowListViewAnimator(800,0);
	}
}
