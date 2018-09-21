package com.autocontrol.coc.cocautomanagement;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.SizeUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class LiveWindowManager {
    /**
     * 小悬浮窗View的实例
     */
    private static LiveWindowView smallWindow;

    /**
     * 小悬浮窗View的参数
     */
    public static LayoutParams smallWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;

    /**
     * 用于获取手机可用内存
     */
    private static ActivityManager mActivityManager;
    private static WindowManager windowManager;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static boolean createSmallWindow(Context context) {
        windowManager = getWindowManager(context);
        if (Build.VERSION.SDK_INT >= 23) {//6.0 获取权限可显示
            if (!Settings.canDrawOverlays(context)) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                //执行6.0以上绘制代码
                if (smallWindow == null) {
                    smallWindow = new LiveWindowView(context);
                    setWindowParams(context);
                    windowManager.addView(smallWindow, smallWindowParams);
                }
            }
            return true;
        } else {
            if (getAppOps(context)) {
                if (smallWindow == null) {
                    smallWindow = new LiveWindowView(context);
                    setWindowParams(context);
                    windowManager.addView(smallWindow, smallWindowParams);
                }
                return true;
            } else {
                Toast.makeText(context, "请在 安全中心/设置 中打开悬浮窗权限后再使用小窗口观看直播~", Toast.LENGTH_SHORT);
                return false;
            }
        }
    }

    /**
     * 判断 悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean getAppOps(Context context) {
        try {
            @SuppressLint("WrongConstant") Object object = context.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }

    public static void setWindowParams(Context context) {
        if (smallWindowParams == null) {
            smallWindowParams = new LayoutParams();
        }
        if (windowManager == null) {
            windowManager = getWindowManager(context);
        }
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        smallWindowParams.type = LayoutParams.TYPE_PHONE;
        smallWindowParams.format = PixelFormat.RGBA_8888;
        smallWindowParams.softInputMode = LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        smallWindowParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//        dragFloatingActionButton.setSize(FloatingActionButton.SIZE_MINI);
        //小窗口摆放的位置，手机屏幕中央
        smallWindowParams.x = screenWidth - LiveWindowView.viewWidth;
        smallWindowParams.y = screenHeight - LiveWindowView.viewHeight;
        smallWindowParams.width = LiveWindowView.viewWidth;
        smallWindowParams.height = LiveWindowView.viewHeight;

        smallWindow.setParams(smallWindowParams);    //  TWSF0355.2DW1
    }


    /**
     * 将小悬浮窗从屏幕上移除。/1.停止view播放操作    2.移除窗口操作
     *
     * @param context 必须为全局上下文
     */
    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
            smallWindowParams = null;
        }
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
     *
     * @param context 可传入应用程序上下文。
     * @return ActivityManager的实例，用于获取手机可用内存。
     */
    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    /**
     * 计算已使用内存的百分比，并返回。
     *
     * @param context 可传入应用程序上下文。
     * @return 已使用内存的百分比，以字符串形式返回。
     */
    public static String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "悬浮窗";
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @param context 可传入应用程序上下文。
     * @return 当前可用内存。
     */
    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

}
