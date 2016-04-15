package com.game.flabbybird.MyUtil;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by lhqj on 2016/4/13.
 */
public class MyUtil {

    /**
     * dp2px
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp)
    {
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                        .getDisplayMetrics()));
        return px;
    }
}
