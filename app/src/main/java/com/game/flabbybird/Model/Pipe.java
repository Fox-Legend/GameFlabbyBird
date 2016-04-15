package com.game.flabbybird.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;


import java.util.Random;

/**
 * 管道类
 * 分为上下管道
 */
public class Pipe {

    /**
     * 上下管道之间距离比例，游戏界面高度的1/5
     */
    private static final float RADIO_BETWEEN_UP_DOWN = 1 / 5F;
    /**
     * 上管道的最大高度
     */
    private static final float  RADIO_MAX_HEIGHT = 2 / 5F;
    /**
     * 上管道的最小高度
     */
    private static final float RADIO_MIN_HEIGHT = 1 / 6F;
    /**
     * 管道的横坐标
     */
    private int x;
    /**
     * 上管道的高度
     */
    private int height;
    /**
     * 上下管道之间的距离
     */
    private int margin;
    /**
     * 上管道的图像
     */
    private Bitmap mTopBg;
    /**
     * 下管道的图像
     */
    private Bitmap mBottomBg;
    private static Random random = new Random();



    /**
     *
     * @param context
     * @param gameWidth:默认从最左边出现
     * @param gameHeight：
     * @param mTop
     * @param mBottom
     */
    public Pipe(Context context,int gameWidth,int gameHeight,Bitmap mTop,Bitmap mBottom){
        margin = (int) (gameHeight * RADIO_BETWEEN_UP_DOWN);
        x = gameWidth;
        mTopBg = mTop;
        mBottomBg = mBottom;

        randomHeight(gameHeight);//随机出现的y

    }

    /**
     * 随机生成一个高度
     * @param gameHeight
     */
    private void  randomHeight(int gameHeight){
        //在最大高度和最小高度之间生成一个随机值
        height = random.nextInt((int) (gameHeight * (RADIO_MAX_HEIGHT - RADIO_MIN_HEIGHT)));
        height = (int) (height + gameHeight * RADIO_MIN_HEIGHT);
    }


    /**
     * s检测鸟是否撞到了管道
     * @param mBird
     * @return
     */
    public boolean touchBird(Bird mBird){
        if( mBird.getX() + mBird.getmWidth() > x
                && (mBird.getY() < height || mBird.getY() + mBird.getmHeight() > height
                + margin)){
            return true;
        }
        return false;

    }
    /**
     * 绘制管道
     * @param mCanvas
     * @param rectF
     */
    public void drawPipe(Canvas mCanvas,RectF rectF){
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        //rectF为整个管道，假设完成的管道为100，需要绘制20，则向上偏移80
        mCanvas.translate(x,-(rectF.bottom - height));
        mCanvas.drawBitmap(mTopBg,null,rectF,null);

        //下管道，偏移值：上管道高度+margin
        mCanvas.translate(0, rectF.bottom + margin);
        mCanvas.drawBitmap(mBottomBg, null, rectF, null);
        mCanvas.restore();


    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
