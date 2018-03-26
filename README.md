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

## 3 View的弹性滑动
### 1 使用Scroller
- 1 Scroller的典型使用方法
    
        Scroller mScroller = new Scroller(context);
        //缓慢移动到指定的位置
        //仅仅调用这个方法是无法让View滑动的，还需要借助computeScroll中的invalidate方法
        private void smoothScrollTo(int destX, int destY){
            int deltaX = destX - scrollX;
            //1000ms内滑向destX,效果就是慢慢滑动
            //scrollX\scrollY:起点， deltaX\deltaY:滑动的距离
            mScroller.startScroll(scrollX, 0, deltaX, 0, 1000);
            invalidate();
        }
   
        //compteScroll方法在View中是一个空方法，因此需要我们自己去实现    
        @Override
        ppublic void computeScroll(){
            if(mScroller.computeScrollOffset()){
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                postInvalidate();
            }
        }
- 2 invalidate()方法会导致View重绘，在View的draw方法中又会去调用computeScroll()方法
- 3 在computeScroll()方法中会向Scroller对象中获取当前的scrollX和scrollY,然后通过scrollTo()函数实现滑动
- 4 mScroller.computeScrollOffset()返回true表示需要继续滑动，返回true表示滑动结束
- 总结：Scroller本身并不能实现View的滑动，它需要配合View的computeScroll方法才能完成弹性滑动的效果，它不断让View重绘，而每次重绘滑动起始时间会有一个时间间隔，通过这个间隔Scroller就可以得出View当前的滑动距离，知道了滑动距离就可以通过scrollTo()方法完成View的滑动
       
## 4 View的事件分发机制
### 1 View事件传递规则
- 1 所谓点击事件的分发，其实就是对MotionEvent事件的分发过程。即当一个MotionEvent产生了以后，系统需要把这个事件传递给一个具体的View。事件分发过程由三个很重要的方法来共同完成
    - 1 dispatchTouchEvent()
        -  用来进行事件的分发。如果事件能够传递给当前的View，那么该View的此方法一定会被调用，返回结果受当前View的onTouchEvent和下级View的dispatchTouchEvent()的影响，表示是否消耗当前事件
    - 2 onInterceptTouchEvent()
        - 在dispatchTouchEvent()方法内部调用，用来判断是否拦截某个事件，如果当前View拦截了这个事件，那么在同一个事件序列中，此方法不会被再次调用，返回结果表示是否拦截当前事件。
    - 3 onTouchEvent()    
        - 在dispatchTouchEvent()方法中调用，用力处理点击事件，返回结果表示是否消耗当前事件，如果不消耗，则在同一个时间序列中，当前View无法再次接收到事件
- 2 调用过程    
             
         //这三个函数调用的伪代码
         public boolean dispatchTouchEvent(MotionEvent ev){
             boolean consume = false;
             if(onIntercepTouchEvent(ev)){
                consume = onTouchEvent(ev);
             }else {
                consume = child.dispatchTouchEvent(ev);
             }      
         }
        
     - 1 对于一个ViewGroup,点击事件产生后，会调用dispatchTouchEvent()
     - 2 如果这个ViewGroup的onInterceptionTouchEvent()方法返回true,表示要拦截当前事件，接着事件就会交给ViewGroup的onTouchEvent()处理
     - 3 如果这个ViewGroup的onInterceptionTouchEvent()方法返回false,表示ViewGroup不拦截当前事件，这时候会继续传递给它的子元素，接着子元素的dispatchTouchEvent()方法会被调用
- 3 优先级
    - 给View设置了onTouchListener，其优先级比onTouchEvent()方法要高，onTouchListener (onTouch方法)返回true, onTouchEvent()方法将不会被调用
    - 在onTouchEvent方法中，如果当前设置由onClickListener，那么它的onClick方法会被调用，onClickListener的优先级最低
- 4 传递过程
    - 一个点击事件产生后，传递过程如下： Activity -> Window -> View
    - 如果一个View的onTouchEvent返回false，那么它芙蓉泣的onTouchEvent()将会被调用
    - 如果所有的元素都不处理这个事件，那么这个事件最终将会被传递给Activity，即Activity的onTouchEvent方法会被调用     
- 5 总结
    - 某个View一旦决定拦截，那么这个事件序列只能由它来处理，并且它的onInterceptTouchEvent()方法不会仔被调用（设置了标志位）
    - ViewGroup默认不拦截任何事件，Android中的ViewGroup的onInterceptTouchEvent方法默认返回false        
    - View没有onInterceptTouchEvent方法，一旦有事件传递给它，那么它的onTouchEvent方法就会被调用
    - View的enable属性并不影响onTouchEvent的默认返回值，哪怕一个View是disable状态的，只要它的clickable或者longClickable有一个为true，那么它的onTouchEvent就返回true
    - 可以通过requestDisallowInterceptTouchEvent方法可以在子元素中干预父元素的事件分发过程，但是ACTION_DOWN事件除外