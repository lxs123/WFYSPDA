package com.cubertech.bhpda.Activity.printerB3s;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.wfys.BjplActivity;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.DMCodeUtils;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.PermissionUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;
import com.dothantech.lpapi.LPAPI;
import com.dothantech.printer.IDzPrinter;
import com.gengcon.www.jcapi.api.JCAPI;
import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;
import com.google.zxing.qrcode.encoder.QRCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 标签打印
 */
public class PrinterConfirmActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView dmCode;
    private TextView dmCodeContent;
    private String dm;
    private Bitmap bitmap;
    private List<Object> result;
    private String cptm;
    private String ph;
    private ProgressDialog dialog;
    private boolean isPrintError;
    private static final String TAG = "BhgConfirmActivity";


    /**
     * B11系列打印机打印接口
     */
    private LPAPI mLPAPI = null;
    /**
     * B11系列打印机回调接口
     */
    private LPAPI.Callback mLPAPICallback = null;


    /**
     * B3S系列打印机打印接口
     */
    JCAPI mJCAPI = null;
    /**
     * B3S系列打印机回调接口
     */
    JCAPI.CallBack mJCAPICallback = null;

    /**
     * B3S系列
     */
    private static final String B3S = "B3S";
    /**
     * B11系列
     */
    private static final String B11 = "B11";
    /**
     * 连接设备类型
     */
    private static final int DISCONNECTED_TYPE = -1;
    private static final int B11_CONNECTED_TYPE = 1;
    private static final int B3S_CONNECTED_TYPE = 2;
    /**
     * -1 未连接打印机 .
     */
    private static int mConnectPrinterType = DISCONNECTED_TYPE;


    /**
     * 设备列表数据
     */
    private List<Device> mDeviceList = new ArrayList<>();

    /**
     * 设备mac地址
     */
    private List<String> mDeviceAddressList = new ArrayList<>();
    /**
     * 设备列表适配器
     */
    private DeviceAdapter mDeviceAdapter;
    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * 广播过滤
     */
    private IntentFilter mFilter;
    /**
     * 广播
     */
    private BroadcastReceiver mReceiver;
    /**
     * 上一次连接成功设备的位置
     */
    private int mLastConnectSuccessItemPosition;
    private RecyclerView rlDevice;
    private AlertDialog.Builder builder;

    private PermissionUtils permissionUtils;
    private List<String> list;
    private AlertDialog alertDialog;
    private TextView tvScanTitle;
    private Button btnDY;
    private ServiceUtils su = ServiceUtils.getInstance();
    @BindView(R.id.et_tm)
    EditText etTm;
    private List<Object> objectList;
    boolean isFirst = true;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_confirm);
        ButterKnife.bind(this);
        permissionUtils = new PermissionUtils(PrinterConfirmActivity.this);
        list = new ArrayList<>();

        list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);

        Intent intent = getIntent();
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(timeMillis);
        SharedPreferences sp = getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        String name = sp.getString("name", null);
        initView();
        initPrint();
        etTm.setSelection(0);
        etTm.requestFocus();

        etTm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    }

    private void scanCode() {

//
        //todo 请填写  方法名
        String link = "hqDysj";
        HashMap<String, Object> params = new HashMap<String, Object>();
        //todo 请修改  参数名
        params.put("dh", etTm.getText().toString());
//        params.put("pmdsdocno2", tmAll);
        params.put("strToken", "");
        params.put("strVersion", Utils.getVersions(this));
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        su.callService(PrinterConfirmActivity.this, link, params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                objectList = (List<Object>) o;
                //0码值 1工单号   2品号  3名称  4规格   5项目  6 设备号
                Bitmap qrCode = DMCodeUtils.createQRCode(String.valueOf(objectList.get(0)), 350, 350, null);
                dmCode.setImageBitmap(qrCode);
                dmCodeContent.setText(String.valueOf(objectList.get(0)));

            }

            @Override
            public void onFailure(String str) {
                CommonUtil.showErrorDialog(PrinterConfirmActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PrinterConfirmActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须


            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });
    }

    @Override
    protected void onDestroy() {
        //断开打印机
        if (mConnectPrinterType == B11_CONNECTED_TYPE) {
            mLPAPI.closePrinter();
            mConnectPrinterType = DISCONNECTED_TYPE;
        }

        if (mConnectPrinterType == B3S_CONNECTED_TYPE) {
            mJCAPI.close();
            mConnectPrinterType = DISCONNECTED_TYPE;
        }
        super.onDestroy();
        //解除广播注册
        unregisterReceiver(mReceiver);
    }

    private void initView() {
        ImageView back = (ImageView) findViewById(R.id.left);
        Button btnQX = (Button) findViewById(R.id.btn_qx);
        btnDY = (Button) findViewById(R.id.btn_dy);
        btnDY.setText("连接");
        dmCode = (ImageView) findViewById(R.id.dm_code);
        dmCodeContent = (TextView) findViewById(R.id.dm_code_content);
//        Bitmap qrCode = DMCodeUtils.createQRCode("11010000126#5101-20032700001#0001", 350, 350, null);
//        dmCode.setImageBitmap(qrCode);
     /*   Log.e("######", getIntent().getStringExtra("dm"));
        dm = getIntent().getStringExtra("dm").trim();
//        dm = "10011B120180529001";
//        dmCodeContent.setText("10011B-1-20180529-0001");
        bitmap = DMCodeUtils.createDMCodeBitmap(dm, 150, 150);
        //[0]品号[1]品名[2]规格[3]容器名称[4]收容数
        result = (List<Object>) getIntent().getSerializableExtra("result");
        Log.e("######", result.toString());
        Intent intent = getIntent();
        cptm = intent.getStringExtra("cptm");
        ph = result.get(0).toString().trim();
//        bitmap = DMCodeUtils.createDMCodeBitmap(ph, 350, 350);
        String str = dm.replace(ph, "").trim();
        String s = null;
        if (!TextUtils.isEmpty(str) && str.length() > 9) {
            s = "-" + str.substring(0, 8) + "-" + str.substring(8, 9) + "-" +
                    str.substring(9, str.length());
        } else {
            throw new RuntimeException("dm ");
        }
        dmCodeContent.setText(ph + s);

        dmCode.setImageBitmap(bitmap);*/
//        dmCodeContent.setText(ph + s);

        builder = new AlertDialog.Builder(PrinterConfirmActivity.this, R.style.MP_Theme_alertDialog);
        View view = LayoutInflater.from(PrinterConfirmActivity.this).inflate(R.layout.dialog_b3s_bluetooth, null);
        rlDevice = (RecyclerView) view.findViewById(R.id.dialog_blue_list);
        tvScanTitle = (TextView) view.findViewById(R.id.dialog_blue_title);
        rlDevice.setLayoutManager(new LinearLayoutManager(PrinterConfirmActivity.this));
        mDeviceAdapter = new DeviceAdapter(mDeviceList);
        rlDevice.setAdapter(mDeviceAdapter);
        builder.setTitle("请连接蓝牙设备！");
        builder.setPositiveButton("搜索", null);
        builder.setNegativeButton("取消", null);
        alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        //判断蓝牙是否开启
                        if (mBluetoothAdapter.isEnabled()) {
                            //判断搜索权限是否开启
                            permissionUtils.request(list, 100, new PermissionUtils.CallBack() {
                                @Override
                                public void grantAll() {
                                    mDeviceList.clear();
                                    mDeviceAddressList.clear();
                                    //刷新列表
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mLastConnectSuccessItemPosition = -1;
                                            mDeviceAdapter.notifyDataSetChanged();
                                        }
                                    });
                                    tvScanTitle.setText("正在搜索...");
                                    tvScanTitle.setVisibility(View.VISIBLE);
                                    //权限获取成功,允许搜索
                                    mBluetoothAdapter.startDiscovery();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            int itemCount = mDeviceAdapter.getItemCount();
                                            if (itemCount == 0) {
                                                tvScanTitle.setVisibility(View.VISIBLE);
                                                tvScanTitle.setText("附近无设备！");
                                            } else {
                                                tvScanTitle.setVisibility(View.GONE);
                                            }
                                            mBluetoothAdapter.cancelDiscovery();
                                        }
                                    }, 30000);
                                }

                                @Override
                                public void denied() {
                                    Toast.makeText(PrinterConfirmActivity.this, "请开启位置权限,用于搜索蓝牙设备", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(PrinterConfirmActivity.this, "请开启蓝牙", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        back.setOnClickListener(this);
        btnQX.setOnClickListener(this);
        btnDY.setOnClickListener(this);
        //连接设备打印机
        mDeviceAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                runOnUiThread(new Runnable() {

                    private int deviceStatus;

                    @Override
                    public void run() {
                        final Device device = mDeviceList.get(position);
                        String deviceName = device.getDeviceName();
                        String deviceAddress = device.getDeviceAddress();
                        SharedPreferences sharedPreferences = getSharedPreferences("printer", 0);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("name", deviceName);
                        edit.putString("address", deviceAddress);
                        edit.putInt("status", device.getDeviceStatus());
                        edit.commit();

                        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                        deviceStatus = device.getDeviceStatus();
                        //未配对 进行配对
                        if (deviceStatus == BluetoothDevice.BOND_NONE) {
                            try {
//                                // 与设备配对
                                ToastUtils.showToast("请在设置中，蓝牙配对打印机！");
                                return;
//                                if (ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice)) {
//
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            device.setDeviceConnectStatus(12);
//                                            mDeviceAdapter.notifyDataSetChanged();
////                                            deviceStatus = device.getDeviceStatus();
//
//                                            Toast.makeText(BhgConfirmActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//
//                                } else {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(BhgConfirmActivity.this, "配对失败", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //已配对进行连接
                        if (deviceStatus == BluetoothDevice.BOND_BONDED) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    ToastUtils.showToast("正在连接打印机...");
                                    Looper.loop();
                                }
                            }).start();

                            if (deviceName.startsWith(B3S)) {
                                Log.d(TAG, "连接开始时间: " + System.currentTimeMillis());
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean isOpenSuccess = mJCAPI.openPrinterByAddress(deviceAddress);
                                        Log.d(TAG, "连接结束时间: " + System.currentTimeMillis());
                                        if (isOpenSuccess) {
                                            if (mLastConnectSuccessItemPosition != -1) {
                                                mDeviceList.get(mLastConnectSuccessItemPosition).setDeviceConnectStatus(12);
                                            }
                                            device.setDeviceConnectStatus(14);

                                            mLastConnectSuccessItemPosition = position;
                                            mConnectPrinterType = B3S_CONNECTED_TYPE;
                                            ToastUtils.showToast("连接成功");
                                            btnDY.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    btnDY.setText("打印");
                                                }
                                            });
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    printLabel();
                                                    mDeviceAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            ToastUtils.showToast("连接失败！");
                                        }
                                    }
                                });


                            }

                            if (deviceName.startsWith(B11)) {
                                boolean isOpenSuccess = mLPAPI.openPrinterSync(deviceName);

                                if (isOpenSuccess) {
                                    if (mLastConnectSuccessItemPosition != -1) {
                                        mDeviceList.get(mLastConnectSuccessItemPosition).setDeviceConnectStatus(12);
                                    }
                                    device.setDeviceConnectStatus(14);
                                    mLastConnectSuccessItemPosition = position;
                                    mConnectPrinterType = B11_CONNECTED_TYPE;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDeviceAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }

                    }
                });
            }
        });

    }

    ProgressDialog progressDialog;

    private void initPrintConnect() {
        progressDialog = ProgressDialog.show(PrinterConfirmActivity.this, "", "正在加载", false, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    private int deviceStatus;

                    @Override
                    public void run() {

                        SharedPreferences sharedPreferences = getSharedPreferences("printer", 0);
                        String name = sharedPreferences.getString("name", "");
                        String address = sharedPreferences.getString("address", "");
                        int status = sharedPreferences.getInt("status", 0);
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) {
                            return;
                        }

                        final Device device = new Device(name, address,
                                status);
                        String deviceName = device.getDeviceName();
                        String deviceAddress = device.getDeviceAddress();
                        Log.e("#####", device.toString());

                        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                        deviceStatus = device.getDeviceStatus();
                        //未配对 进行配对
                        if (deviceStatus == BluetoothDevice.BOND_NONE) {
                            try {
//                                // 与设备配对
                                ToastUtils.showToast("请在设置中，蓝牙配对打印机！");
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //已配对进行连接
                        if (deviceStatus == BluetoothDevice.BOND_BONDED) {

                            if (deviceName.startsWith(B3S)) {
                                Log.d(TAG, "连接开始时间: " + System.currentTimeMillis());
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean isOpenSuccess = mJCAPI.openPrinterByAddress(deviceAddress);
                                        Log.d(TAG, "连接结束时间: " + System.currentTimeMillis());
                                        if (isOpenSuccess) {
//                                    if (mLastConnectSuccessItemPosition != -1) {
//                                        mDeviceList.get(mLastConnectSuccessItemPosition).setDeviceConnectStatus(12);
//                                    }
                                            device.setDeviceConnectStatus(14);
                                            btnDY.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    btnDY.setText("打印");
                                                }
                                            });
//                                    mLastConnectSuccessItemPosition = position;
                                            progressDialog.dismiss();
                                            mConnectPrinterType = B3S_CONNECTED_TYPE;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//                                            printLabel();
                                                    mDeviceAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            ToastUtils.showToast("连接失败！");
                                            progressDialog.dismiss();
                                        }

                                    }
                                });


                            }

