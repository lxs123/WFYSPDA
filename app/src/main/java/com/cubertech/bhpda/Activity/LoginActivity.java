package com.cubertech.bhpda.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.connect.retrofits.ServiceUtils;
import com.cubertech.bhpda.update.UpdateManager;
import com.cubertech.bhpda.utils.CommonUtil;
import com.cubertech.bhpda.utils.PermissionUtils;
import com.cubertech.bhpda.utils.ToastUtils;
import com.cubertech.bhpda.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class LoginActivity extends AppCompatActivity {
    private SiteService siteService;
    private EditText e_name, e_pwd;
    private TextView tv_data;
    ServiceUtils su = ServiceUtils.getInstance();
    private AccountInfo ai;
    private PermissionUtils permissionUtils;
    private List<String> list;

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#315CAB"));
        }
        e_name = (EditText) findViewById(R.id.login_name_edit);
        e_pwd = (EditText) findViewById(R.id.login_pwd_edit);
        tv_data = (TextView) findViewById(R.id.login_data_tv);
        permissionUtils = new PermissionUtils(LoginActivity.this);
        list = new ArrayList<>();
        list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        list.add(Manifest.permission.INTERNET);
        permissionUtils.request(list, 100, new PermissionUtils.CallBack() {
            @Override
            public void grantAll() {

            }

            @Override
            public void denied() {
                ToastUtils.showToast("请求权限失败！");
            }
        });
        SharedPreferences sp = LoginActivity.this.getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        String name = sp.getString("name", null);
        String pwd = sp.getString("pwd", null);
        String data = sp.getString("data", null);
        //Toast.makeText(LoginActivity.this, "name:"+name+" pwd:"+pwd, Toast.LENGTH_SHORT).show();
        if (name != null && !name.equals("") && pwd != null && !pwd.equals("")) {
            e_name.setText(name);
            e_pwd.setText(pwd);
        }
        if (data != null && !data.equals("")) {
            tv_data.setText(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 直接登录直接获取
     * 账套数据
     */
    public void logindata(String name, String pwd, String data, String IP, String port) {


        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pwds", String.valueOf(e_pwd.getText().toString().trim()));
        params.put("account", String.valueOf(e_name.getText().toString().trim()));
        params.put("strToken", "");
        params.put("strVersion", ai.getVersionCode());
        params.put("strPoint", "");
        params.put("strActionType", "1001");
//        params.put("strUserName", name);
//        params.put("strPassword", pwd);
//        params.put("data", data);
//        params.put("strToken", "");
//        params.put("strVersion", "");
//        params.put("strPoint", "");
//        params.put("strActionType", "1001");
        su.callService(LoginActivity.this, "login", params, true, new ServiceUtils.ServiceCallBack() {
            @Override
            public void onResponse(Object o) {
                List<Object> oo = (List<Object>) o;
                loginMain(name, data, oo.toString());
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFailure(String str) {
                if (str.contains("版本不正确")) {
                    permissionUtils.request(list, 100, new PermissionUtils.CallBack() {
                        @Override
                        public void grantAll() {
                            UpdateManager.getInstance().init(LoginActivity.this).check();
                        }

                        @Override
                        public void denied() {
                            ToastUtils.showToast("请求权限失败！");
                        }
                    });

                }

                CommonUtil.showErrorDialog(LoginActivity.this, str);
                su.closeParentDialog();//必须
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                su.closeParentDialog();//必须
            }

            @Override
            public void onCompleted() {
                su.closeParentDialog();//必须
            }
        });

//        siteService = SiteService.getInstants();
//        //通过Application传值//此处不用实现
//       /* if(application.getUrl()!=null&&!application.getUrl().equals("")){
//            String IP="";
//            String port="";
//        }*/
//        //先弄一个观察者
//        Observable<String> observable = siteService.logindata(params,
//                LoginActivity.this, IP, port);
//        //一个订阅者
//        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
//            @Override
//            public void onCompleted() {
//                siteService.closeParentDialog();
//                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                siteService.closeParentDialog();
//                Toast.makeText(LoginActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNext(String str) {
//                if (!str.equals("")) {
//                    //Toast.makeText(LoginActivity.this, "权限："+str, Toast.LENGTH_SHORT).show();
//                    //getData(str);
//                    loginMain(name, data, str);
//
//                }
//            }
//        });
    }

    /**
     * 登录
     *
     * @param v
     */
    public void login(View v) {
        /*startActivity(new Intent(this, MainActivity.class));
        return;*/
        final String name = e_name.getText().toString().trim();
        if (name.length() == 0) {
            Toast.makeText(LoginActivity.this, "登录名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String pwd = e_pwd.getText().toString().trim();
        if (pwd.length() == 0) {
            Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String tvdata = tv_data.getText().toString().trim();
//        if (tvdata.length() == 0) {
//            Toast.makeText(LoginActivity.this, "请选择账套", Toast.LENGTH_SHORT).toString();
//            return;
//        }

        SharedPreferences sp = LoginActivity.this.getSharedPreferences(
                "config", 0);

        String IP = sp.getString("IP", null);
        String port = sp.getString("port", null);


        if (IP == null || "".equals(IP) || port == null
                || "".equals(port)) {
            CommonUtil.showInfoDialog(this, "设置信息不全");
            return;
        }

        SharedPreferences.Editor edit = sp.edit();
        edit.putString("name", String.valueOf(e_name.getText().toString().trim())
                .trim());
        edit.putString("pwd", String.valueOf(e_pwd.getText().toString().trim())
                .trim());
        edit.putString("data", String.valueOf(tv_data.getText().toString().trim()));
        edit.commit();

        //添加到application中
        TKApplication application = (TKApplication) getApplication();
        application.setUrl(IP);
        application.setPort(port);
        application.setUserName(String.valueOf(e_name.getText().toString().trim()));
        ai = new AccountInfo();
        ai.setName(name);
        ai.setVersionCode(Utils.getVersions(this));//版本
        //必验证，需要得到账套中的权限数据
        logindata(name, pwd, tvdata, IP, port);
    }

    /**
     * 登录主页面
     *
     * @param name       用户名
     * @param data       账套
     * @param permission 权限字符串
     */
    private void loginMain(String name, String data, String permission) {
        //用户，账套都传入
        AccountInfo ai = new AccountInfo();
        ai.setName(name);//用户名
        ai.setData(data);//账套
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", ai);
        bundle.putCharSequence("permission", permission);//权限
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(LoginActivity.this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
        //startActivity(new Intent(this, MainActivity.class));
    }

    public void test() {
        /*SharedPreferences sp = LoginActivity.this.getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        String IP = sp.getString("IP", null);
        String port = sp.getString("port", null);


        if (IP == null || "".equals(IP) || port == null
                || "".equals(port)) {
            CommonUtil.showInfoDialog(this, "设置信息不全");
            return;
        }


        List<Object> PDAZYMX = new ArrayList<Object>();
        PDAZYMX.add("1111");
        HashMap<String, Object> params = new HashMap<String, Object>();

        *//*params.put("strUserName", name);
        params.put("strPassword", pwd);*//*
        params.put("database", "zmwl");
        params.put("PDAZYMX", PDAZYMX);
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //先弄一个观察者
        Observable<String> observable = siteService.getgxzy(params,
                LoginActivity.this, IP, port);


        //一个订阅者
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(LoginActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String str) {
                if (!str.equals("")) {
                    Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();

                }
            }
        });*/

    }

    /**
     * 选择账套
     */
    public void choice(View v) {
        //判断配置不能为空
        //判断用户名密码不能为空
        final String name = e_name.getText().toString().trim();
        if (name.length() == 0) {
            Toast.makeText(LoginActivity.this, "登录名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String pwd = e_pwd.getText().toString().trim();
        if (pwd.length() == 0) {
            Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sp = LoginActivity.this.getSharedPreferences(
                "config", Activity.MODE_PRIVATE);

        String IP = sp.getString("IP", null);
        String port = sp.getString("port", null);


        if (IP == null || "".equals(IP) || port == null
                || "".equals(port)) {
            CommonUtil.showInfoDialog(this, "设置信息不全");
            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("strUserName", name);
        params.put("strPassword", pwd);
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //先弄一个观察者
        Observable<String> observable = siteService.loginString(params,
                LoginActivity.this, IP, port);
        //一个订阅者
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
                //Toast.makeText(LoginActivity.this, "  结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                Toast.makeText(LoginActivity.this, "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String str) {
                if (!str.equals("")) {
                    //Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                    getData(str);
                }
            }
        });
    }


    int yourChoice = -1;

    private void getData(String datas) {
        //final String[] items = { "我是1","我是2","我是3","我是4" };
        final String[] items = datas.split(",");
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(LoginActivity.this, R.style.MP_Theme_alertDialog);
        singleChoiceDialog.setTitle("选择账套");
        int Choice = 0;
        final String tvdata = tv_data.getText().toString().trim();
        if (tvdata.length() == 0) {
            Choice = -1;
        } else {
            for (int i = 0; i < items.length; i++) {
                String ite = items[i];
                if (ite.equals(tvdata)) {
                    Choice = i;
                }
            }
        }
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, Choice,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            tv_data.setText(String.valueOf(items[yourChoice]));
                        }
                    }
                });
        singleChoiceDialog.show();
    }


    /**
     * 配置
     *
     * @param v
     */
    public void btnconfig(View v) {
        LayoutInflater flater = getLayoutInflater();
        final View view = flater.inflate(R.layout.config,
                (ViewGroup) findViewById(R.id.dialog));
        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this, R.style.MP_Theme_alertDialog);
        dialog.setTitle("配置");

        EditText Edit_url = (EditText) view.findViewById(R.id.Edit_url);
        EditText Edit_port = (EditText) view.findViewById(R.id.Edit_port);

        SharedPreferences sp = LoginActivity.this.getSharedPreferences(
                "config", Activity.MODE_PRIVATE);
        String url = sp.getString("IP", null);
        String port = sp.getString("port", null);

        Edit_url.setText(url);
        Edit_port.setText(port);

        dialog.setView(view);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    EditText Edit_url = (EditText) view
                            .findViewById(R.id.Edit_url);
                    EditText Edit_port = (EditText) view
                            .findViewById(R.id.Edit_port);

                    SharedPreferences sp = LoginActivity.this
                            .getSharedPreferences("config",
                                    Activity.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();

                    edit.putString("IP", String.valueOf(Edit_url.getText())
                            .trim());
                    edit.putString("port", String.valueOf(Edit_port.getText())
                            .trim());

                    edit.commit();
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "输入不正确！",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    //dialog.cancel();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //dialog.cancel();
            }
        });
        dialog.show();
    }
}
