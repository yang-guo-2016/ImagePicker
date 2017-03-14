package io.github.changjiashuai.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import io.github.changjiashuai.BaseActivity;
import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.R;
import io.github.changjiashuai.adapter.ImagePageAdapter;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.widget.ViewPagerFixed;

public class ImagePreviewActivity extends BaseActivity implements ImagePicker.OnImageSelectedListener, ViewPager.OnPageChangeListener, View.OnLongClickListener, ImagePageAdapter.PhotoViewClickListener {

    private static final String TAG = "ImagePreviewActivity";
    public static final String EXTRA_URLS = "extra_urls";
    public static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";

    /*所有已经选中的图片*/
    private ArrayList<ImageItem> mSelectedImages = new ArrayList<>();
    private int mCurrentItemPosition = 0;
    private ViewPagerFixed viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        showPreview();
    }

    private void showPreview() {
        ArrayList<String> urls = getIntent().getStringArrayListExtra(EXTRA_URLS);
        if (urls != null) {
            for (String url : urls) {
                ImageItem imageItem = new ImageItem();
                imageItem.path = url;
                mSelectedImages.add(imageItem);
            }
        }

        viewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        ImagePageAdapter adapter = new ImagePageAdapter(this, mSelectedImages);
        adapter.setPhotoViewClickListener(this);
        adapter.setOnLongClickListener(this);
        viewPager.setAdapter(adapter);
        mCurrentItemPosition = getIntent().getIntExtra(EXTRA_CURRENT_ITEM_POSITION, 0);
        viewPager.setCurrentItem(mCurrentItemPosition, false);
        viewPager.addOnPageChangeListener(this);
        ImagePicker.getInstance().addOnImageSelectedListener(this);
    }


    /**
     * 单击时，隐藏头和尾
     */
    private void onImageSingleTap() {
        finish();
    }

    @Override
    protected void onDestroy() {
        ImagePicker.getInstance().removeOnImageSelectedListener(this);
        viewPager.removeOnPageChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onImageSelected(int position, ImageItem imageItem, boolean isAdd) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentItemPosition = position;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onLongClick(View v) {
//        Toast.makeText(this, "Long Click", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void OnPhotoTapListener(View view, float v, float v1) {
        onImageSingleTap();
    }
}