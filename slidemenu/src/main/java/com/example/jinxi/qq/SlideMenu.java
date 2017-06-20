package com.example.jinxi.qq;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by jinxi on 2017/5/22.
 */

public class SlideMenu extends FrameLayout {
    int maxleft;
    View menu, main;
    ViewDragHelper dragHelper;
    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == main || child == menu; //1是主界面，0是菜单界面
        }

        /**
         * 当View位置改变的时候调用
         * @param changedView   当前位置改变了的View
         * @param left  当前view最新的left
         * @param top   当前view最新的top
         * @param dx    水平移动的距离
         * @param dy    垂直移动的距离
         */

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menu) {
                menu.layout(0, 0, menu.getMeasuredWidth(), menu.getBottom());

                int newLeft = main.getLeft() + dx;
                newLeft = fixLeft(newLeft);
                main.layout(newLeft, main.getTop(), newLeft + main.getMeasuredWidth(), main.getBottom());
            }

            //由于要执行一系列动画，我们先算出手指拖动的百分比
            float fraction=main.getLeft()*1f/maxleft;
            //根据百分比执行动画
            execAnim(fraction);
        }

        /**
         * 当View松开或则释放的时候执行
         * @param releasedChild     在那个View上松开
         * @param xvel  x方向的滑动速度
         * @param yvel  y方向滑动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (main.getLeft()>maxleft/2){
                //打开
//                scroller.startScroll();
//                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);

                dragHelper.smoothSlideViewTo(main,maxleft,0);
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            }else {
                //关闭
                dragHelper.smoothSlideViewTo(main,0,0);
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            }
        }

        Scroller scroller=null;


        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }


        /**
         * 控制和修正View水平方向的位置
         * @param child     当前触摸的子View
         * @param left     表示ViewDragHelper帮我们计算好的最新的left值, left=child.getLeft()+dx
         * @param dx    水平方向移动距离
         * @return 返回的值表示我们想让view的left变成的值，也就是最终返回的值才决定了child的left
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == main) {
                left = fixLeft(left);
            }

            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }
    };

    /**
     * 根据拖动百分比执行动画
     * @param fraction
     */
    private void execAnim(float fraction) {
        //fraction: 0 -> 1
        //main缩放：1 -> 0.8
        main.setScaleX(floatEval.evaluate(fraction, 1f,0.8f));
        main.setScaleY(floatEval.evaluate(fraction, 1f,0.8f));
    }

    private float evaluateValue(float start, float end, float fraction){
        return start + (end-start)*fraction;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //判断动画有没有执行完
        //ViewDragHelper的写法
        if(dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    //限制Left
    private int fixLeft(int newLeft) {
        if (newLeft>maxleft){
            newLeft=maxleft;
        }else if (newLeft<0){
            newLeft=0;
        }
        return newLeft;
    }

    public SlideMenu(@NonNull Context context) {
        this(context, null);
    }

    public SlideMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    FloatEvaluator floatEval = new FloatEvaluator();//浮点计算器
    public SlideMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 当完成xml布局填充的时候调用，所以该方法执行的时候，当前View已经知道自己有几个字View了
     * 注意：该方法调用的时候，还没有进行测量和布局呢，所以不能再该方法中获取view的宽高
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menu = getChildAt(0);
        main = getChildAt(1);

    }

    /**
     * 当onMeasuere执行完毕之后就会执行这个方法，所以可以在这个方法中获取任意View的宽高了
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxleft = (int) (getMeasuredWidth() * 0.6f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让ViewDragHelper判断是否拦截event
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将event将给dragHelper来解析处理。
        dragHelper.processTouchEvent(event);


        return true;
    }
}
