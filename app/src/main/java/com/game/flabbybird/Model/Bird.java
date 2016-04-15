package com.game.flabbybird.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.game.flabbybird.MyUtil.MyUtil;

/**
 * Created by lhqj on 2016/4/13.
 */
public class Bird {
    /**
     * 鸟在屏幕高度的2/3位置
     */
    private static final float RADIO_POS_HEIGHT = 2 / 3F;
    /**
     * 鸟的高度30dp
     */
    private static final int  BRID_SIZE = 30;
    //鸟的位置
    private int x;
    private int y;
    /**
     * 鸟的宽度
     */
    private int mWidth;
    /**
     * 鸟的高度
     */
    private int mHeight;

    /**
     * 鸟的图像
     */
    private Bitmap bitmap;
    private RectF rectF = new RectF();

    /**
     *构造函数
     * @param context
     * @param gameWidth:游戏界面的宽度
     * @param gameHeight：游戏界面的高度
     * @param bitmap：鸟的图像
     */
    public Bird(Context context,int gameWidth,int gameHeight,Bitmap bitmap){
        this.bitmap = bitmap;
        //鸟的位置
        x = gameWidth / 2 - bitmap.getWidth() / 2;
        y = (int) (gameHeight * RADIO_POS_HEIGHT);

        //计算鸟的宽高(dp和px的转化)
        mWidth = MyUtil.dp2px(context, BRID_SIZE);
        mHeight = (int) (mWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());

    }

    public void drawBird(Canvas canvas){
        rectF.set(x,y,x + mWidth,y + mHeight);
        canvas.drawBitmap(bitmap,null,rectF,null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }
}
