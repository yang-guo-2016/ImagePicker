package io.github.changjiashuai.imagepicker;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.github.changjiashuai.loader.ImageLoader;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/23 17:56.
 */

public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Context context, String path, ImageView imageView, int width, int height) {
        Glide.with(context)
                .load(path)
                .centerCrop()
//                .placeholder(R.drawable.default_image)
                .into(imageView);
    }
}
