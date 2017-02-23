package io.github.changjiashuai.loader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/22 16:33.
 */

public interface ImageLoader {
    void displayImage(Context context, String path, ImageView imageView, int width, int height);

    void clearMemoryCache();
}
