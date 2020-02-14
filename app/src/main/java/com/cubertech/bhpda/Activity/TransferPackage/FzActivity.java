package com.cubertech.bhpda.Activity.TransferPackage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.DMCodeUtils;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ScanActivity;
import com.dothantech.lpapi.IAtBitmap;
import com.dothantech.printer.IDzPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 复制
 * Created by Administrator on 2018/4/25.
 */

public class FzActivity extends AppCompatActivity implements View.OnClickListener {
    //请求的code
    public static final int REQUEST_ENABLE_BT = 1;
    private EditText et_tm;
    private SiteService siteService;
    AccountInfo ai;
    private TKApplication application;
    private ImageView dmCode;
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
                            Toast.makeText(getApplicationContext(), "打印失败,请联系开发人员!", Toast.LENGTH_SHORT).show();
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
    private String serial;
    private String date;
    private String ph;

    private void onPrintSuccess() {
        hideProgress();
//        dialog = ProgressDialog.show(RqmdyConfirmActivity.this, "", "打印成功,请截图,并联系开发人员....",
//                false, false);
    }

    private IDzPrinter.PrinterAddress mPrinterAddress;

    private void onPrinterConnected(IDzPrinter.PrinterAddress printer) {
        if (!isfrist) {
            Toast.makeText(getApplicationContext(), "打印机连接成功!", Toast.LENGTH_SHORT).show();
            isfrist = true;
        }

        mPrinterAddress = printer;
        SharedPreferences printerConfig = getSharedPreferences("printer", 0);
        SharedPreferences.Editor editor = printerConfig.edit();
        editor.putString("printerAddress", mPrinterAddress.macAddress);
        editor.commit();
    }

    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fz);
        // 调用IDzPrinter对象的init方法初始化对象
        IDzPrinter.Factory.getInstance().init(FzActivity.this, mCallback);
