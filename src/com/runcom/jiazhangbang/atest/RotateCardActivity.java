package com.runcom.jiazhangbang.atest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.runcom.jiazhangbang.R;

/**
 * Created by Wood on 2016/8/12.
 */
public class RotateCardActivity extends Activity implements View.OnClickListener
{
	private static final String LOG_TAG = "RotateCardActivity";

	private RelativeLayout rlCardRoot;
	private ImageView imageViewBack;
	private ImageView imageViewFront;

	private void initView()
	{
		rlCardRoot = (RelativeLayout) findViewById(R.id.rl_card_root);
		imageViewBack = (ImageView) findViewById(R.id.imageView_back);
		imageViewFront = (ImageView) findViewById(R.id.imageView_front);
		imageViewBack.setOnClickListener(this);
		imageViewFront.setOnClickListener(this);
		setCameraDistance();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rl_card_root);
		initView();
		initData();
	}

	/**
	 * ��������
	 */
	public void initData()
	{
		String imageUri = "drawable://" + R.drawable.app_ic;
//		ImageLoader.getInstance().displayImage(imageUri ,imageViewBack ,R.drawable.app_ic);
		imageUri = "drawable://" + R.drawable.app_ic;
//		ImageLoader.getInstance().displayImage(imageUri ,imageViewFront ,R.drawable.app_ic);
		imageViewBack.setVisibility(View.VISIBLE);
		imageViewFront.setVisibility(View.INVISIBLE);
	}

	/**
	 * ����
	 */
	public void cardTurnover()
	{
		if(View.VISIBLE == imageViewBack.getVisibility())
		{
//			ViewHelper.setRotationY(imageViewFront ,180f);// �ȷ�ת180��ת����ʱ�Ͳ��Ƿ�ת����
			Rotatable rotatable = new Rotatable.Builder(rlCardRoot).sides(R.id.imageView_back ,R.id.imageView_front).direction(Rotatable.ROTATE_Y).rotationCount(1).build();
			rotatable.setTouchEnable(false);
			rotatable.rotate(Rotatable.ROTATE_Y , -180 ,1500);
		}
		else
			if(View.VISIBLE == imageViewFront.getVisibility())
			{
				Rotatable rotatable = new Rotatable.Builder(rlCardRoot).sides(R.id.imageView_back ,R.id.imageView_front).direction(Rotatable.ROTATE_Y).rotationCount(1).build();
				rotatable.setTouchEnable(false);
				rotatable.rotate(Rotatable.ROTATE_Y ,0 ,1500);
			}
	}

	/**
	 * �ı��ӽǾ���, ������Ļ
	 */
	private void setCameraDistance()
	{
		int distance = 10000;
		float scale = getResources().getDisplayMetrics().density * distance;
		rlCardRoot.setCameraDistance(scale);
	}

	@Override
	public void onClick(View v )
	{
		switch(v.getId())
		{
			case R.id.imageView_back:
			case R.id.imageView_front:
				cardTurnover();
				break;
		}
	}
}
