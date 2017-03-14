package io.github.changjiashuai.bean;

import java.io.Serializable;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/22 16:24.
 */

public class ImageItem implements Serializable {

    public String name;       //图片的名字
    public String path;       //图片的路径
    public long size;         //图片的大小
    public int width;         //图片的宽度
    public int height;        //图片的高度
    public String mimeType;   //图片的类型
    public long createTime;   //图片的创建时间

    /**
     * 图片的路径和创建时间相同就认为是同一张图片
     */
    @Override
    public boolean equals(Object o) {
        try {
            ImageItem other = (ImageItem) o;
            return this.path.equalsIgnoreCase(other.path) && this.createTime == other.createTime;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}