//        // 尝试连接上次成功连接的打印机
        SharedPreferences printerConfig = getSharedPreferences("printer", 0);
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
        et_tm = (EditText) findViewById(R.id.et_tm);
        et_tm.setSelection(0);
        et_tm.requestFocus();
        dmCode = (ImageView) findViewById(R.id.dm_code);
        et_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //执行相关读取操作
                    //Toast.makeText(GxzyActivity.this, "执行读取数据库操作", Toast.LENGTH_SHORT).show();
                    scanCode();//
                }
                return false;
            }
        });
        ImageView back = (ImageView) findViewById(R.id.left);
        Button btnQX = (Button) findViewById(R.id.btn_qx);
        Button btnDY = (Button)findViewById(R.id.btn_dy);
        btnDY.setOnClickListener(this);
        back.setOnClickListener(this);
        btnQX.setOnClickListener(this);
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
                    new AlertDialog.Builder(FzActivity.this).setTitle("提示").setMessage("打印机连接成功,是否打印数据!!")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    // 获取打印数据并进行打印
                                    if (bitmap != null) {
                                        if (printBitmap(bitmap, getPrintParam(1, 1))) {
                                            showProgress();
                                            return;
                                        }
                                    }
                                    hideProgress();
                                }
                            }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }
        }, 1000);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 应用退出时，调用IDzPrinter对象的quit方法断开打印机连接
        IDzPrinter.Factory.getInstance().quit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        finish();
    }

    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class);
        Intent scanIntent = intentIntegrator.createScanIntent();
        startActivityForResult(scanIntent, 999);
    }

    /**
     * 扫描二维码
     */
    private void scanCode() {
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(FzActivity.this, "请扫描条码", Toast.LENGTH_LONG).show();
            return;
        }
        bitmap = DMCodeUtils.createDMCodeBitmap(et_tm.getText().toString().trim(), 350, 350);
        dmCode.post(new Runnable() {
            @Override
            public void run() {
                dmCode.setImageBitmap(bitmap);
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 强制隐藏软键盘
        imm.hideSoftInputFromWindow(et_tm.getWindowToken(), 0);


//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("code", tm);
//        params.put("database", ai.getData());
//        params.put("strToken", "");
//        params.put("strVersion", "");
//        params.put("strPoint", "");
//        params.put("strActionType", "1001");
//        siteService = SiteService.getInstants();
//        //通过Application传值
//        String IP = "";
//        String port = "";
//        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
//            IP = application.getUrl();
//            port = application.getPort();
//        }
//        //先弄一个观察者
//        Observable<List<Object>> observable = siteService.DMcodeFgzyBh(params,
//                FzActivity.this, IP, port);
//        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
//            @Override
//            public void onCompleted() {
//                siteService.closeParentDialog();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                siteService.closeParentDialog();
//                Toast.makeText(FzActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNext(List<Object> obj) {
//                if (obj == null || obj.size() <= 0) {
//                    return;
//                }
//
//            }
//        });
    }

    private void showProgress() {
        dialog = ProgressDialog.show(FzActivity.this, "", "正在打印....",
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
                    new AlertDialog.Builder(FzActivity.this).setTitle("请选择已绑定的设备").
                            setAdapter(new DeviceListAdapter(), new DeviceListItemClicker()).show();
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
        api.startJob(60 * 100, 40 * 100);
        // 设置之后绘制的对象内容旋转180度
//        api.setItemOrientation(270);
//        api.drawBitmap(bitmap,0,0,15*100,15*100);
        // 设置之后绘制的对象内容旋转180度
        api.setItemOrientation(0);
        /**
         *  api.drawText(字体:"4", Y轴:0 * 100, X轴:0 * 100, 宽:4 * 100,高: 4 * 100,
         *  字体大小:4 * 100, 字体类型:IAtBitmap.FontStyle.REGULAR);
         *  1*100=1cm
         */
        api.drawBitmap(bitmap, 4 * 100, 1 * 100, 30 * 100, 30 * 100);
        api.drawText("日期", 38 * 100, 2 * 100, 16 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText(date, 38 * 100, 8 * 100, 24 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText("流水", 38 * 100, 14 * 100, 16 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText(serial, 38 * 100, 20 * 100, 24 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        api.drawText("品号:"+ph, 4 * 100, 32 * 100, 50 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);


        //        api.drawText("1", 4 * 100, 8 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("2", 4 * 100, 12 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("3", 4 * 100, 16 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("4", 4 * 100, 20 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("5", 4 * 100, 24 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("6", 4 * 100, 28 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("7", 4 * 100, 32 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("8", 4 * 100, 36 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
//        api.drawText("9", 4 * 100, 40 * 100, 4 * 100, 4 * 100, 4 * 100, IAtBitmap.FontStyle.REGULAR);
        // 结束绘图任务
        api.endJob();

        // 打印
        return IDzPrinter.Factory.getInstance().print(api, param);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999) {
            String ScanResult = "";
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    //Toast.makeText(getActivity(),"内容为空",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // ScanResult 为 获取到的字符串
                    ScanResult = intentResult.getContents();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            et_tm.setText(ScanResult);
            scanCode();

        } else if (requestCode == REQUEST_ENABLE_BT) { // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
            switch (resultCode) {
                // 点击确认按钮
                case Activity.RESULT_OK: {
                    // TODO 用户选择开启 Bluetooth，Bluetooth 会被开启
                    pairedPrinters = IDzPrinter.Factory.getAllPrinters();
                    new AlertDialog.Builder(FzActivity.this).setTitle("请选择打印机").setAdapter(new DeviceListAdapter(), new DeviceListItemClicker()).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_dy:
                String content = et_tm.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getBaseContext(), "请扫描!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    //流水号
                    serial = content.substring(content.length() - 4, content.length());
                    date = content.substring(content.length() - 13, content.length()-5);
                    ph = content.substring(0, content.length()-13);
                    Log.e("#####"+content, serial +"?////"+ date +"////"+ ph);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "请扫描正确的容器码!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isPrinterConnected()) {
//                    获取打印数据并进行打印
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
                convertView = LayoutInflater.from(FzActivity.this).inflate(R.layout.printer_item, null);
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
