package io.github.changjiashuai;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.changjiashuai.bean.ImageFolder;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.loader.ImageLoader;
import io.github.changjiashuai.ui.ImageGridActivity;
import io.github.changjiashuai.ui.ImagePreviewActivity;
import io.github.changjiashuai.widget.CropImageView;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/22 16:29.
 */

public class ImagePicker {

    public static final int REQUEST_CODE_PICK = 1000;
    public static final int REQUEST_CODE_TAKE = 1001;
    public static final int REQUEST_CODE_CROP = 1002;
    public static final int REQUEST_CODE_GRID_PREVIEW = 1003;
    public static final int REQUEST_CODE_PREVIEW = 1004;

    public static final int RESULT_CODE_ITEMS = 1004;
    public static final int RESULT_CODE_BACK = 1005;

    public static final String EXTRA_SELECTED_IMAGE_POSITION = "extra_selected_image_position";
    public static final String EXTRA_SHOW_SELECTED = "extra_show_selected";
    public static final String EXTRA_IS_ORIGIN = "isOrigin";

    /*image picker 启动配置*/
    private Config mConfig;
    private File cropCacheFolder;
    private File takeImageFile;
    /*选中的图片集合*/
    private ArrayList<ImageItem> mSelectedImages = new ArrayList<>();
    /*所有的图片文件夹*/
    private List<ImageFolder> mImageFolders;
    /*当前选中的文件夹位置， 0表示所有图片*/
    private int mCurrentImageFolderPosition = 0;
    /*图片选中的监听回调*/
    private List<OnImageSelectedListener> mImageSelectedListeners;

    private static volatile ImagePicker singleton = null;

    public static ImagePicker getInstance() {
        if (singleton == null) {
            synchronized (ImagePicker.class) {
                if (singleton == null) {
                    singleton = new ImagePicker();
                }
            }
        }
        return singleton;
    }

    private ImagePicker() {
    }

    public void pickImageForResult(Activity activity, @NonNull Config config) {
        mConfig = config;
        if (mConfig == null) {
            throw new IllegalArgumentException("config must be set!!!");
        }
        Intent intent = new Intent();
        if (activity != null) {
            intent.setClass(activity, ImageGridActivity.class);
            activity.startActivityForResult(intent, REQUEST_CODE_PICK);
        }
    }

    public void previewImageForResult(Activity activity, @NonNull Config config,
                                      ArrayList<String> urls, int currentItemPosition) {
        mConfig = config;
        if (mConfig == null) {
            throw new IllegalArgumentException("config must be set!!!");
        }
        Intent intent = new Intent();
        if (activity != null) {
            intent.setClass(activity, ImagePreviewActivity.class);
            intent.putStringArrayListExtra(ImagePreviewActivity.EXTRA_URLS, urls);
            intent.putExtra(ImagePreviewActivity.EXTRA_CURRENT_ITEM_POSITION, currentItemPosition);
            activity.startActivityForResult(intent, REQUEST_CODE_PREVIEW);
        }
    }

    public boolean isMultiMode() {
        return mConfig.multiMode;
    }

    public boolean isCrop() {
        return mConfig.crop;
    }

    public boolean isSaveRectangle() {
        return mConfig.saveRectangle;
    }

    public boolean isShowCamera() {
        return mConfig.showCamera;
    }

    public int getSelectLimit() {
        return mConfig.selectLimit;
    }

    public int getOutPutX() {
        return mConfig.outPutX;
    }

    public int getOutPutY() {
        return mConfig.outPutY;
    }

    public int getFocusWidth() {
        return mConfig.focusWidth;
    }

    public int getFocusHeight() {
        return mConfig.focusHeight;
    }

    public ImageLoader getImageLoader() {
        return mConfig.mImageLoader;
    }

    public Config getConfig() {
        return mConfig;
    }

    public
    @CropImageView.Style
    int getStyle() {
        return mConfig.style;
    }

