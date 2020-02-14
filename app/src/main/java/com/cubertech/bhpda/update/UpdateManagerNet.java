package com.cubertech.bhpda.update;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.cubertech.bhpda.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UpdateManagerNet {
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;

	private String str_url;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	ResponseString reponseCallBack;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				//安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

	/*
	 * public UpdateManagerNet(Context context,String url) { this.mContext =
	 * context; this.str_url=url; }
	 */

	public UpdateManagerNet(Context context, String url,
                            final ResponseString reponseCallBack) {
		this.mContext = context;
		this.str_url = url;
		this.reponseCallBack = reponseCallBack;
	}

	/**
	 * 检测软件更新
	 * 
	 * @throws IOException
	 * @throws NotFoundException
	 */
	public void checkUpdate() throws NotFoundException, IOException {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					switch(isUpdate()){
					case 0:
						Log.d("消息", "已是最新版本");
						reponseCallBack.onSuccess("");
						break;
					case 1:
						// 显示提示对话框
						Looper.prepare();
						showNoticeDialog();
						Looper.loop();
						Log.d("消息", "有新版本");
						break;
					case 2:
						reponseCallBack.onSuccess("xml内容错误");
						break;
					}
					/*if (isUpdate()) {
						// 显示提示对话框
						Looper.prepare();
						showNoticeDialog();
						Looper.loop();
						Log.d("消息", "有新版本");
					} else {
						// Toast.makeText(mContext, R.string.soft_update_no,
						// Toast.LENGTH_LONG).show();

						Log.d("消息", "已是最新版本");
						reponseCallBack.onSuccess("");
					}*/
				} catch (NotFoundException e) {
					e.printStackTrace();
					Log.d("错误", e.toString());
					reponseCallBack.onSuccess(e.toString());

				} catch (IOException e) {
					e.printStackTrace();
					Log.d("错误", e.toString());
					reponseCallBack.onSuccess(e.toString());
				}

			}
		}).start();
	}

	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 * @throws IOException
	 */
	private int isUpdate() throws IOException {
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		// 把version.xml放到网络上，然后获取文件信息
		// URL url = new URL("http://imp.ctone.net/Frame/version.xml");
		//URL url = new URL("http://192.168.1.198:9999/updata/version.xml");
		str_url=str_url.replace("http://","");
        if(!str_url.endsWith("/")){
            str_url+="/";
        }
		URL url = new URL("http://"+str_url + "version.xml");
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream inStream = urlConn.getInputStream();
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			Log.i("消息", "33333");
			e.printStackTrace();
			return 2;
		}
		if (null != mHashMap) {
			int serviceCode = Integer.valueOf(mHashMap.get("version"));// String.valueOf("serviceCode"+"---"+serviceCode)
			// Toast.makeText(UpdateManager.this,'', Toast.LENGTH_SHORT).show();
			Log.i("-----serviceCode", "" + serviceCode);
			Log.i("-----versionCode", "" + versionCode);

			//  版本判断
			if (serviceCode > versionCode) {
				//return true;
				return 1;
			}
		} else {
			Log.i("-----null == mHashMap", "null == mHashMap");
		}

		return 0;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			/*
			 * versionCode = context.getPackageManager().getPackageInfo(
			 * "com.gaoxiaotongctone", 0).versionCode;
			 */
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.i("获取版本号报错", "" + e.toString());
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		// 构造对话框
		Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage("检测到新版本，立即更新吗");
		// 更新
		builder.setPositiveButton("更新",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 显示下载对话框
						showDownloadDialog();
					}
				});
		// 稍后更新
		builder.setNegativeButton("退出",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						System.exit(0);
					}
				});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		// 构造软件下载对话框
		Builder builder = new Builder(mContext);
		builder.setTitle("正在更新");
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton("取消",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置取消状态
						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.d("错误", e.toString());
				reponseCallBack.onSuccess(e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("错误", e.toString());
				reponseCallBack.onSuccess(e.toString());

			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 *安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

	/**
	 * 返回结果接口
	 */
	public interface ResponseString {
		public void onSuccess(String result);

	}
}
