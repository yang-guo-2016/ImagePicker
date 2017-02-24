package io.github.changjiashuai.ui;

import io.github.changjiashuai.BasePresenter;
import io.github.changjiashuai.BaseView;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/23 23:19.
 */

public interface ImageGridContract {
    interface View extends BaseView<Presenter> {
        void renderImageGrid();
        void viewToImagePreview();
    }

    interface Presenter extends BasePresenter {
        void loadImages();
    }
}
