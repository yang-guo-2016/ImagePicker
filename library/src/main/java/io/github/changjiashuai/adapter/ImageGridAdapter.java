package io.github.changjiashuai.adapter;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.changjiashuai.BaseActivity;
import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.Utils;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.library.R;
import io.github.changjiashuai.ui.ImageGridActivity;
import io.github.changjiashuai.widget.SuperCheckBox;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/23 16:46.
 */

public class ImageGridAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private ImagePicker mImagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> mImageItems;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages;   //全局保存的已经选中的图片数据
    private boolean isShowCamera;                   //是否显示拍照按钮
    private int mImageSize;                         //每个条目的大小
    private OnImageItemClickListener mOnImageItemClickListener; //图片被点击的监听

    public ImageGridAdapter(@NonNull Activity activity, @NonNull ArrayList<ImageItem> imageItems) {
        mActivity = activity;
        mImageItems = imageItems;
        mImageSize = Utils.getImageItemWidth(mActivity);
        mImagePicker = ImagePicker.getInstance();
        isShowCamera = mImagePicker.isShowCamera();
        mSelectedImages = mImagePicker.getSelectedImages();
    }

    public void refreshData(ArrayList<ImageItem> imageItems) {
        if (imageItems == null || imageItems.size() == 0) {
            mImageItems = new ArrayList<>();
        } else {
            mImageItems = imageItems;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return isShowCamera ? mImageItems.size() + 1 : mImageItems.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return null;
            }
            return mImageItems.get(position - 1);
        } else {
            return mImageItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = initItemViewWithCamera(parent);
        } else {
            convertView = initItemView(position, convertView, parent);
        }
        return convertView;
    }

    private View initItemViewWithCamera(ViewGroup parent) {
        View convertView = LayoutInflater.from(mActivity)
                .inflate(R.layout.image_grid_list_item_with_camera, parent, false);
        convertView.setLayoutParams(
                new AbsListView
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
        convertView.setTag(null);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((BaseActivity) mActivity).checkPermission(Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.CAMERA},
                            ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    mImagePicker.takePicture(mActivity, ImagePicker.REQUEST_CODE_TAKE);
                }
            }
        });
        return convertView;
    }

    private View initItemView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity)
                    .inflate(R.layout.image_grid_list_item, parent, false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ImageItem imageItem = getItem(position);

        holder.ivThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnImageItemClickListener != null)
                    mOnImageItemClickListener.onImageItemClick(holder.rootView, imageItem, position);
            }
        });
        holder.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectLimit = mImagePicker.getSelectLimit();
                if (holder.cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                    Toast.makeText(mActivity.getApplicationContext(),
                            mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    holder.cbCheck.setChecked(false);
                    holder.mask.setVisibility(View.GONE);
                } else {
                    mImagePicker.addSelectedImageItem(position, imageItem, holder.cbCheck.isChecked());
                    holder.mask.setVisibility(View.VISIBLE);
                }
            }
        });
        //根据是否多选，显示或隐藏checkbox
        if (mImagePicker.isMultiMode()) {
            holder.cbCheck.setVisibility(View.VISIBLE);
            boolean checked = mSelectedImages.contains(imageItem);
            if (checked) {
                holder.mask.setVisibility(View.VISIBLE);
                holder.cbCheck.setChecked(true);
            } else {
                holder.mask.setVisibility(View.GONE);
                holder.cbCheck.setChecked(false);
            }
        } else {
            holder.cbCheck.setVisibility(View.GONE);
        }
        mImagePicker.getImageLoader().displayImage(mActivity, imageItem.path, holder.ivThumb, mImageSize, mImageSize); //显示图片
        return convertView;
    }

    private class ViewHolder {
        public View rootView;
        public ImageView ivThumb;
        public View mask;
        public SuperCheckBox cbCheck;

        public ViewHolder(View view) {
            rootView = view;
            ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            mask = view.findViewById(R.id.mask);
            cbCheck = (SuperCheckBox) view.findViewById(R.id.cb_check);
        }
    }

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.mOnImageItemClickListener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }
}
