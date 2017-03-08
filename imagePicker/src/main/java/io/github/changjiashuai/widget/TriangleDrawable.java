package io.github.changjiashuai.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorInt;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/24 15:22.
 */

public class TriangleDrawable extends ShapeDrawable {

    private Path mPath;

    public TriangleDrawable(@ColorInt int color) {
        mPath = new Path();
        getPaint().setColor(color);
        setShape(new TriangleShape(mPath, 32, 32));
        setBounds(0, 0, 4, 4);
    }

    private class TriangleShape extends Shape {

        private Path mPath;
        private float mWidth;
        private float mHeight;

        public TriangleShape(Path path, float width, float height) {
            mPath = path;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            canvas.save();
            //实例化路径
            mPath.moveTo(0, mHeight);// 此点为多边形的起点
            mPath.lineTo(mWidth, 0);
            mPath.lineTo(mWidth, mHeight);
            mPath.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(mPath, paint);
            canvas.restore();
        }
    }
}