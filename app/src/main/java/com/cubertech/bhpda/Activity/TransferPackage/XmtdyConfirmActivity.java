package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.DMCodeUtils;
import com.cubertech.bhpda.utils.SQLiteUtils;
import com.dothantech.lpapi.IAtBitmap;
import com.dothantech.printer.IDzPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class XmtdyConfirmActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_ENABLE_BT = 1;
    private ImageView dmCode;
    private TextView dmCodeContent;
    private String dm;
    private SiteService siteService;
    private List<Object> result1;
    private TKApplication application;
    private String cptm;
    //private boolean isSave = false;
    private Bitmap bitmap;
    private Handler mHandler = new Handler();
    private boolean isfrist = false;

    // 调用IDzPrinter对象的init方法时用到的IDzPrinterCallback对象
    private final IDzPrinter.IDzPrinterCallback mCallback = new IDzPrinter.IDzPrinterCallback() {

        /****************************************************************************************************************************************/
        // 所有回调函数都是在打印线程中被调用，因此如果需要刷新界面，需要发送消息给界面主线程，以避免互斥等繁琐操作。

        /****************************************************************************************************************************************/

        // 打印机连接状态发生变化时被调用
        @Override
        public void onStateChange(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterState arg1) {
            final IDzPrinter.PrinterAddress printer = arg0;
            switch (arg1) {
                case Connected:
                case Connected2:
                    // 打印机连接成功，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterConnected(printer);


                        }
                    });
                    break;

                case Disconnected:
                    // 打印机连接失败、断开连接，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //if (!isUrlRequest) {
                            isPrintError = true;
                            //}
                            hideProgress();
                            Toast.makeText(getApplicationContext(), "打印连接失败!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                default:
                    break;
            }
        }

        // 蓝牙适配器状态发生变化时被调用
        @Override
        public void onProgressInfo(IDzPrinter.ProgressInfo arg0, Object arg1) {
        }

        @Override
        public void onPrinterDiscovery(IDzPrinter.PrinterAddress arg0, IDzPrinter.PrinterInfo arg1) {
        }

        // 打印标签的进度发生变化是被调用
        @Override
        public void onPrintProgress(IDzPrinter.PrinterAddress address, Object bitmapData, IDzPrinter.PrintProgress progress, Object addiInfo) {
            switch (progress) {
                case Success:
                    // 打印标签成功，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrintSuccess();
                        }
                    });
                    break;

                case Failed:
                    // 打印标签失败，发送通知，刷新界面提示
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            onPrintFailed();
                            hideProgress();
                            //isPrintSuccess = false;
                            Toast.makeText(getApplicationContext(), "打印失败,请检查设备能否正常工作!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                default:
                    break;
            }
        }
    };
    private List<IDzPrinter.PrinterAddress> pairedPrinters;
    private BluetoothAdapter mBluetoothAdapter;

    //private String ph;
    //private boolean isPrintSuccess;
    //private boolean isUrlRequest = false;//是否联网请求//联网成功为true
    private boolean isPrintError = true;//是否打印错误//出现联网成功,但是没有打印时,为true

    private void onPrintSuccess() {
        //isPrintSuccess = true;
        isPrintError = false;
        hideProgress();
        SQLiteUtils instance = SQLiteUtils.getInstance(XmtdyConfirmActivity.this);
        instance.deleteDm(dm);
        Toast.makeText(XmtdyConfirmActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private IDzPrinter.PrinterAddress mPrinterAddress;

    private void onPrinterConnected(IDzPrinter.PrinterAddress printer) {
        if (!isfrist) {
            Toast.makeText(getApplicationContext(), "打印机连接成功!", Toast.LENGTH_SHORT).show();
            isfrist = true;
        }

        mPrinterAddress = printer;
        //SharedPreferences printerConfig = getSharedPreferences("printer", 0);
        SharedPreferences printerConfig = getSharedPreferences("printer", MODE_PRIVATE);
        SharedPreferences.Editor editor = printerConfig.edit();
        editor.putString("printerAddress", mPrinterAddress.macAddress);
        editor.commit();
    }

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmtdy_confirm);
        // 调用IDzPrinter对象的init方法初始化对象
        IDzPrinter.Factory.getInstance().init(XmtdyConfirmActivity.this, mCallback);
//        // 尝试连接上次成功连接的打印机
        //SharedPreferences printerConfig = getSharedPreferences("printer", 0);
        SharedPreferences printerConfig = getSharedPreferences("printer", MODE_PRIVATE);
        String printerAddress = printerConfig.getString("printerAddress", null);
        if (printerAddress != null) {
            mPrinterAddress = new IDzPrinter.PrinterAddress(printerAddress);
        }
        if (mPrinterAddress != null && IDzPrinter.Factory.getAllPrinters() != null
                && IDzPrinter.Factory.getAllPrinters().size() != 0) {
            if (IDzPrinter.Factory.getInstance().connect(mPrinterAddress)) {
                // 连接打印机的请求提交成功，刷新界面提示
                onPrinterConnecting(mPrinterAddress, false);
//                return;
            }
        }
        initView();
    }


    private void onPrinterConnecting(IDzPrinter.PrinterAddress mPrinterAddress, boolean b) {
        this.mPrinterAddress = mPrinterAddress;
        SharedPreferences printerConfig = getSharedPreferences("printer", 0);
        SharedPreferences.Editor editor = printerConfig.edit();
        editor.putString("printerAddress", mPrinterAddress.macAddress);
        editor.commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (b && isPrinterConnected()) {
                    new AlertDialog.Builder(XmtdyConfirmActivity.this).setTitle("提示").setMessage("打印机连接成功,是否打印数据!!")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                                    //siteService = SiteService.getInstants();
                                    if (result1 != null && result1.size() >= 4) {
//                    result.remove(3);
                                    } else {
//                                        return;
                                    }

                                    //isUrlRequest = true;
                                    //isPrintError=false;
                                    //获取打印数据并进行打印
                                    if (bitmap != null) {
                                        if (printBitmap(bitmap, getPrintParam(1, 1))) {
                                            showProgress();
                                            return;
                                        }
                                    }

                                    hideProgress();
                                    showToast("打印失败!");

                                }
                            }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }
        }, 1500);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 应用退出时，调用IDzPrinter对象的quit方法断开打印机连接
        IDzPrinter.Factory.getInstance().quit();
    }

    private void initView() {
        ImageView back = (ImageView) findViewById(R.id.left);
        Button btnQX = (Button) findViewById(R.id.btn_qx);
        Button btnDY = (Button) findViewById(R.id.btn_dy);
        dmCode = (ImageView) findViewById(R.id.dm_code);
        dmCodeContent = (TextView) findViewById(R.id.dm_code_content);

        Log.e("######", getIntent().getStringExtra("dm"));
        dm = getIntent().getStringExtra("dm").trim();
//        dm = "10011B120180529001";
//        dmCodeContent.setText("10011B-1-20180529-0001");
        bitmap = DMCodeUtils.createDMCodeBitmap(dm, 150, 150);
        //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
        //新值[0]数量[1]类型(箱/托)[2]版本号[3]图号(规格)[4]零件号
        result1 = (List<Object>) getIntent().getSerializableExtra("result");
        Intent intent = getIntent();
        cptm = intent.getStringExtra("cptm");
        //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
        /*ph = result.get(0).toString().trim();
//        bitmap = DMCodeUtils.createDMCodeBitmap(ph, 350, 350);
        String str = dm.replace(ph, "").trim();
        String s = null;
        if (!TextUtils.isEmpty(str) && str.length() > 9) {
            s = "-" + str.substring(0, 8) + "-" + str.substring(8, 9) + "-" +
                    str.substring(9, str.length());
        } else {
            throw new RuntimeException("dm ");
        }*/
        //dmCodeContent.setText(ph + s);
        dmCodeContent.setText(dm);

        dmCode.setImageBitmap(bitmap);
//        dmCodeContent.setText(ph + s);


        back.setOnClickListener(this);
        btnQX.setOnClickListener(this);
        btnDY.setOnClickListener(this);
    }

    private void showProgress() {
        dialog = ProgressDialog.show(XmtdyConfirmActivity.this, "", "正在打印....",
                false, false);
    }

    private void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    // 判断当前打印机是否连接
    private boolean isPrinterConnected() {
        // 调用IDzPrinter对象的getPrinterState方法获取当前打印机的连接状态
        IDzPrinter.PrinterState state = IDzPrinter.Factory.getInstance().getPrinterState();

        // 打印机未连接
        if (state == null || state.equals(IDzPrinter.PrinterState.Disconnected)) {
//            DzToast.show(R.string.pleaseconnectprinter);
            if (!state.equals(IDzPrinter.PrinterState.Connecting))
                onPrinterConnected();
            return false;
        }

        // 打印机正在连接
        if (state.equals(IDzPrinter.PrinterState.Connecting)) {
            Toast.makeText(getApplicationContext(), "打印机正在连接中!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 打印机已连接
        return true;
    }

    //连接打印机
    private void onPrinterConnected() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showToast("当前蓝牙有问题");
            return;
        }
        chechBluetooth();

    }

    /**
     * 判断有没有开启蓝牙
     */
    private void chechBluetooth() {
        //没有开启蓝牙
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
//                mBluetoothAdapter.enable();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 设置蓝牙可见性，最多300秒
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
                return;
                //开启蓝牙
            } else {
                pairedPrinters = IDzPrinter.Factory.getAllPrinters();
                if (pairedPrinters == null || pairedPrinters.size() == 0) {
                    showToast("请先通过蓝牙绑定设备!");
                } else
                    new AlertDialog.Builder(XmtdyConfirmActivity.this).setTitle("请选择已绑定的设备").
                            setAdapter(new XmtdyConfirmActivity.DeviceListAdapter(), new XmtdyConfirmActivity.DeviceListItemClicker()).show();
            }
        }
    }

    // 打印机列表的每项点击事件
    private class DeviceListItemClicker implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            IDzPrinter.PrinterAddress printer = pairedPrinters.get(which);
            if (printer != null) {
                // 连接选择的打印机
                if (IDzPrinter.Factory.getInstance().connect(printer)) {
                    isfrist = false;
                    // 连接打印机的请求提交成功，刷新界面提示
                    onPrinterConnecting(printer, true);

                    return;
                }
            }

            // 连接打印机失败，刷新界面提示
            showToast("打印机连接失败");
        }
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    // 打印图片
    private boolean printBitmap(Bitmap bitmap, Bundle param) {
        // 创建IAtBitmap对象
        IAtBitmap api = IAtBitmap.Factory.createInstance();
        //api.startJob(60 * 100, 40 * 100);
        api.startJob(80 * 100, 50 * 100);//打印纸宽高80*50
        // 设置之后绘制的对象内容旋转180度
//        api.setItemOrientation(270);
//        api.drawBitmap(bitmap,0,0,15*100,15*100);
        // 设置之后绘制的对象内容旋转180度  201808171054074040001
        api.setItemOrientation(0);
        /**
         *  api.drawText(内容:"4", Y轴:0 * 100, X轴:0 * 100, 宽:4 * 100,高: 4 * 100,
         *  字体大小:4 * 100, 字体类型:IAtBitmap.FontStyle.REGULAR);
         *  1*100=1cm
         */
        //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
        //
        //ph = result.get(0).toString().trim();
        //String str = dm.replace(ph, "").trim();
//       /* api.drawBitmap(bitmap, 4 * 100, 1 * 100, 30 * 100, 30 * 100);
//        api.drawText("日期", 38 * 100, 2 * 100, 16 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("测试", 38 * 100, 8 * 100, 24 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("流水", 38 * 100, 14 * 100, 16 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("测试", 38 * 100, 20 * 100, 24 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("品号:" + "测试", 4 * 100, 32 * 100, 50 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);*/

        //新值[0]数量[1]类型(箱/托)[2]版本号[3]图号(规格)[4]零件号
        /**
         *   api.drawText("日期   "+ result1.get(5).toString().trim(), 38 * 100, 4 * 100, 40 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
         api.drawText("数量   "+ result1.get(0).toString().trim(), 38 * 100, 10 * 100, 40 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
         api.drawText("箱/托 "+ result1.get(1).toString().trim(), 38 * 100, (14+2) * 100, 24 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
         api.drawText("版本   "+ result1.get(2).toString().trim(), 38 * 100, 22 * 100, 40 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);

         api.drawText("图号   " + result1.get(3).toString().trim(), 4 * 100, (34+2) * 100, 60 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
         api.drawText("零件号" + result1.get(4).toString().trim(), 4 * 100, (40+2) * 100, 60 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);

         */
        api.drawBitmap(bitmap, 4 * 100, 4 * 100, 30 * 100, 30 * 100);
        api.drawText("日期            " + result1.get(5).toString().trim(), 38 * 100, 4 * 100, 40 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText("Date", 38 * 100, 7 * 100, 40 * 100, 4 * 100, 250, IAtBitmap.FontStyle.REGULAR);

        api.drawText("数量            " + result1.get(0).toString().trim(), 38 * 100, 11 * 100, 40 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText("Quantity", 38 * 100, 14 * 100, 40 * 100, 4 * 100, 250, IAtBitmap.FontStyle.REGULAR);

        api.drawText("箱/托          " + result1.get(1).toString().trim(), 38 * 100, (14 + 4) * 100, 24 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText(result1.get(1).toString().trim().contains("箱") ? "Box" : "Quantity", 38 * 100, 21 * 100, 40 * 100, 4 * 100, 250, IAtBitmap.FontStyle.REGULAR);

        api.drawText("版本   " + result1.get(2).toString().trim(), 38 * 100, 25 * 100, 40 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText("Revision", 38 * 100, 28 * 100, 40 * 100, 4 * 100, 250, IAtBitmap.FontStyle.REGULAR);


        api.drawText("图号                 " + result1.get(3).toString().trim(), 4 * 100, (34 + 2) * 100, 60 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText("Drawing Number", 4 * 100, (34 + 5) * 100, 60 * 100, 4 * 100, 250, IAtBitmap.FontStyle.REGULAR);

        api.drawText("零件号             " + result1.get(4).toString().trim(), 4 * 100, (40 + 3) * 100, 60 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText("Part No.", 4 * 100, (43 + 3) * 100, 60 * 100, 4 * 100, 250, IAtBitmap.FontStyle.REGULAR);

        api.endJob();

        // 打印
        return IDzPrinter.Factory.getInstance().print(api, param);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        if (requestCode == REQUEST_ENABLE_BT) {
            switch (resultCode) {
                // 点击确认按钮
                case Activity.RESULT_OK: {
                    // TODO 用户选择开启 Bluetooth，Bluetooth 会被开启
                    pairedPrinters = IDzPrinter.Factory.getAllPrinters();
                    new AlertDialog.Builder(XmtdyConfirmActivity.this).setTitle("请选择打印机").setAdapter(new XmtdyConfirmActivity.DeviceListAdapter(), new XmtdyConfirmActivity.DeviceListItemClicker()).show();
                }
                break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED: {
                    // TODO 用户拒绝打开 Bluetooth, Bluetooth 不会被开启

                }
                break;
                default:
                    break;
            }
        }
    }

    // 获取打印时需要的打印参数
    private Bundle getPrintParam(int copies, int orientation) {
        Bundle param = new Bundle();
//
//        // 打印浓度
//        if (printDensity >= 0) {
//            param.putInt(PrintParamName.PRINT_DENSITY, printDensity);
//        }
//
//        // 打印速度
//        if (printSpeed >= 0) {
//            param.putInt(PrintParamName.PRINT_SPEED, printSpeed);
//        }
//
//        // 间隔类型
//        if (gapType >= 0) {
//            param.putInt(PrintParamName.GAP_TYPE, gapType);
//        }

        // 打印页面旋转角度
        if (orientation != 0) {
            param.putInt(IDzPrinter.PrintParamName.PRINT_DIRECTION, orientation);
        }

        // 打印份数
        if (copies > 1) {
            param.putInt(IDzPrinter.PrintParamName.PRINT_COPIES, copies);
        }

        return param;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_dy:
                if (isPrinterConnected()) {

                    //siteService = SiteService.getInstants();
                    //原值[0]品号[1]品名[2]规格[3]容器名称[4]收容数
                    if (result1 != null && result1.size() >= 4) {
//                    result.remove(3);
                    } else {
                        return;
                    }
                    //isUrlRequest = true;
                    //isPrintError=false;

//                                  获取打印数据并进行打印
                    if (bitmap != null) {
                        if (printBitmap(bitmap, getPrintParam(1, 1))) {
                            showProgress();
                            return;
                        }
                    }

                    hideProgress();
                    showToast("打印失败!");

                }
                break;
        }

    }

    /**
     * @see android.app.Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {
        if (isPrintError) {
            AlertDialog.Builder builder = new AlertDialog.Builder(XmtdyConfirmActivity.this);
            builder.setTitle("提示");
            builder.setMessage("存在未打印的数据,确定退出?");
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    siteService = SiteService.getInstants();
                    HashMap<String, Object> params = new HashMap<>();
                    application = (TKApplication) getApplication();
                    String user = application != null && application.getUserName() != null ? application.getUserName() : "";
                    params.put("users", user);
                    params.put("database", getSharedPreferences("config", Activity.MODE_PRIVATE).getString("data", ""));
                    params.put("strToken", "");
                    params.put("strVersion", "");
                    params.put("strPoint", "");
                    params.put("strActionType", "1001");
                    String IP = "";
                    String port = "";

                    if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
                        IP = application.getUrl();
                        port = application.getPort();
                    }
                    Observable<String> observable = siteService.logPring(params, XmtdyConfirmActivity.this, IP, port);
                    observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            siteService.closeParentDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            siteService.closeParentDialog();
                            Toast.makeText(XmtdyConfirmActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(String s) {
                            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                            finish();
                        }
                    });

                }
            });
            builder.show();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            finish();
        }

    }

    // 用于填充打印机列表的Adapter
    private class DeviceListAdapter extends BaseAdapter {
        private TextView tv_name = null;
        private TextView tv_mac = null;

        @Override
        public int getCount() {
            return pairedPrinters.size();
        }

        @Override
        public Object getItem(int position) {
            return pairedPrinters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(XmtdyConfirmActivity.this).inflate(R.layout.printer_item, null);
            }
            tv_name = (TextView) convertView.findViewById(R.id.tv_device_name);
            tv_mac = (TextView) convertView.findViewById(R.id.tv_macaddress);

            if (pairedPrinters != null && pairedPrinters.size() > position) {
                IDzPrinter.PrinterAddress printer = pairedPrinters.get(position);
                tv_name.setText(printer.shownName);
                tv_mac.setText(printer.macAddress);
            }

            return convertView;
        }
    }
}
