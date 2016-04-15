package com.game.flabbybird.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * 地板类
 */
public class Floor {

    /**
     * 地板位置在游戏面板高度的4/5底部
     */
    private static final float FLOOR_Y_POS_RADIO = 4 / 5f;

    /**
     * x,y坐标
     */
    private int x;
    private int y;
    /**
     * 用BitmapShaper填充
     */
    private BitmapShader mFloorShader;
    private int mGameWidth;//整个游戏界面的宽高
    private int mGameHeight;

    /**
     *
     * @param gameWidth:游戏界面宽度
     * @param gameHeight：游戏界面高度
     * @param floorBg：地板的图像
     */
    public Floor(int gameWidth,int gameHeight,Bitmap floorBg){
        this.mGameHeight = gameHeight;
        this.mGameWidth = gameWidth;
        y = (int) (gameHeight * FLOOR_Y_POS_RADIO);
        //刷子重复
        mFloorShader = new BitmapShader(floorBg, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);

    }

    /**
     * 绘制地板
     * @param mCanvas
     * @param mPaint
     */
    public void drawFloor(Canvas mCanvas,Paint mPaint){
        if(-x >mGameWidth){//当滑动距离超过游戏宽
            x = x % mGameWidth;
        }
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        //移动到指定的位置
        mCanvas.translate(x,y);
        mPaint.setShader(mFloorShader);

        mCanvas.drawRect(x, 0, -x + mGameWidth, mGameHeight - y, mPaint);
        mCanvas.restore();
        mPaint.setShader(null);

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
}
