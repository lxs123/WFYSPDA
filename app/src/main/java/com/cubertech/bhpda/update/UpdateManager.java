package com.cubertech.bhpda.update;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 更新 工具类
 * 注: 使用时需要实例化对象(getInstance),并初始化(init)
 * 下载路径是SD卡下 DownLoadPDA
 * APK 的文件名  与项目的 app_name相同
 * <p>
 * Created by Administrator on 2018/7/23.
 */

public class UpdateManager {
    private String mApkPath;
    private String mDownloadTitle = "下载新版本中...";   // 下载标题
    public static long mApkId = -1;
    public static boolean isUpdating = false;  // 是否更新中
    private static UpdateManager mInstance;
    private Context mContext;
    /*下载进度*/
    private static final int DOWN_UPDATE = 1;
    /*下载完成*/
    private static final int DOWN_OVER = 2;
    private String apkUrl;//APK下载地址
    private boolean interceptFlag = false;

    /*用于更新下载的进度*/
    private int progress;

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressDialog progressDialog;
    private UpdateManager.UpdateReceiver receiver;
    private int PERMISSIONS_STORAGE = 0;
    private int REQUEST_EXTERNAL_STORAGE = 1;
    private String appName;
    private String version;
    private ProgressDialog dialog;

    /**
     * 单例创建,必备
     */
    public static UpdateManager getInstance() {
        if (mInstance == null) {
            mInstance = new UpdateManager();
        }
        return mInstance;
    }

    /**
     * 初始化,必备
     */
    public UpdateManager init(Context context) {
        this.mContext = context;
        return this;
    }

