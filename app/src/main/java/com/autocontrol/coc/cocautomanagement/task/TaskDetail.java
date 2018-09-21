package com.autocontrol.coc.cocautomanagement.task;

import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.autocontrol.coc.cocautomanagement.utils.Constant;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ShellUtils;

/**
 * Created by mac_py on 21/09/2018.
 */

public class TaskDetail extends TaskBase{
    private int taskType = -1;
    private int widthCoe;
    private int heightCoe;
    private String plantForm;
    private final DisplayMetrics dm;

    public TaskDetail(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度（像素）
        widthCoe = dm.widthPixels / 720;
        // 屏幕高度（像素）
        heightCoe = dm.heightPixels / 1280;
        plantForm = SPUtils.getInstance().getString(Constant.plantform, null);
    }

    public void startTask(int type) {
        switch (type) {
            case 1:
                ExeStartClanWar();
                break;
        }
    }

    private void ExeStartClanWar() {
        //确定的XY坐标 720p
        int x_cimfirm = 640;
        int y_cimfirm = 645;
        //部落站按钮 720P
        int x_clanwar = 53;
        int y_clanwar = 441;
        //开始搜索按钮
        int x_startclanwar = 650;
        int y_startclanwar = 520;

        int x_startclanwarSearch = 1020;
        int y_startclanwarSearch = 650;

        //挂机点开菜单
        int x_guajiOpenMenu = 1275;
        int y_guajiOpenMenu = 253;

        //挂机停止按钮
        int x_StopLeiren = 925;
        int y_StopLeiren = 253;
        //历史战争确认
        int x_endlastwar = 920;
        int y_endlastwar = 620;
        //回营按钮
        int x_gobackhome = 82;
        int y_gobackhome = 636;


        Log.d("执行开战任务", "获取屏幕像素宽==" + dm.widthPixels);
        Log.d("执行开战任务", "获取屏幕像素高==" + dm.heightPixels);


        x_cimfirm = x_cimfirm * widthCoe;
        y_cimfirm = y_cimfirm * heightCoe;

        x_clanwar = x_clanwar * widthCoe;
        y_clanwar = y_clanwar * heightCoe;

        x_startclanwar = x_startclanwar * widthCoe;
        y_startclanwar = y_startclanwar * heightCoe;

        x_startclanwarSearch = x_startclanwarSearch * widthCoe;
        y_startclanwarSearch = y_startclanwarSearch * heightCoe;

        x_guajiOpenMenu = x_guajiOpenMenu * widthCoe;
        y_guajiOpenMenu = y_guajiOpenMenu * heightCoe;

        x_StopLeiren = x_StopLeiren * widthCoe;
        y_StopLeiren = y_StopLeiren * heightCoe;

        x_endlastwar = x_endlastwar * widthCoe;
        y_endlastwar = y_endlastwar * heightCoe;


        Log.d("执行开战任务", "杀指定游戏进程");
        ShellUtils.execCmd("am force-stop" + plantForm, true);
        SystemClock.sleep(3000);

        Log.d("执行开战任务", "进入指定COC平台");
        if (plantForm.contains("九游")) {
            ShellUtils.execCmd("am start -n " + plantForm + "/.GameApp", true);
        } else {
            ShellUtils.execCmd("am start -n " + plantForm + "/com.supercell.clashofclans.GameAppKunlun", true);
        }
        SystemClock.sleep(30000);

        Log.d("执行开战任务", "点击可能存在的确定按钮");
        ShellUtils.execCmd("input tap " + x_cimfirm + " " + y_cimfirm, true);
        SystemClock.sleep(2000);

        Log.d("执行开战任务", "点击部落站按钮");
        ShellUtils.execCmd("input tap " + x_clanwar + " " + y_clanwar, true);
        SystemClock.sleep(2000);

        Log.d("执行开战任务", "点击确认上次部落站");
        ShellUtils.execCmd("input tap " + x_endlastwar + " " + y_endlastwar, true);
        SystemClock.sleep(2000);

        Log.d("执行开战任务", "点击开始部落站");
        ShellUtils.execCmd("input tap " + x_startclanwar + " " + y_startclanwar, true);
        SystemClock.sleep(2000);

        Log.d("执行开战任务", "点击确认开始部落站");
        ShellUtils.execCmd("input tap " + x_startclanwarSearch + " " + y_startclanwarSearch, true);
        SystemClock.sleep(2000);

        Log.d("执行开战任务", "第七步关闭应用");
        ShellUtils.execCmd("am force-stop " + plantForm, true);
        SystemClock.sleep(3000);

    }


}
