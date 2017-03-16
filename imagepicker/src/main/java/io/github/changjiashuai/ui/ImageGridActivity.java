package io.github.changjiashuai.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.util.List;

import io.github.changjiashuai.BaseActivity;
import io.github.changjiashuai.ImageDataSource;
import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.R;
import io.github.changjiashuai.adapter.ImageFolderAdapter;
import io.github.changjiashuai.adapter.ImageGridAdapter;
import io.github.changjiashuai.bean.ImageFolder;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.widget.FolderPopUpWindow;
import io.github.changjiashuai.widget.TriangleDrawable;

public class ImageGridActivity extends BaseActivity implements ImagePicker.OnImageSelectedListener, View.OnClickListener, ImageDataSource.OnImagesLoadedListener, ImageGridAdapter.OnImageItemClickListener {

    private static final String TAG = "ImageGridActivity";
    public static final int RC_STORAGE_CODE = 0x01;
    public static final int RC_CAMERA_CODE = 0x02;

    /*是否选中原图*/
    private boolean isOrigin = false;
    /*图片展示控件*/
    private GridView mGridView;
    /*底部栏*/
    private View mFooterBar;
    /*确定按钮*/
    private Button mBtnOk;
    /*文件夹切换按钮*/
    private Button mBtnDir;
    /*预览按钮*/
    private Button mBtnPreview;
    /*图片文件夹的适配器*/
    private ImageFolderAdapter mImageFolderAdapter;
    /*ImageSet的PopupWindow*/
    private FolderPopUpWindow mFolderPopupWindow;
    /*所有的图片文件夹*/
    private List<ImageFolder> mImageFolders;
    /*图片展示的适配器*/
    private ImageGridAdapter mImageGridAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        initView();
        initData();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new ImageDataSource(this, null, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_STORAGE_CODE);
            }
        }
    }

    private void initView() {
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnDir = (Button) findViewById(R.id.btn_dir);
        int triangleFillColor = getResources()
                .getColor(R.color.image_picker_image_grid_bottom_bar_button_dir_indicator_fill_color);
        mBtnDir.setCompoundDrawablesWithIntrinsicBounds(null, null,
                new TriangleDrawable(triangleFillColor), null);
        mBtnDir.setOnClickListener(this);
        mBtnPreview = (Button) findViewById(R.id.btn_preview);
        mBtnPreview.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mFooterBar = findViewById(R.id.footer_bar);
        if (ImagePicker.getInstance().isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPreview.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPreview.setVisibility(View.GONE);
        }
    }

    private void initData() {
        ImagePicker.getInstance().clear();
        ImagePicker.getInstance().addOnImageSelectedListener(this);
        mImageGridAdapter = new ImageGridAdapter(this);
        mImageFolderAdapter = new ImageFolderAdapter(this, new FolderPopUpWindow
                .OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mImageFolderAdapter.setSelectIndex(position);
                ImagePicker.getInstance().setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();
                ImageFolder imageFolder = mImageFolderAdapter.getItem(position);
                if (null != imageFolder) {
                    mImageGridAdapter.refreshData(imageFolder.images);
                    mBtnDir.setText(imageFolder.name);
                }
                mGridView.smoothScrollToPosition(0);//滑动到顶部
            }
        });
        onImageSelected(0, null, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_STORAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ImageDataSource(this, null, this);
            } else {
                showToast(getString(R.string.storage_reject));
            }
        } else if (requestCode == RC_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImagePicker.getInstance().takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
            } else {
                showToast(getString(R.string.camera_reject));
            }
        }
    }

    @Override
    protected void onDestroy() {
        ImagePicker.getInstance().removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onImageSelected(int position, ImageItem imageItem, boolean isAdd) {
        if (ImagePicker.getInstance().getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.image_picker_select_complete,
                    ImagePicker.getInstance().getSelectImageCount(),
                    ImagePicker.getInstance().getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnOk.getBackground().setColorFilter(getResources()
                    .getColor(R.color.image_picker_button_normal), PorterDuff.Mode.MULTIPLY);
            mBtnPreview.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.image_picker_complete));
            mBtnOk.setEnabled(false);
            mBtnOk.getBackground().setColorFilter(getResources()
                    .getColor(R.color.image_picker_button_disabled), PorterDuff.Mode.MULTIPLY);
            mBtnPreview.setEnabled(false);
        }
        mBtnPreview.setText(getResources().getString(R.string.image_picker_preview_count,
                ImagePicker.getInstance().getSelectImageCount()));
        mImageGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            finishWithResult();
        } else if (id == R.id.btn_dir) {
            changeImageDir();
        } else if (id == R.id.btn_preview) {
            viewToPreviewActivity(0, true);
        } else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    private void changeImageDir() {
        if (mImageFolders == null) {
            Log.i("ImageGridActivity", "您的手机没有图片");
            return;
        }
        //点击文件夹按钮
        showPopupFolderWindow();
    }

    /**
     * 创建弹出的ListView
     */
    private void showPopupFolderWindow() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
        mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据
        if (mFolderPopupWindow.isShowing()) {
            mFolderPopupWindow.dismiss();
        } else {
            mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
            //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
            int index = mImageFolderAdapter.getSelectIndex();
            index = index == 0 ? index : index - 1;
            mFolderPopupWindow.setSelection(index);
        }
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        mImageFolders = imageFolders;
        ImagePicker.getInstance().setImageFolders(mImageFolders);
        if (mImageFolders.size() == 0) {
            mImageGridAdapter.refreshData(null);
        } else {
            mImageGridAdapter.refreshData(mImageFolders.get(0).images);
        }
        mImageGridAdapter.setOnImageItemClickListener(this);
        mGridView.setAdapter(mImageGridAdapter);
        mImageFolderAdapter.refreshData(mImageFolders);
    }

    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        //根据是否有相机按钮确定位置
        position = ImagePicker.getInstance().isShowCamera() ? position - 1 : position;
        // 选中去预览（选中的所有图片）， 为选择预览（当前文件夹里所有图片）
        if (ImagePicker.getInstance().isMultiMode()) {
            if (ImagePicker.getInstance().isSelect(imageItem)) {
                // item selected show selected all image position=size-1
                position = ImagePicker.getInstance().getSelectImageCount() - 1;
                viewToPreviewActivity(position, true);
            } else {
                // item unselected show current folder all image
                viewToPreviewActivity(position, false);
            }
        } else {
            //  单选裁剪或返回
            ImagePicker.getInstance().clearSelectedImages();
            ImagePicker.getInstance().updateSelectedImageItem(position,
                    ImagePicker.getInstance().getCurrentImageFolderItems().get(position), true);
            if (ImagePicker.getInstance().isCrop()) {
                viewToCropActivity();
            } else {
                finishWithResult();
            }
        }
    }

    private boolean deleteImage(String imgPath) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
                        MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=?",new String[] { imgPath }, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE_TAKE) {
            //拍照
            if (resultCode == RESULT_CANCELED) {
                // TODO: 2017/3/16 maybe have good idea!
                File tempFile = ImagePicker.getInstance().getTakeImageFile();
                deleteImage(tempFile.getPath());
                mImageGridAdapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_OK) {
                //发送广播通知图片增加了
                ImagePicker.galleryAddPic(this, ImagePicker.getInstance().getTakeImageFile());
                ImageItem imageItem = new ImageItem();
                imageItem.path = ImagePicker.getInstance().getTakeImageFile().getAbsolutePath();
                ImagePicker.getInstance().clearSelectedImages();
                ImagePicker.getInstance().updateSelectedImageItem(0, imageItem, true);
                if (ImagePicker.getInstance().isCrop()) {
                    viewToCropActivity();
                } else {
                    finishWithResult();
                }
            }
        } else if (requestCode == ImagePicker.REQUEST_CODE_CROP) {
            //裁剪
            finishWithResult();
        } else if (requestCode == ImagePicker.REQUEST_CODE_GRID_PREVIEW) {
            //预览
            if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(ImagePicker.EXTRA_IS_ORIGIN, false);
                // 直接从预览返回
                mImageGridAdapter.notifyDataSetChanged();
            } else if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                isOrigin = data.getBooleanExtra(ImagePicker.EXTRA_IS_ORIGIN, false);
                // 在预览界面选择后确定返回
                finishWithResult();
            }
        }
    }

    private void viewToPreviewActivity(int position, boolean showSelected) {
        Intent intent = new Intent(this, ImageGridPreviewActivity.class);
        intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
        intent.putExtra(ImagePicker.EXTRA_SHOW_SELECTED, showSelected);
        intent.putExtra(ImagePicker.EXTRA_IS_ORIGIN, isOrigin);
        startActivityForResult(intent, ImagePicker.REQUEST_CODE_GRID_PREVIEW);  //如果是多选，点击图片进入预览界面
    }

    private void viewToCropActivity() {
        Intent intent = new Intent(this, ImageCropActivity.class);
        startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);//单选需要裁剪，进入裁剪界面
    }

    private void finishWithResult() {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_IS_ORIGIN, isOrigin);
        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
        finish();
    }
}