package com.game.flabbybird.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.game.flabbybird.Model.Bird;
import com.game.flabbybird.Model.Floor;
import com.game.flabbybird.Model.Pipe;
import com.game.flabbybird.MyUtil.MyUtil;
import com.game.flabbybird.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhqj on 2016/4/13.
 */
public class GameFlabbyBird extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private static final String TAG = "GameFlabbyBird";
    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;

    /**
     *绘制线程
     */
    private Thread mThread;
    /**
     * 线程开关
     */
    private boolean isRunning;
    /**
     * 当前View的大小
     */
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();
    private Bird mBird;
    private Paint mPaint;
    /**
     * 背景图像
     */
    private Bitmap mBg;
    private Bitmap mBirdBg;

    /**
     * 地板相关
     */
    private Floor mFloor;
    private Bitmap mFloorBg;
    private int mSpeed;//移动速度

    /**
     * 管道相关
     */
    private Bitmap mTopBg;
    private Bitmap mBottomBg;
    private RectF mPipeRect;
    private int mPipeWidth;
    private static final int PIPE_WIDTH = 60;
    /**
     * 因为管道有很多，所以是一个序列
     */
    private List<Pipe> mPipes = new ArrayList<>();
    /**
     * 需要移除的管道
     */
    private List<Pipe> mNeedRemovePipe = new ArrayList<>();

    /**
     * 分数显示相关
     */
    private int[] mNums = new int[]{R.drawable.n0,R.drawable.n1,R.drawable.n2,R.drawable.n3,
            R.drawable.n4,R.drawable.n5,R.drawable.n6,R.drawable.n7,R.drawable.n8,R.drawable.n9};
    private Bitmap [] mNumBitmap;
    private int mGrade = 0;
    /**
     * 单个数字显示的高度为1/15
     */
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15f;
    /**
     * 单个数字显示的高度
     */
    private int mSingleGradeHeight;
    /**
     * 单个数字显示的宽度
     */
    private int mSingleGradeWidth;
    /**
     * 单个数字的范围
     */
    private RectF mSingleNumRectF;

    /**
     * 游戏状态
     */
    private enum GameStatus{
        WAITTING,RUNNING,STOP
    }

    /**
     * 记录游戏的状态
     */
    private GameStatus gameStatus = GameStatus.WAITTING;
    /**
     * 触摸上升的距离，因为是上升，值为负数
     */
    private static final int TOUCH_UP_SIZE = -16;
    /**
     * 鸟上升的距离
     */
    private final int mBirdUpDis = MyUtil.dp2px(getContext(), TOUCH_UP_SIZE);
    private int mTmpBirdDis;
    /**
     * 鸟下降的速度
     */
    private  final  int mAutoDownSpeed = MyUtil.dp2px(getContext(),2);

    /**
     * 两个管道间的距离
     */
    private final int PIPE_DIS_BETWEEN_TWO = MyUtil.dp2px(getContext(),150);
    /**
     * 记录两个管之间的移动间隔，若等于PIPE_DIS_BETWEEN_TWO则生成一下一根
     */
    private int mTmpMoveDistance;
    private int mRemovedPipe;

    public GameFlabbyBird(Context context) {
        this(context,null);
    }

    public GameFlabbyBird(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameFlabbyBird(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);//设置画布背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setFocusable(true);//设置可获得焦点
        setFocusableInTouchMode(true);

        //设置为常亮
        this.setKeepScreenOn(true);
        //地板画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        initBitmaps();

        //初始化速度
        mSpeed = MyUtil.dp2px(context, 2);//2
        mPipeWidth = MyUtil.dp2px(context,PIPE_WIDTH);

    }

    /**
     * 初始化图像
     */
    private void initBitmaps(){
        mBg = loadImageByResId(R.drawable.bg1);
        mBirdBg = loadImageByResId(R.drawable.b1);
        mFloorBg = loadImageByResId(R.drawable.floor_bg2);
        mTopBg = loadImageByResId(R.drawable.g2);
        mBottomBg = loadImageByResId(R.drawable.g1);

        mNumBitmap = new Bitmap[mNums.length];
        for(int i = 0;i<mNumBitmap.length;i++){
            mNumBitmap[i] = loadImageByResId(mNums[i]);
        }

    }

    /**
     * 根据Id加载图像
     * @param resId
     * @return
     */
    private Bitmap loadImageByResId(int resId){
        return BitmapFactory.decodeResource(getResources(),resId);
    }

    /**
     *更新尺寸，和初始化尺寸相关
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mGamePanelRect.set(0,0,w,h);
        // 初始化mBird
        mBird = new Bird(getContext(), mWidth, mHeight, mBirdBg);
        mFloor = new Floor(mWidth,mHeight,mFloorBg);
        // 初始化管道范围
        mPipeRect = new RectF(0, 0, mPipeWidth, mHeight);

//        Pipe pipe = new Pipe(getContext(), w, h, mTopBg, mBottomBg);
//        mPipes.add(pipe);

        //分数宽高
        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f
                        / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectF = new RectF(0,0,mSingleGradeWidth,mSingleGradeHeight);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启线程
        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while(isRunning){
            long start = System.currentTimeMillis();

            draw();//开始绘制
            logic();//处理游戏状态
            long end = System.currentTimeMillis();

            try {
                if(end - start < 50){
                    Thread.sleep(50 - (end - start));//
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理游戏逻辑事件
     */
    private void logic(){
        switch (gameStatus){
            case RUNNING:
                mGrade = 0;
                // 更新我们地板绘制的x坐标，地板移动
                mFloor.setX(mFloor.getX() - mSpeed);

                logicPipe();

                //默认下落，点击时瞬间上升
               // if(mPipes.size()>0){
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                    //AutoPlayGame();
                //}



                //计算分数，移除一个管道就的一分
                mGrade += mRemovedPipe;
                Log.d(TAG,"分数：" + mGrade);

                for(Pipe pipe:mPipes){//还有未移除的管道，在屏幕上显示的，且在鸟左边的
//                    Log.d(TAG,"Pipe的总数：" + count);
                    if(pipe.getX() + mPipeWidth < mBird.getX()){
                        mGrade++;
                        //Log.d(TAG,"分数：" + mGrade);
                    }
                }
                checkGameOver();

                break;
            case STOP: //鸟落下
                //撞到管道，让鸟慢慢落下
                if(mBird.getY() < mFloor.getY() - mBird.getmWidth()){
                    mTmpBirdDis +=mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                }else{
                    gameStatus = GameStatus.WAITTING;
                    initPos();
                }
                break;
            default:
                break;

        }
    }

    /**
     * 添加管道和移除管道
     */
    private void logicPipe() {
        //管道移动，并记录可以移除的管道
        for(Pipe pipe:mPipes){
            if(pipe.getX() < -mPipeWidth){
                //Log.d(TAG,"mRemovedPipe的总数：" + mRemovedPipe);
                mNeedRemovePipe.add(pipe);
                mRemovedPipe++;
                continue;
            }
            pipe.setX(pipe.getX() - mSpeed);
        }

        mPipes.removeAll(mNeedRemovePipe);


        //管道
        mTmpMoveDistance +=mSpeed;

        if(mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO){
            Pipe pipe = new Pipe(getContext(), getWidth(), getHeight(), mTopBg, mBottomBg);
            mPipes.add(pipe);
            mTmpMoveDistance = 0;
        }

    }

    /**
     * 重置鸟的位置信息
     */
    private void initPos(){
        mPipes.clear();
        mNeedRemovePipe.clear();
        //重置mBird的位置
        mBird.setY(mHeight * 2/3);
        //重置下来速度
        mTmpBirdDis = 0;
        mRemovedPipe = 0;
        mGrade = 0;
    }

    /**
     * 检测游戏是否结束
     */
    private void checkGameOver(){

        // 如果触碰地板
        if(mBird.getY() > mFloor.getY() - mBird.getmHeight()){
            gameStatus = GameStatus.STOP;
        }
        //如果鸟撞到管道
        for(Pipe wall:mPipes){
            //管道已经过了
            if(wall.getX() + mPipeWidth < mBird.getX()){
                continue;
            }
            if(wall.touchBird(mBird)){
                gameStatus = GameStatus.STOP;
                break;
            }


        }
    }




    /**
     * 在画布上绘制
     */
    private void draw(){

        try {
            mCanvas = mHolder.lockCanvas();//获得画布
            if(mCanvas != null){
                //drawSomething
                drawBg();//绘制背景
                mBird.drawBird(mCanvas);//绘制鸟
                mFloor.drawFloor(mCanvas,mPaint);//绘制地板
                //更新Floor的x坐标
                mFloor.setX(mFloor.getX() - mSpeed);
                drawPipe();//绘制管道
                drawGrade(); //绘制成绩
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }


    }

    /**
     * 绘制成绩
     */
    private void drawGrade() {
        String grade = mGrade + "";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth / 2 - grade.length() * mSingleGradeWidth / 2,1 / 8f *mHeight);
        //一个字符一个字符进行绘制
        for(int i = 0;i<grade.length();i++){
            String numStr = grade.substring(i,i+1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num],null,mSingleNumRectF,null);
            mCanvas.translate(mSingleGradeWidth,0);
        }
        mCanvas.restore();
    }

    /**
     * 绘制管道
     */
    private void drawPipe() {

        for(Pipe pipe:mPipes){
            pipe.setX(pipe.getX() - mSpeed);
            pipe.drawPipe(mCanvas,mPipeRect);
        }
    }

    /**
     * 实现自动玩游戏功能
     */
    private void AutoPlayGame(){

        //根据管道的位置改变鸟的位置
        //1、先获得上管道的位置
       if(mGrade<100){
           mTmpBirdDis = mBirdUpDis;
           for(Pipe pipe:mPipes){
              // float PipeTop_Bottom = mPipeRect.bottom;//绘制Pipe的底部位置
               int PipeHeight = pipe.getHeight();
               Log.d(TAG,"pipe的底部位置：" + PipeHeight);
               int margin = pipe.getMargin();          //上下管道之间的距离
               Log.d(TAG,"鸟的位置：" + mBird.getY());
               if(mBird.getY() + mBird.getmHeight() >= (PipeHeight + margin)){//鸟的位置在下管道的下方
                   Log.d(TAG,"鸟上升");
                   mTmpBirdDis += mAutoDownSpeed;
                   mBird.setY(mBird.getY() + mTmpBirdDis);
               }
               if(mBird.getY() - mBird.getmHeight() <= PipeHeight){     //鸟的位置在上管道的上方
                   Log.d(TAG,"鸟下降" + "pipe的底部位置：" + PipeHeight + ",鸟的位置：" + mBird.getY());
                   mTmpBirdDis += mAutoDownSpeed;
                   mBird.setY(mBird.getY() - mTmpBirdDis);

               }
           }
       }

    }

    /**
     *模拟点击事件
     *
     */
    private  void stimulateTouchEvent(View view ,int x,int y){

        while(true){
            long startTime = SystemClock.uptimeMillis();
            final MotionEvent downEvent = MotionEvent.obtain(startTime,startTime,MotionEvent.ACTION_DOWN,x,y,0);
            startTime+=6000;
            final MotionEvent upEvent = MotionEvent.obtain(startTime, startTime,
                    MotionEvent.ACTION_UP, x, y, 0);
            view.onTouchEvent(downEvent);
            view.onTouchEvent(upEvent);
            downEvent.recycle();
            upEvent.recycle();
        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){//点击屏幕

            switch (gameStatus){
                case WAITTING:
                    gameStatus = GameStatus.RUNNING;
                    //stimulateTouchEvent(this,getWidth()/2,getHeight() / 2);
                    break;
                case RUNNING:
                    mTmpBirdDis = mBirdUpDis;//当点击开始游戏时，鸟的上升

                    break;
            }
        }
        return true;
    }



    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawBitmap(mBg, null, mGamePanelRect, null);
    }



}
