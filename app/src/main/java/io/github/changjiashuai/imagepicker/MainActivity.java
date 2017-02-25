package io.github.changjiashuai.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.ui.ImageGridActivity;
import io.github.changjiashuai.ui.ImagePreviewActivity;
import io.github.changjiashuai.widget.CropImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImagePicker();
        findViewById(R.id.btn_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == ImagePicker.REQUEST_CODE_PICK) {
            if (data != null) {
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    boolean isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                    Log.i(TAG, "onActivityResult: isOrigin=" + isOrigin);
                    Log.i(TAG, "onActivityResult: getSelectedImages" +
                            ImagePicker.getInstance().getSelectedImages());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化仿微信控件ImagePicker
     */

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoder());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(true);
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }
}
