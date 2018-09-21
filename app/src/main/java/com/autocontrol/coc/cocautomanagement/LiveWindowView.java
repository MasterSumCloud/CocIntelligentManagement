package com.autocontrol.coc.cocautomanagement;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.autocontrol.coc.cocautomanagement.utils.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;

import java.lang.reflect.Field;

import static com.autocontrol.coc.cocautomanagement.R.mipmap.icon_stop;

public class LiveWindowView extends LinearLayout {

    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private final int screenWidth;
    private final int screenWidthHalf;
    private final int screenHeight;
    private final int statusHeight;
    private final int virtualHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    private float yInView;
    Context context;
    private boolean change = true;
    private final View inflate;
    private boolean isDrag;
    private int lastX;
    private int lastY;
    private final FloatingActionButton faoatBtn;

    private boolean openOrCclose;

    public LiveWindowView(final Context context) {
        super(context);
        this.context = context.getApplicationContext();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        inflate = LayoutInflater.from(context).inflate(R.layout.window_pullflow, this);
        faoatBtn = inflate.findViewById(R.id.fab);
        faoatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (openOrCclose) {
                    faoatBtn.setImageResource(R.mipmap.icon_start);
                    faoatBtn.setBackgroundTintList(getColorStateListTest(R.color.colorGreen));
                    openOrCclose = false;
                    startPullActivity();
                } else {
                    faoatBtn.setImageResource(R.mipmap.icon_stop);
                    faoatBtn.setBackgroundTintList(getColorStateListTest(R.color.colorAccent));
                    openOrCclose = true;
                    MainActivity act = (MainActivity) context;
                    act.moveTaskToBack(true);
                }
            }
        });
        viewWidth = SizeUtils.dp2px(50);
        viewHeight = SizeUtils.dp2px(50);
        //        inflate.setFocusableInTouchMode(true);
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        screenWidthHalf = screenWidth / 2;
        screenHeight = ScreenUtils.getScreenHeight(getContext());
        statusHeight = ScreenUtils.getStatusHeight(getContext());
        virtualHeight = ScreenUtils.getVirtualBarHeigh(getContext());

    }


    private ColorStateList getColorStateListTest(int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };

        int color = ContextCompat.getColor(context, colorRes);

        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }

    /**
     * 打开直播页
     */
    private void startPullActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                Log.e("down---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag = true;
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些手机无法触发点击事件的问题
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                Log.e("distance---->", distance + "");
                if (distance < 3) {//给个容错范围，不然有部分手机还是无法点击
                    isDrag = false;
                    break;
                }

                float x = getX() + dx;
                float y = getY() + dy;

                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > screenWidth - getWidth() ? screenWidth - getWidth() : x;
                if (y < 0) {
                    y = 0;
                }
                if (y > screenHeight - statusHeight - getHeight()) {
                    y = screenHeight - statusHeight - getHeight();
                }
                updateViewPosition((int) rawX, (int) rawY);
                lastX = rawX;
                lastY = rawY;
                Log.e("move---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf + " " + isDrag + "  statusHeight=" + statusHeight + " virtualHeight" + virtualHeight + " screenHeight" + screenHeight + "  getHeight=" + getHeight() + " y" + y);
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //恢复按压效果
                    faoatBtn.setPressed(false);
                    Log.e("ACTION_UP---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                    endAnmi(rawX >= screenWidthHalf);
                }
                Log.e("up---->", isDrag + "");
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);

    }

    public void updateViewPosition(int x, int y) {
        mParams.x = screenWidth - x - SizeUtils.dp2px(25);
        mParams.y = screenHeight - y - SizeUtils.dp2px(25);
        windowManager.updateViewLayout(this, mParams);
    }


    private void endAnmi(boolean left) {
        if (!left) {
            while (mParams.x < screenWidth) {
                if (mParams.x + 3 <= screenWidth) {
                    mParams.x += 3;
                } else {
                    mParams.x = screenWidth;
                }
                windowManager.updateViewLayout(this, mParams);
            }
        } else {
            while (mParams.x > 0) {
                if (mParams.x - 3 > 0) {
                    mParams.x -= 3;
                } else {
                    mParams.x = 0;
                }
                windowManager.updateViewLayout(this, mParams);
            }
        }
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    public void upXYPosition() {
        windowManager.updateViewLayout(this, mParams);
    }


}
