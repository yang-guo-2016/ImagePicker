package io.github.changjiashuai.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.ui.ImagePreviewActivity;
import io.github.changjiashuai.widget.CropImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView mIvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_multi_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMultiPicker();
            }
        });
        findViewById(R.id.btn_single_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSinglePicker();
            }
        });
        mIvImage = (ImageView) findViewById(R.id.iv_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImagePicker.REQUEST_CODE_PICK) {
            if (data != null) {
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    boolean isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                    if (!isOrigin) {
                        ArrayList<ImageItem> imageItems = ImagePicker.getInstance().getSelectedImages();
                        if (imageItems.size() > 0) {
                            ImageItem imageItem = imageItems.get(0);
                            Glide.with(getApplicationContext())
                                    .load(imageItem.path)
                                    .into(mIvImage);
                        }
                    } else {
                        //遍历做压缩处理
                        Toast.makeText(this, "稍后压缩", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initMultiPicker(){
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoder());
        config.multiMode(true).selectLimit(9);
        ImagePicker.getInstance().pickImageForResult(this, config);
    }

    private void initMultiPickerWithAllConfig(){
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoder());
        config.multiMode(true).selectLimit(9).showCamera(true).outPutX(1000).outPutY(1000).focusWidth(800).focusHeight(800);
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
                .showCamera(false).outPutX(1000).outPutY(1000).focusWidth(800).focusHeight(800);;
        ImagePicker.getInstance().pickImageForResult(this, config);
    }
}
