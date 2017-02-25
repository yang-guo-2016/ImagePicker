package io.github.changjiashuai.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.Utils;
import io.github.changjiashuai.bean.ImageFolder;
import io.github.changjiashuai.library.R;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/23 17:29.
 */

public class ImageFolderAdapter extends BaseAdapter {

    private ImagePicker mImagePicker;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private int mImageSize;
    private List<ImageFolder> mImageFolders;
    private int lastSelected = 0;

    public ImageFolderAdapter(@NonNull Activity activity, @NonNull List<ImageFolder> imageFolders) {
        mActivity = activity;
        mImageFolders = imageFolders;
        mImagePicker = ImagePicker.getInstance();
        mImageSize = Utils.getImageItemWidth(mActivity);
        mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<ImageFolder> imageFolders) {
        if (imageFolders == null || imageFolders.size() == 0) {
            mImageFolders = new ArrayList<>();
        } else {
            mImageFolders = imageFolders;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageFolders.size();
    }

    @Override
    public ImageFolder getItem(int position) {
        return mImageFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.folder_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageFolder folder = getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(mActivity.getString(R.string.image_picker_folder_image_count, folder.images.size()));
        mImagePicker.getImageLoader().displayImage(mActivity, folder.cover.path, holder.cover, mImageSize, mImageSize);

        if (lastSelected == position) {
            holder.folderCheck.setChecked(true);
        } else {
            holder.folderCheck.setChecked(false);
        }
        return convertView;
    }

    public void setSelectIndex(int position) {
        if (lastSelected == position) {
            return;
        }
        lastSelected = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        AppCompatCheckBox folderCheck;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.iv_cover);
            folderName = (TextView) view.findViewById(R.id.tv_folder_name);
            imageCount = (TextView) view.findViewById(R.id.tv_image_count);
            folderCheck = (AppCompatCheckBox) view.findViewById(R.id.cb_folder_check);
            view.setTag(this);
        }
    }
}