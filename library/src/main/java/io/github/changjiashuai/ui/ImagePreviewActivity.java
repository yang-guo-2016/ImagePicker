package io.github.changjiashuai.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.changjiashuai.BaseActivity;
import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.adapter.ImagePageAdapter;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.library.R;
import io.github.changjiashuai.widget.ViewPagerFixed;

public class ImagePreviewActivity extends BaseActivity implements ImagePicker.OnImageSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ImagePreviewActivity";
    private ImagePicker mImagePicker;
    private ArrayList<ImageItem> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹下所有的图片
    private int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，改文件夹下第几个图片
    private TextView mTitleCount;                  //显示当前图片的位置  例如  5/31
    private boolean mShowSelected = false;
    private ArrayList<ImageItem> mSelectedImages;   //所有已经选中的图片
    private View mContentLayout;
    private View topBar;
    private ViewPagerFixed mViewPager;
    private ImagePageAdapter mAdapter;

    public static final String ISORIGIN = "isOrigin";

    private boolean isOrigin;                      //是否选中原图
    private AppCompatCheckBox mCbCheck;                //是否选中当前图片的CheckBox
    private AppCompatCheckBox mCbOrigin;               //原图
    private Button mBtnOk;                         //确认图片的选择
    private View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        mImagePicker = ImagePicker.getInstance();
        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        mShowSelected = getIntent().getBooleanExtra(ImagePicker.EXTRA_SHOW_SELECTED, false);
        if (mShowSelected) {
            mImageItems = mImagePicker.getSelectedImages();
        } else {
            mImageItems = mImagePicker.getCurrentImageFolderItems();
        }
        mSelectedImages = mImagePicker.getSelectedImages();

        //初始化控件
        mContentLayout = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);

        mTitleCount = (TextView) findViewById(R.id.tv_des);

        mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.image_picker_preview_image_count, mCurrentPosition + 1,
                mImageItems.size()));

        isOrigin = getIntent().getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
        mImagePicker.addOnImageSelectedListener(this);

        topBar.findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (Button) topBar.findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);

        bottomBar = findViewById(R.id.bottom_bar);

        mCbCheck = (AppCompatCheckBox) findViewById(R.id.cb_check);
        mCbOrigin = (AppCompatCheckBox) findViewById(R.id.cb_origin);
        mCbOrigin.setText(getString(R.string.image_picker_origin));
        mCbOrigin.setOnCheckedChangeListener(this);
        mCbOrigin.setChecked(isOrigin);

        //初始化当前页面的状态
        onImageSelected(0, null, false);
        ImageItem item = mImageItems.get(mCurrentPosition);
        boolean isSelected = mImagePicker.isSelect(item);
        mTitleCount.setText(getString(R.string.image_picker_preview_image_count,
                mCurrentPosition + 1, mImageItems.size()));
        mCbCheck.setChecked(isSelected);
        updateOriginImageSize();
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                ImageItem item = mImageItems.get(mCurrentPosition);
                boolean isSelected = mImagePicker.isSelect(item);
                mCbCheck.setChecked(isSelected);
                mTitleCount.setText(getString(R.string.image_picker_preview_image_count,
                        mCurrentPosition + 1, mImageItems.size()));
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageItem imageItem = mImageItems.get(mCurrentPosition);
                int selectLimit = mImagePicker.getSelectLimit();
                if (mCbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                    showToast(ImagePreviewActivity.this.getString(R.string.image_picker_select_limit, selectLimit));
                    mCbCheck.setChecked(false);
                } else {
                    mImagePicker.addSelectedImageItem(mCurrentPosition, imageItem, mCbCheck.isChecked());

                    //每次选择一张图片就计算一次图片总大小
                    if (mSelectedImages != null && mSelectedImages.size() > 0) {
                        updateOriginImageSize();
                    } else {
                        mCbOrigin.setText(getString(R.string.image_picker_origin));
                    }
                }
            }
        });
    }


    /**
     * 单击时，隐藏头和尾
     */
    private void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(android.R.color.transparent);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            if (Build.VERSION.SDK_INT >= 16) {
                mContentLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(R.color.image_picker_status_bar);//通知栏所需颜色
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            if (Build.VERSION.SDK_INT >= 16) {
                mContentLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    private void updateOriginImageSize() {
        long size = 0;
        for (ImageItem ii : mSelectedImages) {
            size += ii.size;
        }
        if (size > 0) {
            String fileSize = Formatter.formatFileSize(this, size);
            mCbOrigin.setText(getString(R.string.image_picker_origin_size, fileSize));
        } else {
            mCbOrigin.setText(getString(R.string.image_picker_origin));
        }
    }

    /**
     * 图片添加成功后，修改当前图片的选中数量 当调用 addSelectedImageItem 或 deleteSelectedImageItem 都会触发当前回调
     */
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (mImagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.image_picker_select_complete,
                    mImagePicker.getSelectImageCount(), mImagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnOk.getBackground().setColorFilter(getResources()
                    .getColor(R.color.image_picker_button_normal), PorterDuff.Mode.MULTIPLY);
        } else {
            mBtnOk.setText(getString(R.string.image_picker_complete));
            mBtnOk.setEnabled(false);
            mBtnOk.getBackground().setColorFilter(getResources()
                    .getColor(R.color.image_picker_button_disabled), PorterDuff.Mode.MULTIPLY);
        }

//        if (mCbOrigin.isChecked()) {
//            long size = 0;
//            for (ImageItem imageItem : selectedImages)
//                size += imageItem.size;
//            String fileSize = Formatter.formatFileSize(this, size);
//            mCbOrigin.setText(getString(R.string.origin_size, fileSize));
//        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImagePicker.getSelectedImages());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else if (id == R.id.btn_back) {
            finishWithResult();
        }
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
        super.onBackPressed();
    }

    private void finishWithResult() {
        Log.i(TAG, "finishWithResult: ");
        Intent intent = new Intent();
        intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mImagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                long size = 0;
                for (ImageItem item : mSelectedImages) {
                    size += item.size;
                }
                String fileSize = Formatter.formatFileSize(this, size);
                isOrigin = true;
                mCbOrigin.setText(getString(R.string.image_picker_origin_size, fileSize));
            } else {
                isOrigin = false;
                mCbOrigin.setText(getString(R.string.image_picker_origin));
            }
        }
    }
}