    public File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getCacheDir() + "/ImagePicker/cropTemp/");
        }
        return cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    public File getTakeImageFile() {
        return takeImageFile;
    }

    public void setTakeImageFile(File takeImageFile) {
        this.takeImageFile = takeImageFile;
    }

    public List<ImageFolder> getImageFolders() {
        return mImageFolders;
    }

    public void setImageFolders(List<ImageFolder> imageFolders) {
        mImageFolders = imageFolders;
    }

    public int getCurrentImageFolderPosition() {
        return mCurrentImageFolderPosition;
    }

    public void setCurrentImageFolderPosition(int currentImageFolderPosition) {
        mCurrentImageFolderPosition = currentImageFolderPosition;
    }

    public ArrayList<ImageItem> getCurrentImageFolderItems() {
        return mImageFolders.get(mCurrentImageFolderPosition).images;
    }

    public boolean isSelect(ImageItem imageItem) {
        return mSelectedImages.contains(imageItem);
    }

    public int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    public ArrayList<ImageItem> getSelectedImages() {
        return mSelectedImages;
    }

    public void setSelectedImages(ArrayList<ImageItem> selectedImages) {
        mSelectedImages = selectedImages;
    }

    public void clearSelectedImages() {
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
    }

    public void clear() {
        if (mImageSelectedListeners != null) {
            mImageSelectedListeners.clear();
            mImageSelectedListeners = null;
        }
        if (mImageFolders != null) {
            mImageFolders.clear();
            mImageFolders = null;
        }
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
        mCurrentImageFolderPosition = 0;
    }

    /**
     * 拍照的方法
     */
    public void takePicture(Activity activity, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard()) {
                takeImageFile = new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
            } else {
                takeImageFile = Environment.getDataDirectory();
            }
            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
            if (takeImageFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, takeImageFile.getAbsolutePath());
                    Uri contentUri = activity.getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takeImageFile));
                }
            }
        }
        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /**
     * 扫描图片
     */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 图片选中的监听
     */
    public interface OnImageSelectedListener {
        void onImageSelected(int position, ImageItem imageItem, boolean isAdd);
    }

    public void addOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) {
            mImageSelectedListeners = new ArrayList<>();
        }
        mImageSelectedListeners.add(l);
    }

    public void removeOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) {
            return;
        }
        mImageSelectedListeners.remove(l);
    }

    public void updateSelectedImageItem(int position, ImageItem item, boolean isAdd) {
        if (isAdd) {
            addSelectedImageItem(item);
        } else {
            removeSelectedImageItem(item);
        }
        notifyImageSelectedChanged(position, item, isAdd);
        Log.i("TAG", "updateSelectedImageItem: " + mSelectedImages);
    }

    public void addSelectedImageItem(ImageItem imageItem) {
        if (mSelectedImages.size() < getSelectLimit()) {
            if (mSelectedImages.contains(imageItem)) {
                return;
            }
            mSelectedImages.add(imageItem);
        }
    }

    public void removeSelectedImageItem(ImageItem imageItem) {
        if (mSelectedImages.contains(imageItem)) {
            mSelectedImages.remove(imageItem);
        }
    }

    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        if (mImageSelectedListeners == null) {
            return;
        }
        for (OnImageSelectedListener l : mImageSelectedListeners) {
            l.onImageSelected(position, item, isAdd);
        }
    }

    public static class Config {

        //============must===============//
        /*图片加载器*/
        private ImageLoader mImageLoader;

        //============optional===========//
        /*图片选择模式*/
        private boolean multiMode = true;
        /*裁剪*/
        private boolean crop = true;
        /*裁剪后的图片是否是矩形，否则跟随裁剪框的形状*/
        private boolean saveRectangle = false;
        /*最大选择图片数量*/
        private int selectLimit = 9;
        /*裁剪框的形状*/
        @CropImageView.Style
        private int style = CropImageView.RECTANGLE;

        //===========default=============//
        /*显示相机*/
        private boolean showCamera = true;
        /*裁剪保存文件的宽度。单位像素*/
        private int outPutX = 800;
        /*裁剪保存文件的高度。单位像素*/
        private int outPutY = 800;
        /*裁剪框的宽度。单位像素（圆形自动取宽高最小值）*/
        private int focusWidth = 400;
        /*裁剪框的高度。单位像素（圆形自动取宽高最小值）*/
        private int focusHeight = 400;

        public Config(ImageLoader imageLoader) {
            mImageLoader = imageLoader;
        }

        public Config multiMode(boolean multiMode) {
            this.multiMode = multiMode;
            return this;
        }

        public Config crop(boolean crop) {
            this.crop = crop;
            return this;
        }

        public Config saveRectangle(boolean saveRectangle) {
            this.saveRectangle = saveRectangle;
            return this;
        }

        public Config showCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        public Config selectLimit(int selectLimit) {
            this.selectLimit = selectLimit;
            return this;
        }

        public Config outPutX(int outPutX) {
            this.outPutX = outPutX;
            return this;
        }

        public Config outPutY(int outPutY) {
            this.outPutY = outPutY;
            return this;
        }

        public Config focusWidth(int focusWidth) {
            this.focusWidth = focusWidth;
            return this;
        }

        public Config focusHeight(int focusHeight) {
            this.focusHeight = focusHeight;
            return this;
        }

        public Config cropStyle(@CropImageView.Style int style) {
            this.style = style;
            return this;
        }
    }
}