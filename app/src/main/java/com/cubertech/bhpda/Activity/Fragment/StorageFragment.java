package com.cubertech.bhpda.Activity.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cubertech.bhpda.Activity.ChtzdActivity;
import com.cubertech.bhpda.Activity.InStorageActivity;
import com.cubertech.bhpda.Activity.KwbdActivity;
import com.cubertech.bhpda.Activity.OutStorageActivity;
import com.cubertech.bhpda.Activity.PdTestActivity;
import com.cubertech.bhpda.Activity.PickingActivity;
import com.cubertech.bhpda.Activity.StoragePackage.CgjhActivity;
import com.cubertech.bhpda.Activity.WlcxActivity;
import com.cubertech.bhpda.Activity.ZycxActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.Utils;

/**
 * 入库界面
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StorageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StorageFragment extends Fragment {
    private SiteService siteService;
    private Context mContext;
    private Activity mActivity;


    TKApplication application;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_DATA = "DATA";
    private static final String ARG_NAME = "Name";
    private static final String ARG_PER = "PER";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String mData;//账套
    private String mName;//用户登录名
    private String mPer;//权限

    private OnFragmentInteractionListener mListener;
    private View Iview;

    public StorageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StorageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StorageFragment newInstance(String param1, String param2,
                                              String datas, String names, String per) {
        StorageFragment fragment = new StorageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        args.putString(ARG_DATA, datas);//账套
        args.putString(ARG_NAME, names);//用户登录名
        args.putString(ARG_PER, per);//权限
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        mActivity = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            mData = getArguments().getString(ARG_DATA);//账套
            mName = getArguments().getString(ARG_NAME);//用户登录名
            mPer = getArguments().getString(ARG_PER);//权限


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Iview = inflater.inflate(R.layout.fragment_storage, container, false);
        initView(Iview);
        return Iview;
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
            /*throw new RuntimeException(context.toString()
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

    LinearLayout /*ly_cgjh,ly_cgth,ly_jhjy,ly_ll,ly_tl,ly_db,ly_scrk,ly_cpxh,ly_cpth,ly_kcjy,*/ly_zycx, ly_pd, ly_rk, ly_ck, ly_kwbd, ly_wlcx;
    LinearLayout /*cgjh,cgth,jhjy,ll,tl,db,scrk,cpxh,cpth,kcjy,*/pd, rk, ck, kwbd, wlcx, zycx;
    LinearLayout lld, ly_lld, ly_chtzd, chtzd;

    private void initView(View iview) {
       /* ly_cgjh=(LinearLayout)iview.findViewById(R.id.ly_cgjh);
        ly_cgth=(LinearLayout)iview.findViewById(R.id.ly_cgth);
        ly_jhjy=(LinearLayout)iview.findViewById(R.id.ly_jhjy);
        ly_ll=(LinearLayout)iview.findViewById(R.id.ly_ll);
        ly_tl=(LinearLayout)iview.findViewById(R.id.ly_tl);
        ly_db=(LinearLayout)iview.findViewById(R.id.ly_db);
        ly_scrk=(LinearLayout)iview.findViewById(R.id.ly_scrk);
        ly_cpxh=(LinearLayout)iview.findViewById(R.id.ly_cpxh);
        ly_cpth=(LinearLayout)iview.findViewById(R.id.ly_cpth);
        ly_kcjy=(LinearLayout)iview.findViewById(R.id.ly_kcjy);*/

        ly_rk = (LinearLayout) iview.findViewById(R.id.ly_rk);
        ly_ck = (LinearLayout) iview.findViewById(R.id.ly_ck);
        ly_pd = (LinearLayout) iview.findViewById(R.id.ly_pd);
        ly_kwbd = (LinearLayout) iview.findViewById(R.id.ly_kwbd);
        ly_wlcx = (LinearLayout) iview.findViewById(R.id.ly_wlcx);
        ly_zycx = (LinearLayout) iview.findViewById(R.id.ly_mtdy);
        zycx = (LinearLayout) iview.findViewById(R.id.mtdy);
        ly_lld = (LinearLayout) iview.findViewById(R.id.ly_lld);
        lld = (LinearLayout) iview.findViewById(R.id.lld);
        ly_chtzd = (LinearLayout) iview.findViewById(R.id.ly_chtzd);
        chtzd = (LinearLayout) iview.findViewById(R.id.chtzd);
        //mPer = "PDACGJHPDACGTHPDAJHJYPDALLPDATLPDADBPDASCRKPDACPXHPDACPTHPDAKCJYPDAPDPDARKPDACKPDAKWBDPDAWLCX";

        /*if(mPer.indexOf("PDACGJH")>=0){
            ly_cgjh.setVisibility(View.VISIBLE);//显示采购进货1
        }
        if(mPer.indexOf("PDACGTH")>=0){
            ly_cgth.setVisibility(View.VISIBLE);//显示采购退货2
        }
        if(mPer.indexOf("PDAJHJY")>=0){
            ly_jhjy.setVisibility(View.VISIBLE);//显示进货检验3
        }
        if(mPer.indexOf("PDALL")>=0){
            ly_ll.setVisibility(View.VISIBLE);//显示领料4
        }
        if(mPer.indexOf("PDATL")>=0){
            ly_tl.setVisibility(View.VISIBLE);//显示退料5
        }
        if(mPer.indexOf("PDADB")>=0){
            ly_db.setVisibility(View.VISIBLE);//显示调拨6
        }
        if(mPer.indexOf("PDASCRK")>=0){
            ly_scrk.setVisibility(View.VISIBLE);//显示生产入库7
        }
        if(mPer.indexOf("PDACPXH")>=0){
            ly_cpxh.setVisibility(View.VISIBLE);//显示成品销货8
        }
        if(mPer.indexOf("PDACPTH")>=0){
            ly_cpth.setVisibility(View.VISIBLE);//显示成品退货9
        }
        if(mPer.indexOf("PDAKCJY")>=0){
            ly_kcjy.setVisibility(View.VISIBLE);//显示库存交易10
        }*/
        Log.e("#####", mPer);
        if (mPer.indexOf("PDAPD") >= 0) {
            ly_pd.setVisibility(View.VISIBLE);//显示盘点11
        }
        if (mPer.indexOf("PDARK") >= 0) {
            ly_rk.setVisibility(View.VISIBLE);//显示烟台未来入库
        }
        if (mPer.indexOf("PDACK") >= 0) {
            ly_ck.setVisibility(View.VISIBLE);//显示烟台未来出库
        }
        if (mPer.indexOf("PDAKWBD") >= 0) {
            ly_kwbd.setVisibility(View.VISIBLE);//显示烟台未来库位绑定
        }
        if (mPer.indexOf("PDAWLCX") >= 0) {
            ly_wlcx.setVisibility(View.VISIBLE);//显示烟台未来物料查询
        }
        if (mPer.indexOf("PDAZYCX") >= 0) {
            ly_zycx.setVisibility(View.VISIBLE);//唛头打印  烟台未来 作业查询
        }


        /*cgjh=(LinearLayout) iview.findViewById(R.id.cgjh);//采购进货
        cgjh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CgjhActivity.class);
                mContext.startActivity(intent);
            }
        });
        cgth=(LinearLayout) iview.findViewById(R.id.cgth);//采购退货
        cgth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"采购退货",Toast.LENGTH_SHORT).show();
            }
        });
        jhjy=(LinearLayout) iview.findViewById(R.id.jhjy);//进货检验
        jhjy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"进货检验",Toast.LENGTH_SHORT).show();
            }
        });
        ll=(LinearLayout) iview.findViewById(R.id.ll);//领料
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"领料",Toast.LENGTH_SHORT).show();
            }
        });
        tl=(LinearLayout) iview.findViewById(R.id.tl);//退料
        tl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"退料",Toast.LENGTH_SHORT).show();
            }
        });
        db=(LinearLayout) iview.findViewById(R.id.db);//调拨
        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"调拨",Toast.LENGTH_SHORT).show();
            }
        });
        scrk=(LinearLayout) iview.findViewById(R.id.scrk);//生产入库
        scrk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"生产入库",Toast.LENGTH_SHORT).show();
            }
        });
        cpxh=(LinearLayout) iview.findViewById(R.id.cpxh);//成品销货
        cpxh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"成品销货",Toast.LENGTH_SHORT).show();
            }
        });
        cpth=(LinearLayout) iview.findViewById(R.id.cpth);//成品退货
        cpth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"成品退货",Toast.LENGTH_SHORT).show();
            }
        });
        kcjy=(LinearLayout) iview.findViewById(R.id.kcjy);//库存交易
        kcjy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"库存交易",Toast.LENGTH_SHORT).show();
            }
        });*/

        rk = (LinearLayout) iview.findViewById(R.id.rk);//烟台未来入库
        rk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //完工入库 烟台未来出库
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                ai.setVersionCode(Utils.getVersions(getActivity()));
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, InStorageActivity.class);   //烟台未来入库
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        ck = (LinearLayout) iview.findViewById(R.id.ck);//烟台未来出库
        ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, OutStorageActivity.class); //烟台未来出库
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        pd = (LinearLayout) iview.findViewById(R.id.pd);//盘点 烟台未来盘点
        pd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
//                intent.setClass(mContext, ZjActivity.class);
                intent.setClass(mContext, PdTestActivity.class); //烟台未来盘点
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                //Toast.makeText(mContext,"盘点",Toast.LENGTH_SHORT).show();
            }
        });
        kwbd = (LinearLayout) iview.findViewById(R.id.kwbd);//烟台未来库位绑定
        kwbd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, KwbdActivity.class); //烟台未来库位绑定
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        wlcx = (LinearLayout) iview.findViewById(R.id.wlcx);//烟台未来物料查询
        wlcx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, WlcxActivity.class);//烟台未来物料查询
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        zycx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, ZycxActivity.class);//箱唛头打印
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        lld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, PickingActivity.class);//领料通知单
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        chtzd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, ChtzdActivity.class);//出货通知单
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }
}
