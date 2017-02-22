package io.github.changjiashuai.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/22 16:45.
 */

public class CropImageView extends ImageView {

    public static final int RECTANGLE = 0;
    public static final int CIRCLE = 1;

    @IntDef({RECTANGLE, CIRCLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Style {}

    @Style
    private int[] mStyles = {RECTANGLE, CIRCLE};

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
