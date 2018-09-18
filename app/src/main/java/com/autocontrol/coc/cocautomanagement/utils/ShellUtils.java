package com.autocontrol.coc.cocautomanagement.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public final class ShellUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");

    private ShellUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static CommandResult execCmd(String var0, boolean var1) {
        Log.d("命令===>>", var0);
        return execCmd(new String[]{var0}, var1, true);
    }

    public static CommandResult execCmd(List<String> var0, boolean var1) {
        return execCmd(var0 == null ? null : (String[]) var0.toArray(new String[0]), var1, true);
    }

    public static CommandResult execCmd(String[] var0, boolean var1) {
        return execCmd(var0, var1, true);
    }

    public static CommandResult execCmd(String var0, boolean var1, boolean var2) {
        return execCmd(new String[]{var0}, var1, var2);
    }

    public static CommandResult execCmd(List<String> var0, boolean var1, boolean var2) {
        return execCmd(var0 == null ? null : (String[]) var0.toArray(new String[0]), var1, var2);
    }

    public static CommandResult execCmd(String[] var0, boolean var1, boolean var2) {
        int var3 = -1;
        if (var0 != null && var0.length != 0) {
            Process var4 = null;
            BufferedReader var5 = null;
            BufferedReader var6 = null;
            StringBuilder var7 = null;
            StringBuilder var8 = null;
            DataOutputStream var9 = null;

            try {
                var4 = Runtime.getRuntime().exec(var1 ? "su" : "sh");
                var9 = new DataOutputStream(var4.getOutputStream());
                String[] var10 = var0;
                int var11 = var0.length;

                for (int var12 = 0; var12 < var11; ++var12) {
                    String var13 = var10[var12];
                    if (var13 != null) {
                        var9.write(var13.getBytes());
                        var9.writeBytes(LINE_SEP);
                        var9.flush();
                    }
                }

                var9.writeBytes("exit" + LINE_SEP);
                var9.flush();
                var3 = var4.waitFor();
                if (var2) {
                    var7 = new StringBuilder();
                    var8 = new StringBuilder();
                    var5 = new BufferedReader(new InputStreamReader(var4.getInputStream(), "UTF-8"));
                    var6 = new BufferedReader(new InputStreamReader(var4.getErrorStream(), "UTF-8"));
                    String var32;
                    if ((var32 = var5.readLine()) != null) {
                        var7.append(var32);

                        while ((var32 = var5.readLine()) != null) {
                            var7.append(LINE_SEP).append(var32);
                        }
                    }

                    if ((var32 = var6.readLine()) != null) {
                        var8.append(var32);

                        while ((var32 = var6.readLine()) != null) {
                            var8.append(LINE_SEP).append(var32);
                        }
                    }
                }
            } catch (Exception var30) {
                var30.printStackTrace();
            } finally {
                try {
                    if (var9 != null) {
                        var9.close();
                    }
                } catch (IOException var29) {
                    var29.printStackTrace();
                }

                try {
                    if (var5 != null) {
                        var5.close();
                    }
                } catch (IOException var28) {
                    var28.printStackTrace();
                }

                try {
                    if (var6 != null) {
                        var6.close();
                    }
                } catch (IOException var27) {
                    var27.printStackTrace();
                }

                if (var4 != null) {
                    var4.destroy();
                }

            }

            return new CommandResult(var3, var7 == null ? null : var7.toString(), var8 == null ? null : var8.toString());
        } else {
            return new CommandResult(var3, (String) null, (String) null);
        }
    }

    public static class CommandResult {
        public int result;
        public String successMsg;
        public String errorMsg;

        public CommandResult(int var1, String var2, String var3) {
            this.result = var1;
            this.successMsg = var2;
            this.errorMsg = var3;
        }
    }
}
