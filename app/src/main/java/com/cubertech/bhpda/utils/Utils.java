package com.cubertech.bhpda.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by LiQi on 2017/9/19.
 */

public class Utils {
    private static int ascNum;
    private static char strChar;
    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }


    /**
     * 字符转ASC
     *
     * @param st
     * @return
     */
    public static int getAsc(String st) {
        byte[] gc = st.getBytes();
        ascNum = (int) gc[0];
        return ascNum;
    }

    /**
     * ASC转字符
     *
     * @param backnum
     * @return
     */
    public static char backchar(int backnum) {
        strChar = (char) backnum;
        return strChar;
    }


    public static void safeToast(final Activity context, final String str){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 获取版本值
     * @param context
     * @return
     */
    public static String getVersions(Context context) {

        String versionCode = "-1";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            /*
             * versionCode = context.getPackageManager().getPackageInfo(
             * "com.gaoxiaotongctone", 0).versionCode;
             */
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //Log.i("获取版本号报错", "" + e.toString());
            //e.printStackTrace();
            versionCode = "1.0";//此处也修改成版本号，防止获取错误
        }
        return versionCode;
    }
}