//                    if (deviceName.startsWith(B11)) {
//                        boolean isOpenSuccess = mLPAPI.openPrinterSync(deviceName);
//
//                        if (isOpenSuccess) {
//                            if (mLastConnectSuccessItemPosition != -1) {
//                                mDeviceList.get(mLastConnectSuccessItemPosition).setDeviceConnectStatus(12);
//                            }
//                            device.setDeviceConnectStatus(14);
//                            mLastConnectSuccessItemPosition = position;
//                            mConnectPrinterType = B11_CONNECTED_TYPE;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mDeviceAdapter.notifyDataSetChanged();
//                                }
//                            });
//                        }
//                    }
                        }

                    }
                });
            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            }
        }, 5000);
    }

    /**
     * 初始化打印控件
     */
    private void initPrint() {
        mLPAPICallback = new LPAPI.Callback() {
            @Override
            public void onProgressInfo(IDzPrinter.ProgressInfo progressInfo, Object o) {

            }

            @Override
            public void onStateChange(IDzPrinter.PrinterAddress printerAddress, IDzPrinter.PrinterState printerState) {

            }

            @Override
            public void onPrintProgress(IDzPrinter.PrinterAddress printerAddress, Object o, IDzPrinter.PrintProgress printProgress, Object o1) {

            }

            @Override
            public void onPrinterDiscovery(IDzPrinter.PrinterAddress printerAddress, IDzPrinter.PrinterInfo printerInfo) {

            }
        };


        mLPAPI = LPAPI.Factory.createInstance(mLPAPICallback);


        mJCAPICallback = new JCAPI.CallBack() {
            @Override
            public void onConnectSuccess() {
                alertDialog.dismiss();
                btnDY.setText("打印");
                ToastUtils.showToast("连接成功！");
            }

            @Override
            public void onConnectFail() {
                ToastUtils.showToast("连接失败！");
                btnDY.setText("连接");
            }

            @Override
            public void disConnect() {

            }

            @Override
            public void onAbnormalResponse(int i) {

            }

            @Override
            public void electricityChange(int i) {

            }
        };


        mJCAPI = JCAPI.getInstance(PrinterConfirmActivity.this, mJCAPICallback);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //蓝牙广播意图过滤
        mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mFilter.setPriority(1000);
        mFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        mFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //获取意图
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceAddress = bluetoothDevice.getAddress();
                    String name = bluetoothDevice.getName();
                    boolean isPrinterType = bluetoothDevice.getBluetoothClass().getDeviceClass() == 1664;


                    if (!mDeviceAddressList.contains(deviceAddress) && name != null && isPrinterType) {
                        mDeviceAddressList.add(deviceAddress);
                        Device device = null;
                        //显示已配对设备
                        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                            device = new Device(bluetoothDevice.getName(), deviceAddress, 12);
                        } else if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                            device = new Device(bluetoothDevice.getName(), deviceAddress, 10);
                        }

                        mDeviceList.add(device);
                        //刷新列表
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceAdapter.notifyDataSetChanged();
                            }
                        });

                    }


                } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceAddress = bluetoothDevice.getAddress();
                    if (mDeviceAddressList.contains(deviceAddress)) {
                        for (Device device : mDeviceList) {
                            if (device.getDeviceAddress().equals(deviceAddress)) {
                                if (device.getDeviceStatus() == 14) {
                                    return;
                                } else {
                                    device.setDeviceConnectStatus(14);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDeviceAdapter.notifyDataSetChanged();
                                        }
                                    });

                                    return;

                                }
                            }
                        }
                    }
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceAddress = bluetoothDevice.getAddress();
                    if (mDeviceAddressList.contains(deviceAddress)) {
                        for (Device device : mDeviceList) {
                            if (device.getDeviceAddress().equals(deviceAddress)) {
                                if (device.getDeviceStatus() == 12 || device.getDeviceStatus() == 10) {
                                    return;
                                } else {
                                    if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                                        device.setDeviceConnectStatus(12);
                                    } else if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                                        device.setDeviceConnectStatus(10);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDeviceAdapter.notifyDataSetChanged();
                                        }
                                    });

                                    return;

                                }
                            }
                        }
                    }
                }
