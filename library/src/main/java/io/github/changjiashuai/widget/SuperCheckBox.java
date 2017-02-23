package io.github.changjiashuai.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.CheckBox;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/23 17:20.
 */

public class SuperCheckBox extends CheckBox {
    public SuperCheckBox(Context context) {
        super(context);
    }

    public SuperCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            playSoundEffect(SoundEffectConstants.CLICK);
        }
        return handled;
    }
}
