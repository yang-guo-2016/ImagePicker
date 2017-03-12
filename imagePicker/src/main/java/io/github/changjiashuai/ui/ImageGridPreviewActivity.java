package io.github.changjiashuai.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.changjiashuai.BaseActivity;
import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.R;
import io.github.changjiashuai.adapter.ImagePageAdapter;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.widget.ViewPagerFixed;

public class ImageGridPreviewActivity extends BaseActivity implements ImagePicker.OnImageSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ImagePreviewActivity";
    /*跳转进时的图片文件夹下所有的图片*/
    private ArrayList<ImageItem> mImageItems;
    /*跳转进时的序号，该文件夹下第几个图片*/
    private int mCurrentPosition = 0;
    /*显示当前图片的位置  例如  5/31 */
    private TextView mTitleCount;
    /*所有已经选中的图片*/
    private ArrayList<ImageItem> mSelectedImages;
    private View mContentLayout;
    private View topBar;

    /*是否选中原图*/
    private boolean isOrigin;
    /*是否选中当前图片的CheckBox*/
    private AppCompatCheckBox mCbCheck;
    /*原图*/
    private AppCompatCheckBox mCbOrigin;
    /*确认图片的选择*/
    private Button mBtnOk;
    private View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid_preview);

        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        boolean showSelected = getIntent().getBooleanExtra(ImagePicker.EXTRA_SHOW_SELECTED, false);
        if (showSelected) {
            mImageItems = ImagePicker.getInstance().getSelectedImages();
        } else {
            mImageItems = ImagePicker.getInstance().getCurrentImageFolderItems();
        }
        mSelectedImages = ImagePicker.getInstance().getSelectedImages();

        //初始化控件
        mContentLayout = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);

        mTitleCount = (TextView) findViewById(R.id.tv_des);

        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        ImagePageAdapter adapter = new ImagePageAdapter(this, mImageItems);
        adapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.image_picker_preview_image_count, mCurrentPosition + 1,
                mImageItems.size()));

        isOrigin = getIntent().getBooleanExtra(ImagePicker.EXTRA_IS_ORIGIN, false);
        ImagePicker.getInstance().addOnImageSelectedListener(this);

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
        boolean isSelected = ImagePicker.getInstance().isSelect(item);
        mTitleCount.setText(getString(R.string.image_picker_preview_image_count,
                mCurrentPosition + 1, mImageItems.size()));
        mCbCheck.setChecked(isSelected);
        updateOriginImageSize();
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                ImageItem item = mImageItems.get(mCurrentPosition);
                boolean isSelected = ImagePicker.getInstance().isSelect(item);
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
                int selectLimit = ImagePicker.getInstance().getSelectLimit();
                if (mCbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                    showToast(ImageGridPreviewActivity.this.getString(R.string.image_picker_select_limit, selectLimit));
                    mCbCheck.setChecked(false);
                } else {
                    ImagePicker.getInstance()
                            .addSelectedImageItem(mCurrentPosition, imageItem, mCbCheck.isChecked());

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
        if (size > 0 && isOrigin) {
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
        if (ImagePicker.getInstance().getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.image_picker_select_complete,
                    ImagePicker.getInstance().getSelectImageCount(), ImagePicker.getInstance().getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnOk.getBackground().setColorFilter(getResources()
                    .getColor(R.color.image_picker_button_normal), PorterDuff.Mode.MULTIPLY);
        } else {
            mBtnOk.setText(getString(R.string.image_picker_complete));
            mBtnOk.setEnabled(false);
            mBtnOk.getBackground().setColorFilter(getResources()
                    .getColor(R.color.image_picker_button_disabled), PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            finishWithResult(ImagePicker.RESULT_CODE_ITEMS);
        } else if (id == R.id.btn_back) {
            finishWithResult(ImagePicker.RESULT_CODE_BACK);
        }
    }

    @Override
    public void onBackPressed() {
        finishWithResult(ImagePicker.RESULT_CODE_BACK);
        super.onBackPressed();
    }

    private void finishWithResult(int resultCode) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_IS_ORIGIN, isOrigin);
        setResult(resultCode, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        ImagePicker.getInstance().removeOnImageSelectedListener(this);
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