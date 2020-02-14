package com.cubertech.bhpda.Activity.Fragment.Hbcz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.IntentIntegrator;
import com.cubertech.bhpda.utils.IntentResult;
import com.cubertech.bhpda.utils.ListUtils;
import com.cubertech.bhpda.utils.ScanActivity;
import com.cubertech.bhpda.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HbczLeftFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HbczLeftFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HbczLeftFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CjczLeftAdapter adapter;
    private RecyclerView cjcz_recycle;
    private AlertDialog.Builder builder;
    private boolean isDialog;
    private int anInt;

    //合并后箱唛头码
    private EditText tm;

    private EditText et_tm;
    private TextView tv_cpph;
    private TextView tv_cppm;
    private TextView tv_cpgg;
    private TextView et_zysl;

    public TKApplication application;

    private SiteService siteService;
    public AccountInfo ai;

    public HbczLeftFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CjczLeftFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HbczLeftFragment newInstance(String param1, String param2) {
        HbczLeftFragment fragment = new HbczLeftFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cjcz_left, container, false);

        cjcz_recycle = (RecyclerView) view.findViewById(R.id.cjcz_recycle);
        cjcz_recycle.setLayoutManager(new LinearLayoutManager(getActivity()));
//        cjcz_recycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new CjczLeftAdapter();
        cjcz_recycle.setAdapter(adapter);

        Bundle bundle = getArguments();//从activity传过来的Bundle
        ai = bundle.getParcelable("account");

        tm = (EditText) view.findViewById(R.id.tm);
        tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if (!tm.getText().toString().trim().startsWith("XMT")) {
                        Toast.makeText(getActivity(), "请扫描箱唛头码！", Toast.LENGTH_LONG).show();
                        tm.setText("");
                        tm.setSelection(0);
                        tm.requestFocus();
                    }
                }
                return false;
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           /* throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private List<Map<String, String>> list = new ArrayList<>();

    public String gettm() {
        return tm.getText().toString().trim();
    }

    public List<Map<String, String>> onDataFinished(View view) {
        List<Map<String, String>> list = adapter.getList();
        List<Map<String, String>> list1 = new ArrayList<>();
        if (ListUtils.isEmpty(list)) {
            return null;
        }
        for (int i = 0; i < ListUtils.getSize(list); i++) {
            Map<String, String> map = list.get(i);
            if (!TextUtils.isEmpty(map.get("tv_cpph"))) {
                list1.add(list.get(i));
            }

        }
        return list1;
    }

    public void onShowDialog(View view) {
        if(TextUtils.isEmpty(tm.getText().toString())){
            ToastUtils.showToast("请先扫描合并后的箱码! ");
            return;
        }

        builder = new AlertDialog.Builder(getActivity());
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_cjcz_left_adapter, null);
        Button btn_xiangji = (Button) view1.findViewById(R.id.del1);
        TextView tv_tm_title = (TextView) view1.findViewById(R.id.listitem_tm_title);
        tv_tm_title.setText("容器/箱码");
        et_tm = (EditText) view1.findViewById(R.id.et_tm);
        tv_cpph = view1.findViewById(R.id.tv_cpph);
        tv_cppm = view1.findViewById(R.id.tv_cppm);
        tv_cpgg = view1.findViewById(R.id.tv_cpgg);
        et_zysl = view1.findViewById(R.id.et_zysl);
        et_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 当actionId == XX_SEND 或者 XX_DONE时都触发
                // 或者event.getKeyCode == ENTER 且 event.getAction ==
                // ACTION_DOWN时也触发
                // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    String[] tms=tm.getText().toString().trim().split("-");

                    if(et_tm.getText().toString().trim().indexOf(tms[1].toString())>=0){
                        scanResultDialog();
                    }else{
                        ToastUtils.showToast("品号不相同请重新扫描");
                    }

                }
                return false;
            }
        });
        btn_xiangji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDialog = true;
                xiangji(view);
            }
        });
        builder.setTitle("扫码添加合箱数据");
        builder.setView(view1);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setTextColor(Color.parseColor("#33b4e4"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(et_tm.getText().toString())) {
                            ToastUtils.showToast("请扫描容器/箱码! ");
                            return;
                        } else if (TextUtils.isEmpty(tv_cpph.getText().toString())) {
                            ToastUtils.showToast("请扫描容器/箱码! ");
                            return;
                        }
                        Map<String, String> map = new HashMap<>();
                        map.put("et_tm", et_tm.getText().toString());
                        map.put("tv_cpph", tv_cpph.getText().toString());
                        map.put("tv_cppm", tv_cppm.getText().toString());
                        map.put("tv_cpgg", tv_cpgg.getText().toString());
                        map.put("et_zysl", et_zysl.getText().toString());
                        list.add(map);

                        adapter.setList(list);
                        cjcz_recycle.scrollToPosition(adapter.getItemCount() - 1);
                        dialog.dismiss();
                    }
                });
                Button button1 = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                button1.setTextColor(Color.parseColor("#33b4e4"));
            }
        });
        dialog.show();
    }

    public void xiangji(View view) {
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        isDialog = true;
        onXiangji();
    }

    public void xiangji1(View view) {
        isDialog = false;
        //Toast.makeText(application, "相机", Toast.LENGTH_SHORT).show();
        onXiangji();
    }

    private void onXiangji() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity())
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class);
        Intent scanIntent = intentIntegrator.createScanIntent();
        startActivityForResult(scanIntent, 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            if (isDialog) {
                et_tm.setText(ScanResult);
                scanResultDialog();
            } else {
                tm.setText(ScanResult);
//                scanResult(ScanResult);


            }
        }
    }

    private void scanResult(String et_tm) {
        Map<String, String> map = (Map<String, String>) list.get(anInt);
        map.put("et_tm", et_tm);
        map.put("tv_cpph", "直接填写扫描结果");
        map.put("tv_cppm", "直接填写扫描结果");
        map.put("tv_cpgg", "直接填写扫描结果");
        map.put("et_zysl", "直接填写扫描结果");

        adapter.setList(list);
    }

    private void scanResultDialog() {
        /*tv_cpph.setText("cpph" + adapter.getItemCount());
        tv_cppm.setText("cppm" + adapter.getItemCount());
        tv_cpgg.setText("cpgg" + adapter.getItemCount());
        et_zysl.setText("number" + adapter.getItemCount());*/
        String tm = et_tm.getText().toString().trim();
        if (tm.equals("")) {
            Toast.makeText(getActivity(), "请先扫描", Toast.LENGTH_LONG).show();
            return;
        }
        if(tm.startsWith("TMT")){
            Toast.makeText(getActivity(), "不能操作托盘码！", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("code", tm);
        params.put("database", ai.getData());
        params.put("strToken", "");
        params.put("strVersion", "");
        params.put("strPoint", "");
        params.put("strActionType", "1001");
        siteService = SiteService.getInstants();
        //通过Application传值
        String IP = "";
        String port = "";
        if (application != null && application.getUrl() != null && !application.getUrl().equals("")) {
            IP = application.getUrl();
            port = application.getPort();
        }
        //先弄一个观察者
        Observable<List<Object>> observable = siteService.getHX(params,
                getActivity(), IP, port);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                siteService.closeParentDialog();
            }

            @Override
            public void onError(Throwable e) {
                siteService.closeParentDialog();
                et_tm.setText("");
                et_tm.setSelection(0);
                et_tm.requestFocus();
                Toast.makeText(getActivity(), "" + e.toString().replace("java.lang.Exception:", ""), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(List<Object> obj) {
                if (obj == null || obj.size() <= 0) {
                    return;
                }
                /*tv_cpph.setText("cpph" + adapter.getItemCount());
                  tv_cppm.setText("cppm" + adapter.getItemCount());
                  tv_cpgg.setText("cpgg" + adapter.getItemCount());
                  et_zysl.setText("number" + adapter.getItemCount());*/
                tv_cpph.setText(obj.get(0).toString());//[0]品号
                tv_cppm.setText(obj.get(1).toString());//[1]品名
                tv_cpgg.setText(obj.get(2).toString());//[2]规格
                et_zysl.setText("");//[3]数量

            }

        });
    }

    public class CjczLeftAdapter extends RecyclerView.Adapter<CjczLeftAdapter.ViewHolder> {
        private List<Map<String, String>> list = new ArrayList<>();

        public CjczLeftAdapter() {
        }

        public void setList(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<Map<String, String>> getList() {
            return list;
        }

        public CjczLeftAdapter(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_cjcz_left_adapter, null, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.et_tm.setFocusable(false);
            holder.et_tm.setFocusableInTouchMode(false);
            holder.xiangji.setBackgroundResource(R.mipmap.ic_delete);
            holder.xiangji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("提示");
                    builder.setMessage("确定要删除该条数据么?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
//                    isDialog = false;
//                    anInt = position;
//                    xiangji(view);

                }
            });
            holder.tv_tm_title.setText("容器/箱码");
            Map<String, String> o = (Map<String, String>) list.get(position);
            holder.et_tm.setText(o.get("et_tm"));
            holder.tv_cpph.setText(o.get("tv_cpph"));
            holder.tv_cppm.setText(o.get("tv_cppm"));
            holder.tv_cpgg.setText(o.get("tv_cpgg"));
            holder.et_zysl.setText(o.get("et_zysl"));
            //设置成只读
            holder.et_zysl.setCursorVisible(false);
            holder.et_zysl.setFocusable(false);
            holder.et_zysl.setFocusableInTouchMode(false);
//            mEditText.setCursorVisible(false);
//            mEditText.setFocusable(false);
//            mEditText.setFocusableInTouchMode(false);
            holder.et_tm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // 当actionId == XX_SEND 或者 XX_DONE时都触发
                    // 或者event.getKeyCode == ENTER 且 event.getAction ==
                    // ACTION_DOWN时也触发
                    // 注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//                        Map<String, String> map = (Map<String, String>) list.get(position);
//                        map.put("et_tm", holder.et_tm.getText().toString());
                        anInt = position;
                        scanResult(holder.et_tm.getText().toString());
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private Button xiangji;
            private EditText et_tm;
            private TextView tv_cpph;
            private TextView tv_cppm;
            private TextView tv_cpgg;
            private EditText et_zysl;

            private TextView tv_tm_title;

            public ViewHolder(View itemView) {
                super(itemView);
                xiangji = (Button) itemView.findViewById(R.id.del1);
                et_tm = (EditText) itemView.findViewById(R.id.et_tm);
                tv_cpph = itemView.findViewById(R.id.tv_cpph);
                tv_cppm = itemView.findViewById(R.id.tv_cppm);
                tv_cpgg = itemView.findViewById(R.id.tv_cpgg);
                et_zysl = itemView.findViewById(R.id.et_zysl);
                tv_tm_title = (TextView) itemView.findViewById(R.id.listitem_tm_title);
            }
        }
    }
}