    /**
     * 检测版本是否需要更新
     */
    public void check(String version) {


//        Log.e("###", objects.toString());
//        version = objects.get(0).toString();
        if (Integer.parseInt(version) > getVersionCode()) {
            SharedPreferences sp = mContext.getSharedPreferences("config", Activity.MODE_PRIVATE);
            String ip = sp.getString("IP", null);
            String port = sp.getString("port", null);
//            todo 注释下载地址 lxs
//            apkUrl = "http://" + ip + ":" + port + objects.get(1).toString().trim() + objects.get(0).toString().trim() + ".apk";

            //注册广播 并发送
            UpdateManager.this.receiver = UpdateManager.this.new UpdateReceiver();
            IntentFilter intentFilter = new IntentFilter("com.cubertech.permissions");
            mContext.registerReceiver(UpdateManager.this.receiver, intentFilter);
            Intent intent = new Intent();
            intent.setAction("com.cubertech.permissions");
            intent.putExtra("state", "start");
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 检测版本是否需要更新
     */
    public void check() {
//        Log.e("###", objects.toString());
//        version = objects.get(0).toString();
        SharedPreferences sp = mContext.getSharedPreferences("config",0);
        String ip = sp.getString("IP", null);
        String port = sp.getString("port", null);
//            todo 下载地址
        apkUrl = "http://" + ip + ":" + port + "/APK/WFYS.apk";

//        apkUrl = "http://192.168.1.138:7878/APK/SDWL.apk";
        //注册广播 并发送
        UpdateManager.this.receiver = UpdateManager.this.new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("com.cubertech.permissions");
        mContext.registerReceiver(UpdateManager.this.receiver, intentFilter);
        Intent intent = new Intent();
        intent.setAction("com.cubertech.permissions");
        intent.putExtra("state", "start");
        mContext.sendBroadcast(intent);
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private int getVersionCode() {
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            appName = mContext.getApplicationInfo().loadLabel(mContext.getPackageManager()).toString();
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            return 0;
        }
    }

    /**
     * @Description: TODO 升级提示对话框
     */
    private void showNoticeDialog() {
        final AlertDialog.Builder commonDialog = new AlertDialog.Builder(mContext, R.style.MP_Theme_alertDialog);
        commonDialog.setMessage("检测到您有新的版本可以更新");

        commonDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int permission = ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_EXTERNAL_STORAGE);
                }
                showDownProgress();

            }
        });
        commonDialog.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                interceptFlag = true;
            }
        });
        commonDialog.show();
    }

    /**
     * 展示下载进度条
     */
    private void showDownProgress() {
        this.progressDialog = new ProgressDialog(mContext);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setProgressStyle(1);
        this.progressDialog.setMax(100);
        this.progressDialog.setMessage("正在为您下载最新版本");
        this.progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                mContext.unregisterReceiver(UpdateManager.this.receiver);
            }
        });
        if (!this.progressDialog.isShowing()) {
            this.progressDialog.show();
        }

        downloadApk();
    }


    /**
     * @Description: 进行下载APK
     */

    private void downloadApk() {
        Thread downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 开启线程下载
     */
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                URLConnection conn = null;
                if (url.getProtocol().equals("http")) {
                    conn = (HttpURLConnection) url.openConnection();// 这里判断请求头为http
                } else if (url.getProtocol().equals("https")) {
//                     创建连接
                    conn = (HttpsURLConnection) url.openConnection();
                }
//                    HttpURLConnection conn = (HttpURLConnection) url
//                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                String filePath = null;
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//外部存储卡
                    filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                } else {
                    ToastUtils.showToast("没有SD卡");
                    return;
                }
                mApkPath = filePath + "/" + "DownLoadPDA";
                File file = new File(mApkPath);
                if (!file.exists()) {
                    file.mkdir();
                }
                File apkFile = new File(mApkPath, "SDWL.apk");
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {

                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Log.e("####", e.getMessage());
            } catch (IOException e) {

                e.printStackTrace();

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (e.toString().contains("FileNotFoundException")) {
//                    new AlertDialog.Builder()
                    Looper.prepare();
                    Toast.makeText(mContext, "更新失败,请检查网络连接是否正常!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                Log.e("####1", e.toString());

            }

        }
    };


    /**
     * @Description: TODO 进行安装APK
     */
    private void installApk() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        File apkfile = new File(mApkPath + "/"  + "SDWL.apk");
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);

    }

    /**
     * 检测版本 后进行操作 当检测到版本需要更新时,则弹出提示框,反之正常运行程序
     *
     * @param listener 检测版本监听器  <方法禁用 >
     */
    public void setOnCheckListener(OnCheckListener listener) {
//        String mac = GetMac.getMac();
//
//        if (TextUtils.isEmpty(mac)) {
//            mac = GetMac.tryGetWifiMac(mContext);
//            if (TextUtils.isEmpty(mac)) {
//                mac = GetMac.getMacAddress();
//                if (TextUtils.isEmpty(mac)) {
//                    ToastUtils.showToast("请检查网络连接是否正常!");
//                    return;
//                }
//            }
//        }
////        listener.onCheckListener(false);
//        dialog = ProgressDialog.show(mContext, "", "正在登录....",
//                false, false);
//        HashMap<String, Object> params = new HashMap<String, Object>();
//
//        Log.e("#####", mac);
//        params.put("mac", mac);
//
//        params.put("strToken", "");
//        params.put("strVersion", "");
//        params.put("strPoint", "");
//        params.put("strActionType", "1001");
//        SiteService siteService = SiteService.getInstants();
//        Observable<List<Object>> observable = siteService.Update(params, mContext);
//        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
//            @Override
//            public void onCompleted() {
//                siteService.closeParentDialog();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                siteService.closeParentDialog();
//                ToastUtils.showToast(e.getMessage());
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//            }
//
//            @Override
//            public void onNext(List<Object> objects) {
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                if (ListUtils.isEmpty(objects)) {
//                    return;
//                }
//                //[0] 版本号  ;[1] 版本目录 (下载过程中需要拼写)
//                version = objects.get(0).toString();
//                Log.i("版本信息", getVersionCode() + " → " + version);
//                if (Integer.parseInt(objects.get(0).toString()) > getVersionCode()) {
//                    listener.onCheckListener(true);
////                    apkUrl = objects.get(1).toString().trim();
////                    apkUrl = "http://192.168.1.114:6180/Downloads/20180723.apk";
//                    SharedPreferences sp = mContext.getSharedPreferences("config", Activity.MODE_PRIVATE);
//                    String ip = sp.getString("IP", null);
//                    String port = sp.getString("port", null);
//                    /**
//                     * LSPDA : 最新版本Sdk相对路径
//                     * L50PDA: r2000 依赖三方包 适用于最老模块KT50
//                     * L55PDA: 7.4.7SDK 适用于老型KT55
//                     */
//                    apkUrl = "http://" + ip + ":" + port + objects.get(1).toString().trim() + "/L55PDA/" + objects.get(0).toString().trim() + ".apk";
//                    UpdateManager.this.receiver = UpdateManager.this.new UpdateReceiver();
//                    IntentFilter intentFilter = new IntentFilter("com.cubertech.permissions");
//                    mContext.registerReceiver(UpdateManager.this.receiver, intentFilter);
//                    Intent intent = new Intent();
//                    intent.setAction("com.cubertech.permissions");
//                    intent.putExtra("state", "start");
//                    mContext.sendBroadcast(intent);
//                } else {
//                    listener.onCheckListener(false);
//                }
//            }
//        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    progressDialog.setProgress(progress);//下载进度
                    break;
                case DOWN_OVER:
                    installApk();//下载完成，进行安装APK
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 通过广播控制UI变化(5.0以上还需要添加 读写权限动态请求)
     */
    private class UpdateReceiver extends BroadcastReceiver {
        private UpdateReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.cubertech.permissions")) {
//                if (intent.getBooleanExtra("PERMISSIONS_RESULT", false)) {
                UpdateManager.this.showNoticeDialog();
//                }
            } else {
                ToastUtils.showToast("1");
            }

        }
    }


    /**
     * 监听  版本检测情况
     */
    public interface OnCheckListener {
        void onCheckListener(boolean isUpdate);//true 需要更新,false 不需要
    }

}
