# View的事件体系
## 1 View基础知识
- 1 View是Android中所有控件的基类，不管是简单的Button、TextView还是复杂的LinearLayout、RelativeLayout、ListView，他们的共同基类都是View。View可以单独的控件，也可以是多个控件组成的一个控件，这种关系就形成了View的树结构。
- 2 View的位置主要由它的四个丁点来决定
    - top、left、right、bottom
    - 这些坐标都是相对于View的父容器来说的，因此它是一种相对坐标。
    - View的宽高与坐标的关系： width = right-left
    - 如何得到View的位置参数？ mLeft = getLeft()
- 3 在Android3.0之后，View增加了x,y,translationX, translationY
    - x,y是View的左上角坐标
    - translationX、translationY是View左上角相对于容器的偏移量，一般为0
    - x = left + translationX
- 4 MotionEvent 
    在手指接触屏幕所产生的一一系列事件中，典型的事件类型由：
    - ACTION_DOWN:手指刚接触屏幕
    - ACTION_MOVE: 手指在屏幕上移动
    - ACTION_UP: 手指在屏幕上松开的一瞬间
   
   可以通过MotionEvent对象得到点击事件发生的x和y坐标，系统提供了两组方法
   - getX、getY : 返回相对于当前View左上角的x、y坐标
   - getRawX、getRawY： 返回相对于手机屏幕左上角的x和y坐标
- 5 TouchSlop: TouchSlop是系统所能识别出来的被认为是滑动的最小距离，也就是说如果手指滑动的距离小于这个常量，那么系统就不认为在进行滑动操作。
   - 这是一个常量，和设备相关，在不同的设备中，值可能是不相同的
   - 怎么获取 
   
           ViewConfiguration.get(getContext)).getScaledTouchSlop()

- 6 VelocityTracker: 速度追踪，用来追踪手指在滑动过程中的速度，包括水平方向和垂直方向
       
        //1 在View的onTouch方法中追踪当前的单击事件的速度
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(envet);
        //2 当需要知道滑动速度时
        VelocityTracker.computeCurrentVelocity(1000);//计算速度
        int xVelocity = (int)velocityTracker.getXVelocity();
        int yVelocity = (int)velocityTracker.getYVelocity();       
        //3 在不需要使用的时候，使用clear方法重置并回收
        velocityTracker.clear();
        velocityTracker.recycle();
    
   - computeCurrentVelocity(int)的参数为时间间隔
   - 从右往左滑，水平方向速度为负值

- 7 GestureDetector 手势检测，用来复制检测用户的单击、滑动、长按、双击等行为
- 8 Scroller 用于实现View的弹性滑动
   - 当使用View的scrollTo、scrollBy方法进行滑动时，其过程是瞬间完成的没有过渡效果
   - Scroller可以用来实现由过渡效果的滑动，其过程不是瞬间完成的。
   - Scroller本身无法让View弹性滑动，它需要和View的computeScroll方法配合使用才能完成弹性滑动
        
           Scroller scroller = new Scroller(mContext);
           //缓慢滑动到指定位置
           private void smoothScrollTo(int destX, int destY){
                int scollX = getScrollX();
                int delta = destX - scrollX;
                //1000ms内滑动到destX
                mScroller.startScroll(scrollx, 0, delta, 0, 1000);
                invalidate();
           }
           
           @Override
           public void computeScroll(){
                if(mScroller.computeScrollOffSet()){
                    scrollTo(mScroller.getCurrx(), mScroller.getCurrY());
                    postInvalidate();
                }
           }
           
   - invalidate与postInvalidate()的区别
        - invalidate: 滑动重绘View树，并且从头到尾不会触发onMeasure()方法，只能在UI线程执行，重绘效率高
        - postalidate: postInvalidate函数既可以在UI线程执行，也可以在非UI线程执行。但是重绘效率低
        
## 2 View的滑动        
- 1 通过三种方法可以实现View的滑动
    - 1 通过View本身提供的scrollTo/scrollBy方法
    - 2 通过动画给View施加平移效果
    - 3 通过改变View的LayoutParams使得View重新布局从而实现滑动
- 2 scrollTo、scrollBy
     - scrollTo()方法实现了基于传递参数的绝对滑动
     - scrollBy()方法是调用scrollTo()方法实现的，实现了基于位置的相对滑动
     - mScrollX 、mScrollY
        - mScrollX的值总是等于View的View上边缘和View的内容上边缘在水平方向上的距离
        - mScrollY的值总是等于View的View左边缘和View的内容左边缘在垂直方向上的距离
        - View边缘是指View的位置，由四个顶点组成，而内容的边缘是指View中内容的边缘
        - scrollTo、scrollBy方法只能改变View的内容的位置而不能改变View的位置
- 3 使用动画        
     - 使用动画来移动View，主要操作View的translationX和translationY属性
     - 既可以采用传统的View动画，也可以采用属性动画，如果采用属性动画的话，为了兼容3.0以下的版本，需要采用开源动画库
     - View动画是对View的影像做操作，并不能真正改变View的位置参数，如果希望保留动画后的状态，需要将fillAfter属性设置为true（点击事件会出问题，所以View有操作时，不适用于这种方法）
 - 4 改变布局参数
     - 改变布局参数，即改变LayoutParams
           
            MarginLayoutParams params = (MarginLayoutParams)mButton.getLayoutParams();
            params.width += 100;
            params.leftMargin += 100;
            mButton.requestLayout();
            //或者 mButton.setLayoutParams(params);
   
   