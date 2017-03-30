# ImagePicker
图片选择器，提供多种图片加载接口，可以配置

test


[![Download](https://api.bintray.com/packages/changjiashuai/maven/imagepicker/images/download.svg)](https://bintray.com/changjiashuai/maven/imagepicker/_latestVersion)


 
### Adding to your project

> Add the jcenter repository information in your build.gradle file like this

```

dependencies {
	compile 'io.github.changjiashuai:imagepicker:{releaseVersion}'
}

```


## 使用

```
	private void initMultiPicker(){
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoder());
        config.multiMode(true).selectLimit(9);
        ImagePicker.getInstance().pickImageForResult(this, config);
    }

    private void initMultiPickerWithAllConfig(){
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoder());
        config.multiMode(true).selectLimit(9)
                .showCamera(true).outPutX(1000).outPutY(1000).focusWidth(800).focusHeight(800);
        ImagePicker.getInstance().pickImageForResult(this, config);
    }

    private void initSinglePicker(){
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoder());
        config.multiMode(false).selectLimit(1).crop(true).saveRectangle(false).cropStyle(CropImageView.CIRCLE);
        ImagePicker.getInstance().pickImageForResult(this, config);
    }

    private void initSinglePickerWithAllConfig(){
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoder());
        config.multiMode(false).selectLimit(1).crop(true).saveRectangle(false).cropStyle(CropImageView.CIRCLE)
                .showCamera(false).outPutX(1000).outPutY(1000).focusWidth(800).focusHeight(800);
        ImagePicker.getInstance().pickImageForResult(this, config);
    }
```

## 主题配置


```
	<!--pressed button in bar-->
    <color name="image_picker_button_pressed_color">#cc22292c</color>

    <!--statusBar-->
    <color name="image_picker_status_bar">#303135</color>

    <!--toolbar-->
    <color name="image_picker_toolbar_background">#303135</color>
    <color name="image_picker_toolbar_divider_background">#cc111111</color>
    <color name="image_picker_toolbar_title_text_color">#ffffff</color>
    <color name="image_picker_toolbar_button_text_color">#ffffff</color>

    <!--image grid-->
    <color name="image_picker_image_grid_background">#111111</color>
    <color name="image_picker_image_grid_list_item_mask_color">#88000000</color>
    <color name="image_picker_image_grid_bottom_bar_background">#303135</color>
    <color name="image_picker_image_grid_bottom_bar_button_dir_text_color">#FFFFFF</color>
    <color name="image_picker_image_grid_bottom_bar_button_dir_indicator_fill_color">#ff9e9e9e</color>
    <color name="image_picker_image_grid_bottom_bar_button_preview_text_color">#FFFFFF</color>
    <color name="image_picker_image_grid_bottom_bar_divider_background">#cc111111</color>

    <!--image folder-->
    <color name="image_picker_folder_popup_window_mask_view_background">#C000</color>
    <color name="image_picker_folder_popup_window_bottom_margin_view_background">#0000</color>

    <color name="image_picker_folder_popup_window_list_item_background">#FFFFFF</color>
    <color name="image_picker_folder_popup_window_list_item_folder_name_text_color">#000</color>
    <color name="image_picker_folder_popup_window_list_item_image_count_text_color">#d2d2d7</color>
    <color name="image_picker_folder_popup_window_list_item_divider">#f0f0f4</color>

    <!--image preview-->
    <color name="image_picker_image_preview_background">#000</color>
    <color name="image_picker_image_preview_bottom_bar_background">#cc22292c</color>
    <color name="image_picker_image_preview_bottom_bar_origin_text_color">#ffffff</color>
    <color name="image_picker_image_preview_bottom_bar_select_text_color">#ffffff</color>

    <!--image crop-->
    <color name="image_picker_image_crop_background">#000</color>

    <!--image checkbox-->
    <color name="image_picker_checkbox_normal">@android:color/darker_gray</color>
    <color name="image_picker_checkbox_selected">#45B600</color>

    <!--image radio button-->
    <color name="image_picker_radiobutton_normal">@android:color/darker_gray</color>
    <color name="image_picker_radiobutton_selected">#45B600</color>

    <!--image button-->
    <color name="image_picker_button_normal">#2A7300</color>
    <color name="image_picker_button_disabled">#7fa87f</color>
    <color name="image_picker_button_selected">#45B600</color>
```
