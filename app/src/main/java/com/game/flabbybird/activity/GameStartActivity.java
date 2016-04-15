package com.game.flabbybird.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.game.flabbybird.View.GameFlabbyBird;

/**
 * Created by lhqj on 2016/4/13.
 */
public class GameStartActivity extends Activity {

    private GameFlabbyBird mGameFlabbyBird;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGameFlabbyBird = new GameFlabbyBird(this);
        setContentView(mGameFlabbyBird);
    }
}