//                else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
////                    BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
////                    Message message = new Message();
////                    message.what = 1;
////                    message.obj = intent;
////                    handler.sendMessage(message);
//                }
            }
        };
        //注册广播
        registerReceiver(mReceiver, mFilter);

        mLastConnectSuccessItemPosition = -1;

    }

    @Override
    protected void onStart() {

        super.onStart();
        if (isFirst) {
            initPrintConnect();
            isFirst = false;
        }

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
//                        abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
//                        //3.调用setPin方法进行配对...
//                        Intent obj = (Intent) msg.obj;
//                        BluetoothDevice mBluetoothDevice = obj.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                        boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, "0000");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showProgress() {
        dialog = ProgressDialog.show(PrinterConfirmActivity.this, "", "正在打印....",
                false, false);
    }

    private void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
            case R.id.btn_qx:
                onBackPressed();
                break;
            case R.id.btn_dy:
                if (ListUtils.isEmpty(objectList)) {
                    ToastUtils.showToast("请扫描工单号！");
                    return;
                }
                //断开打印机
                if (mConnectPrinterType != B11_CONNECTED_TYPE && (mConnectPrinterType != B3S_CONNECTED_TYPE)) {
                    if (!alertDialog.isShowing()) {
                        alertDialog.show();
                    } else {
                    }

                } else {
                    printLabel();
                }

                break;
        }

    }

    ProgressDialog show = null;

    /**
     * 打印标签
     */
    private void printLabel() {

        if (mConnectPrinterType == B11_CONNECTED_TYPE) {
            mLPAPI.startJob(30, 40, 0);
            mLPAPI.draw2DQRCode("打印测试", 4, 4, 15);
            boolean isPrintSuccess = mLPAPI.commitJob();

            if (isPrintSuccess) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PrinterConfirmActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

        if (mConnectPrinterType == B3S_CONNECTED_TYPE) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    show = ProgressDialog.show(PrinterConfirmActivity.this, "", "正在打印......", false, false);
                    Looper.loop();
                }
            }).start();

            boolean isPrinting = false;
            mJCAPI.startJob(40, 60, 270, 1);

            if (!isPrinting) {
                mJCAPI.startPage();
                //0码值 1工单号   2品号  3名称  4规格   5项目  6 设备号
//                objectList = new ArrayList<>();
//                objectList.add("0码值");
//                objectList.add("5101-20041401324");
//                objectList.add("20041401324");
//                objectList.add("3名称");
//                objectList.add("4规格");
//                objectList.add("5项目");
//                objectList.add("设备号");
//                objectList.add("7规格");
                //项目号
                mJCAPI.drawText(String.valueOf(objectList.get(5)), 22, 16, 30, 4, 3.5, 0, 1, 0, 0, 90, false, "");
                //设备号
                mJCAPI.drawText(String.valueOf(objectList.get(6)), 22, 47, 30, 4, 3.5, 0, 1, 0, 0, 90, false, "");
                //工单号
                mJCAPI.drawText(String.valueOf(objectList.get(1)), 14.3, 41.5, 35, 4, 3.5, 0, 1, 0, 0, 90, false, "");
                //品号
                mJCAPI.drawText(String.valueOf(objectList.get(2)), 8.8, 41.5, 35, 4, 3.5, 0, 1, 0, 0, 90, false, "");
                //名称
                mJCAPI.drawText(String.valueOf(objectList.get(3)), 3.3, 41.5, 35, 4, 3.5, 0, 1, 0, 0, 90, false, "");
                //规格
                mJCAPI.drawText(String.valueOf(objectList.get(4)), -2.2, 41.5, 35, 4, 3.5, 0, 1, 0, 0, 90, false, "");
//规格明细
                mJCAPI.drawText(String.valueOf(objectList.get(7)), -6.4, 16, 30, 4, 3.5, 0, 1, 0, 0, 90, false, "");
                //打印测试
                mJCAPI.drawQrCode(String.valueOf(objectList.get(0)), 12.8, 4.2, 20, 0);
                mJCAPI.endPage();
                isPrinting = mJCAPI.commitJob(1);

            }
            if (isPrinting) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show.dismiss();
                        Toast.makeText(PrinterConfirmActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            mJCAPI.endJob();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (show.isShowing()) {
                        ToastUtils.showToast("打印失败！");
                        show.dismiss();
                    }
                }
            }, 8000);




        }
    }

    /**
     * @see Activity#onBackPressed() 返回事件 @ add wlg
     */
    @Override
    public void onBackPressed() {
        if (isPrintError) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PrinterConfirmActivity.this);
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

                }
            });
            builder.show();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            finish();
        }

    }

    public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
        /**
         * 设备列表数据
         */
        private List<Device> mDeviceList;

        ItemClickListener mItemClickListener;

        //第二步， 写一个公共的方法
        public void setOnItemClickListener(ItemClickListener listener) {
            this.mItemClickListener = listener;
        }

        public DeviceAdapter(List<Device> deviceList) {
            mDeviceList = deviceList;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView mDeviceName, mDeviceAddress, mDeviceStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mDeviceName = itemView.findViewById(R.id.tv_device_name);
                mDeviceAddress = itemView.findViewById(R.id.tv_device_address);
                mDeviceStatus = itemView.findViewById(R.id.tv_device_status);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            Device device = mDeviceList.get(position);
            holder.mDeviceName.setText(device.getDeviceName());
            holder.mDeviceAddress.setText(device.getDeviceAddress());
            if (device.getDeviceStatus() == BluetoothDevice.BOND_NONE) {
                holder.mDeviceStatus.setText("未配对");

            }


            if (device.getDeviceStatus() == BluetoothDevice.BOND_BONDED) {
                holder.mDeviceStatus.setText("已配对");

            }

            if (device.getDeviceStatus() == 14) {
                holder.mDeviceStatus.setText("已连接");

            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(position);
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return mDeviceList != null ? mDeviceList.size() : 0;
        }


    }

    public interface ItemClickListener {
        public void onItemClick(int position);
    }

}
