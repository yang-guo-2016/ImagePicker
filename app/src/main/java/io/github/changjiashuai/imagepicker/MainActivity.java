package io.github.changjiashuai.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.widget.CropImageView;

import static io.github.changjiashuai.ImagePicker.REQUEST_CODE_PICK;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private RadioGroup mRgChoice;
    private AppCompatEditText mEtMaxSelectCount;
    private AppCompatCheckBox mCbShowCamera;
    private AppCompatCheckBox mCbCrop;
    private AppCompatSpinner mCropStyleSpinner;
    private AppCompatCheckBox mCbOutputRectangle;
    private LinearLayout mLayoutCrop;
    private AppCompatEditText mEtFocusW;
    private AppCompatEditText mEtFocusH;
    private AppCompatEditText mEtOutputX;
    private AppCompatEditText mEtOutputY;
    private Button mBtnPickImage;
    private Button mBtnPreview;
    private ImageView mIvImage;

    private ArrayList<String> urls = new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered17.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered18.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered19.png"));

    private boolean mMulti = true;
    private boolean mShowCamera = false;
    private boolean mShowCrop = false;
    private boolean mSaveRectangle = false;
    private int mMaxSelectCount = 9;
    private int cropStyle = CropImageView.RECTANGLE;
    private int mFocusWidth = 400;
    private int mFocusHeight = 400;
    private int mOutputX = 800;
    private int mOutputY = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        showMultiSelectUI();
    }

    private void initView() {
        mRgChoice = (RadioGroup) findViewById(R.id.rg_choice);
        mRgChoice.setOnCheckedChangeListener(this);
        mEtMaxSelectCount = (AppCompatEditText) findViewById(R.id.et_max_select_count);
        mCbShowCamera = (AppCompatCheckBox) findViewById(R.id.cb_show_camera);
        mCbShowCamera.setOnCheckedChangeListener(this);
        mCbCrop = (AppCompatCheckBox) findViewById(R.id.cb_crop);
        mCbCrop.setOnCheckedChangeListener(this);
        mCropStyleSpinner = (AppCompatSpinner) findViewById(R.id.sp_crop_style);
        mCropStyleSpinner.setOnItemSelectedListener(this);
        mCbOutputRectangle = (AppCompatCheckBox) findViewById(R.id.cb_output_rectangle);
        mCbOutputRectangle.setOnCheckedChangeListener(this);
        mLayoutCrop = (LinearLayout) findViewById(R.id.layout_crop);
        mEtFocusW = (AppCompatEditText) findViewById(R.id.et_focus_w);
        mEtFocusH = (AppCompatEditText) findViewById(R.id.et_focus_h);
        mEtOutputX = (AppCompatEditText) findViewById(R.id.et_output_x);
        mEtOutputY = (AppCompatEditText) findViewById(R.id.et_output_y);

        mBtnPickImage = (Button) findViewById(R.id.btn_pick_image);
        mBtnPickImage.setOnClickListener(this);
        mBtnPreview = (Button) findViewById(R.id.btn_preview);
        mBtnPreview.setOnClickListener(this);
        mIvImage = (ImageView) findViewById(R.id.iv_image);
    }

    private void showMultiSelectUI() {
        mEtMaxSelectCount.setVisibility(View.VISIBLE);
        mCbShowCamera.setVisibility(View.VISIBLE);

        mCbCrop.setVisibility(View.GONE);
        mCropStyleSpinner.setVisibility(View.GONE);
        mCbOutputRectangle.setVisibility(View.GONE);
        mLayoutCrop.setVisibility(View.GONE);
    }

    private void showSingleSelectUI() {
        mEtMaxSelectCount.setVisibility(View.GONE);
        mCbShowCamera.setVisibility(View.VISIBLE);
        mCbCrop.setVisibility(View.VISIBLE);
        mCropStyleSpinner.setVisibility(View.GONE);
    }

    private void toggleCropUI(boolean flag) {
        if (flag) {
            mCropStyleSpinner.setVisibility(View.VISIBLE);
            mCbOutputRectangle.setVisibility(View.VISIBLE);
            mLayoutCrop.setVisibility(View.VISIBLE);
        } else {
            mCropStyleSpinner.setVisibility(View.GONE);
            mCbOutputRectangle.setVisibility(View.GONE);
            mLayoutCrop.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        mCbShowCamera.setChecked(false);
        mCbCrop.setChecked(false);
        mCbOutputRectangle.setChecked(false);
        switch (checkedId) {
            case R.id.rb_multi_choice:
                mMulti = true;
                showMultiSelectUI();
                break;
            case R.id.rb_single_choice:
                mMulti = false;
                showSingleSelectUI();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mCbShowCamera) {
            mShowCamera = isChecked;
        } else if (buttonView == mCbCrop) {
            mShowCrop = isChecked;
            toggleCropUI(isChecked);
        } else if (buttonView == mCbOutputRectangle) {
            mSaveRectangle = isChecked;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pick_image:
                initPicker();
                break;
            case R.id.btn_preview:
                viewToPreview();
                break;
        }
    }

    private void viewToPreview() {
        ImagePicker.getInstance().previewImageForResult(this,
                new ImagePicker.Config(new GlideImageLoader()),
                urls,0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK) {
            if (data != null) {
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    boolean isOrigin = data.getBooleanExtra(ImagePicker.EXTRA_IS_ORIGIN, false);
                    if (!isOrigin) {
                        ArrayList<ImageItem> imageItems = ImagePicker.getInstance().getSelectedImages();
                        if (imageItems.size() > 0) {
                            ImageItem imageItem = imageItems.get(0);
                            Glide.with(getApplicationContext())
                                    .load(imageItem.path)
                                    .into(mIvImage);

                            ArrayList<String> paths = new ArrayList<>();
                            for (ImageItem imageItem1 : imageItems) {
                                paths.add(imageItem1.path);
                            }
                        }
                    } else {
                        //遍历做压缩处理
                        Toast.makeText(this, "稍后压缩", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void initPicker() {
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoader());
        String msc = mEtMaxSelectCount.getText().toString();
        if (!TextUtils.isEmpty(msc)) {
            mMaxSelectCount = Integer.valueOf(msc);
        }
        if (mMaxSelectCount == 1) {
            mMulti = false;
        }
        config.multiMode(mMulti);
        if (mMulti) {
            config.selectLimit(mMaxSelectCount);
        } else {
            config.selectLimit(1);
        }
        config.showCamera(mShowCamera);
        config.crop(mShowCrop);
        if (mShowCrop) {
            config.cropStyle(cropStyle);
            config.saveRectangle(mSaveRectangle);
            config.focusWidth(getFocusWidth());
            config.focusHeight(getFocusHeight());
            config.outPutX(getOutputX());
            config.outPutY(getOutputY());
        }
        ImagePicker.getInstance().pickImageForResult(this, config);
    }

    private int getFocusWidth() {
        String fw = mEtFocusW.getText().toString();
        if (!TextUtils.isEmpty(fw)) {
            mFocusWidth = Integer.valueOf(fw);
        }
        return mFocusWidth;
    }

    private int getFocusHeight() {
        String fh = mEtFocusH.getText().toString();
        if (!TextUtils.isEmpty(fh)) {
            mFocusHeight = Integer.valueOf(fh);
        }
        return mFocusHeight;
    }

    private int getOutputX() {
        String ox = mEtOutputX.getText().toString();
        if (!TextUtils.isEmpty(ox)) {
            mOutputX = Integer.valueOf(ox);
        }
        return mOutputX;
    }

    private int getOutputY() {
        String oy = mEtOutputY.getText().toString();
        if (!TextUtils.isEmpty(oy)) {
            mOutputY = Integer.valueOf(oy);
        }
        return mOutputY;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            cropStyle = CropImageView.RECTANGLE;
            mCbOutputRectangle.setVisibility(View.GONE);
        } else if (position == 1) {
            cropStyle = CropImageView.CIRCLE;
            mCbOutputRectangle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}