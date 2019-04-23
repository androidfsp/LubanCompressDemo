package com.fansp.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.fansp.myapplication.imglook.GestureImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 2017/12/14.
 */

public class PhotosViewActivity extends Activity {
    @Bind(R.id.iv_photoview_back)
    ImageView ivPhotoviewBack;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    /**
     * ViewPager
     */
    private ViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;

    /**
     * 图片资源id
     */
    private String[] imgIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photosview);
        ButterKnife.bind(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //载入图片资源ID
        imgIdArray = getIntent().getStringExtra("pic").split(",");
        ivPhotoviewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager.setOffscreenPageLimit(2);


        PagerAdapter adapter = new MyViewPagerAdapter(this, Arrays.asList(imgIdArray));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("pos", 0));

        tvTip.setText((getIntent().getIntExtra("pos", 0)+1)+"/"+imgIdArray.length);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvTip.setText((position+1)+"/"+imgIdArray.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    class MyViewPagerAdapter extends PagerAdapter {

        List<String> imgs;

        List<View> views;

        Context mContext;


        public MyViewPagerAdapter(Context context, List<String> imgs) {

            this.mContext = context;
            this.imgs = imgs;

            this.views = new ArrayList<>();

        }

        @Override
        public int getCount() { // 获得size
            return imgs.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewPager) container).removeView((GestureImageView) object);  //删除页卡
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            GestureImageView full_image = new GestureImageView(mContext);
            //full_image
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            full_image.setLayoutParams(params);
            full_image.setScaleType(ImageView.ScaleType.FIT_CENTER);


            System.out.println("position:" + position + "------->>>>  url " + imgs.get(position));
            if (imgs.get(position).trim().contains("/storage")){
                Glide.with(mContext).load(imgs.get(position).trim()).error(R.mipmap.ic_launcher).into(full_image);
            }else {
//                Glide.with(mContext).load(AppConst.DEFAULTURL+imgs.get(position).trim()).into(full_image);
            }


            ((ViewPager) container).addView(full_image);

            return full_image;
        }
    }
}
