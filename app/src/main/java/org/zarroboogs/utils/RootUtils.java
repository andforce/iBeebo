package org.zarroboogs.utils;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by andforce on 15-4-25.
 */
public class RootUtils {

    private static final String TAG = "RootUtils";




    public static boolean haveRoot() {

        boolean mHaveRoot = false;

        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                Log.i(TAG, "have root!");
                mHaveRoot = true;
            } else {
                Log.i(TAG, "not root!");
            }
        } else {
            Log.i(TAG, "mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        Process p = null;

        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                    dos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (p != null) {
                p.destroy();
                p = null;
            }
        }
        return result;
    }

    // 执行命令并且输出结果
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            // dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                // Log.d("result", line);
                if (result == "") {
                    result += line;
                } else {
                    result += "\n" + line;
                }
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                    dos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                    dis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (p != null) {
                p.destroy();
                p = null;
            }
        }
        return result;
    }


    public static boolean canRunRootCommands() {
        boolean retval = false;
        Process suProcess;

        try {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

            if (null != os && null != osRes) {
                // Getting the id of the current user to check if this is root
                os.writeBytes("id\n");
                os.flush();

                String currUid = osRes.readLine();
                boolean exitSu = false;
                if (null == currUid) {
                    retval = false;
                    exitSu = false;
                    Log.d("ROOT", "Can't get root access or denied by user");
                } else if (true == currUid.contains("uid=0")) {
                    retval = true;
                    exitSu = true;
                    Log.d("ROOT", "Root access granted");
                } else {
                    retval = false;
                    exitSu = true;
                    Log.d("ROOT", "Root access rejected: " + currUid);
                }

                if (exitSu) {
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        } catch (Exception e) {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os) after su
            // failed, meaning that the device is not rooted

            retval = false;
            Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        Log.d("ROOT", "Root access granted Has Rooted: " + retval);
        return retval;
    }

}
