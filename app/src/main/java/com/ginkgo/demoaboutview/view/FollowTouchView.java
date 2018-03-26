package com.ginkgo.demoaboutview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.nineoldandroids.view.ViewHelper;


/**
 * 跟着手指滑动的view
 * Created by Administrator on 2018/3/26 0026.
 */

public class FollowTouchView extends ImageView {

    private int mLastX = 0;
    private int mLastY = 0;

    public FollowTouchView(Context context) {
        super(context);
    }

    public FollowTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FollowTouchView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public boolean onTouchEvent(MotionEvent event){
        int x = (int) event.getRawX();//注意不能使用getX方法，因为这里实现的是全屏滑动
        int y = (int) event.getRawY();
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:{
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                int translationX = (int) ViewHelper.getTranslationX(this) + deltaX;
                int translationY = (int) ViewHelper.getTranslationY(this) + deltaY;
                ViewHelper.setTranslationX(this, translationX);
                ViewHelper.setTranslationY(this, translationY);
                break;
            }
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }
}
