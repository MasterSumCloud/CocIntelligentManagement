package com.autocontrol.coc.cocautomanagement.utils;

/**
 * Created by mac_py on 18/09/2018.
 */

public class ADBUtil {
    public static void clickPosition(int x,int y,int duration){
        ShellUtils.execCmd("input tap " + x + " " + y, true);
    }

    public static void scrollPosition(int x,int y,int ex,int ey,int duration){

    }

    public static void startApp(String packgeName){

    }

    public static void stopApp(String packgeName){

    }
}
