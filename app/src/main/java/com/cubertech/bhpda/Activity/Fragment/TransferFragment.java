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

import com.cubertech.bhpda.Activity.InStorageActivity;
import com.cubertech.bhpda.Activity.KwbdActivity;
import com.cubertech.bhpda.Activity.OutStorageActivity;
import com.cubertech.bhpda.Activity.PdTestActivity;
import com.cubertech.bhpda.Activity.RelevancePackage.GlgxjlActivity;
import com.cubertech.bhpda.Activity.TransferPackage.CjczActivity;
import com.cubertech.bhpda.Activity.TransferPackage.FgrkActivity;
import com.cubertech.bhpda.Activity.TransferPackage.FgzyActivity;
import com.cubertech.bhpda.Activity.TransferPackage.FzActivity;
import com.cubertech.bhpda.Activity.TransferPackage.GxzyActivity;
import com.cubertech.bhpda.Activity.TransferPackage.GxzySdwlActivity;
import com.cubertech.bhpda.Activity.TransferPackage.HbczActivity;
import com.cubertech.bhpda.Activity.TransferPackage.NewXhczActivity;
import com.cubertech.bhpda.Activity.TransferPackage.RqmdyActivity;
import com.cubertech.bhpda.Activity.TransferPackage.SjczActivity;
import com.cubertech.bhpda.Activity.TransferPackage.TpmtdyActivity;
import com.cubertech.bhpda.Activity.TransferPackage.WgrkActivity;
import com.cubertech.bhpda.Activity.TransferPackage.XTmtfzActivity;
import com.cubertech.bhpda.Activity.TransferPackage.XhczActivity;
import com.cubertech.bhpda.Activity.TransferPackage.XmtdyActivity;
import com.cubertech.bhpda.Activity.TransferPackage.YkczActivity;
import com.cubertech.bhpda.Activity.TransferPackage.ZjActivity;
import com.cubertech.bhpda.Activity.WlcxActivity;
import com.cubertech.bhpda.Activity.ZycxActivity;
import com.cubertech.bhpda.Activity.wfys.BjplActivity;
import com.cubertech.bhpda.Activity.wfys.BjrkActivity;
import com.cubertech.bhpda.Activity.wfys.DdxhActivity;
import com.cubertech.bhpda.Activity.wfys.KccxActivity;
import com.cubertech.bhpda.Activity.wfys.RkjyActivity;
import com.cubertech.bhpda.Activity.wfys.XjActivity;
import com.cubertech.bhpda.Activity.wfys.YsGlgxjlActivity;
import com.cubertech.bhpda.Activity.wfys.cgrk.CgrkActivity;
import com.cubertech.bhpda.Activity.wfys.db.KjdbActivity;
import com.cubertech.bhpda.Activity.wfys.db.KndbActivity;
import com.cubertech.bhpda.Activity.wfys.dhys.DhysActivity;
import com.cubertech.bhpda.Activity.wfys.gx.GxjyActivity;
import com.cubertech.bhpda.Activity.wfys.picking.ApplyPickingActivity;
import com.cubertech.bhpda.Activity.wfys.picking.FyPickingActivity;
import com.cubertech.bhpda.Activity.wfys.picking.PickingScreenActivity;
import com.cubertech.bhpda.Activity.wfys.picking.PickingScreenFinishActivity;
import com.cubertech.bhpda.Activity.wfys.wgrk.BjWgrkActivity;
import com.cubertech.bhpda.Activity.wfys.wgrk.OtherWgrkActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;
import com.cubertech.bhpda.utils.SQLiteUtils;
import com.cubertech.bhpda.utils.Utils;

/**
 * 转移界面
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends Fragment {
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

    public TransferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransferFragment newInstance(String param1, String param2,
                                               String datas, String names, String per) {
        TransferFragment fragment = new TransferFragment();
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
        Iview = inflater.inflate(R.layout.fragment_transfer, container, false);
        initView(Iview);
        SQLiteUtils instance = SQLiteUtils.getInstance(getActivity());
        instance.getStatus();
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

    LinearLayout ly_gxzy, ly_fgzy, ly_wgrk, ly_fgrk, ly_zj, ly_rqmdy, ly_fz, ly_mtdy, ly_tpmtdy, ly_cjcz,
            ly_hbcz, ly_sjcz, ly_xhcz, ly_ykcz, ly_mtfz, ly_bjpl, ly_glgxjl;
    LinearLayout gxzy, fgzy, wgrk, fgrk, zj, rqmdy, fz, mtdy, tpmtdy, cjcz, hbcz, sjcz, xhcz,
            ykcz, mtfz, bjpl, glgxjl;

    private void initView(View iview) {
        ly_gxzy = (LinearLayout) iview.findViewById(R.id.ly_gxzy);
        ly_fgzy = (LinearLayout) iview.findViewById(R.id.ly_fgzy);
        ly_wgrk = (LinearLayout) iview.findViewById(R.id.ly_wgrk);
        ly_fgrk = (LinearLayout) iview.findViewById(R.id.ly_fgrk);
        ly_zj = (LinearLayout) iview.findViewById(R.id.ly_zj);
        ly_rqmdy = (LinearLayout) iview.findViewById(R.id.ly_rqmdy);
        ly_fz = (LinearLayout) iview.findViewById(R.id.ly_fz);
        ly_mtdy = (LinearLayout) iview.findViewById(R.id.ly_mtdy);
        ly_tpmtdy = (LinearLayout) iview.findViewById(R.id.ly_tpmtdy);
        mtdy = (LinearLayout) iview.findViewById(R.id.mtdy);
        ly_hbcz = (LinearLayout) iview.findViewById(R.id.ly_hbcz);
        ly_cjcz = (LinearLayout) iview.findViewById(R.id.ly_cjcz);
        ly_sjcz = (LinearLayout) iview.findViewById(R.id.ly_sjcz);
        ly_xhcz = (LinearLayout) iview.findViewById(R.id.ly_xhcz);
        ly_ykcz = (LinearLayout) iview.findViewById(R.id.ly_ykcz);
        ly_mtfz = (LinearLayout) iview.findViewById(R.id.ly_mtfz);
        ly_bjpl = (LinearLayout) iview.findViewById(R.id.ly_bjpl);
        ly_glgxjl = (LinearLayout) iview.findViewById(R.id.ly_glgxjl);
        //mPer = "PDAGXZYPDAFGZYPDAWGRKPDAFGRKPDAGXZJPDARQMDYPDARQMFZPDAMTDYPDATPMTDYPDACJCZPDAHBCZ"
        //       + "PDASJCZPDAXHCZPDASJCZPDAMTFZPDAMTFZ";//显示全部的值
        //修改显示的值
        Log.e("#####", mPer);
//        mPer = "PDAGXZY,PDAGXJY,PDARKJY,PDACGRK,PDADHYS,PDALL,PDABJRK,PDAWGRK,PDADDXH";

        if (mPer.indexOf("PDAGXZY") >= 0) {
            ly_gxzy.setVisibility(View.VISIBLE);//==》潍坊雅士显示工序转移
        }
        if (mPer.indexOf("PDAGXJY") >= 0) {
            ly_fgzy.setVisibility(View.VISIBLE);//==》 潍坊雅士显示工序检验
        }

        if (mPer.indexOf("PDARKJY") >= 0) {
            ly_fgrk.setVisibility(View.VISIBLE);//显示返工入库  ==》 潍坊雅士  入库巡检
        }
        if (mPer.indexOf("PDACGRK") >= 0) {
            ly_zj.setVisibility(View.VISIBLE);//显示质检 ==》 潍坊雅士   采购入库
        }
//        if (mPer.indexOf("PDADHYS") >= 0) {
        ly_rqmdy.setVisibility(View.VISIBLE);//容器码打印 ==》 潍坊雅士 到货验收
//        }
        if (mPer.indexOf("PDALL") >= 0) {
            ly_fz.setVisibility(View.VISIBLE);//容器码复制==》 潍坊雅士 工单领料
        }

        if (mPer.indexOf("PDABJRK") >= 0) {
            ly_tpmtdy.setVisibility(View.VISIBLE);//托盘唛头打印  ==》潍坊雅士  钣金入库
        }
//        if (mPer.indexOf("PDACJCZ") >= 0) {
        ly_cjcz.setVisibility(View.GONE);//拆解操作 潍坊雅士 ==》 钣金完工入库(暂不显示)
//        }
        if (mPer.indexOf("PDAWGRK") >= 0) {
            ly_hbcz.setVisibility(View.VISIBLE);//合并操作  ==》潍坊雅士  完工入库-其他
        }

        if (mPer.indexOf("PDADDXH") >= 0) {
            ly_xhcz.setVisibility(View.VISIBLE);//销货操作  ==》潍坊雅士 订单销货
        }
        if (mPer.indexOf("PDAKNDB") >= 0) {
            ly_cjcz.setVisibility(View.VISIBLE);
        }
        if (mPer.indexOf("PDAXJ") >= 0) {
            ly_sjcz.setVisibility(View.VISIBLE);//巡检
        }

        if (mPer.indexOf("PDAFYLL") >= 0) {
            ly_mtdy.setVisibility(View.VISIBLE);//唛头打印 ==》 潍坊雅士 费用领料
        }
        if (mPer.indexOf("PDAKCCX") >= 0) {
            ly_ykcz.setVisibility(View.VISIBLE);//移库操作
        }
        ////////////////////////////////////////////////////////////    以下暂时不用
        ly_mtfz.setVisibility(View.VISIBLE);//唛头复制

        ly_bjpl.setVisibility(View.VISIBLE);

        ly_glgxjl.setVisibility(View.VISIBLE);

        if (mPer.indexOf("PDAQDPD") >= 0) {

        }

//        if (mPer.indexOf("PDAWGRK") >= 0) {
//            ly_wgrk.setVisibility(View.VISIBLE);//显示完工入库 ==》烟台未来入库使用
//        }

        gxzy = (LinearLayout) iview.findViewById(R.id.gxzy);
        gxzy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //工序转移按钮事件
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, GxzySdwlActivity.class);
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        fgzy = (LinearLayout) iview.findViewById(R.id.fgzy);
        fgzy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返工转移
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, GxjyActivity.class);//工序检验
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                /*Intent intent=new Intent(mContext, FgzyActivity.class);
                mContext.startActivity(intent);*/
            }
        });

        wgrk = (LinearLayout) iview.findViewById(R.id.wgrk);
        wgrk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(mContext, WgrkActivity.class);
                mContext.startActivity(intent);*/
                //完工入库 烟台未来出库
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                ai.setVersionCode(Utils.getVersions(getActivity()));
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
//                intent.setClass(mContext, WgrkActivity.class);
                intent.setClass(mContext, InStorageActivity.class);   //烟台未来入库
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        fgrk = (LinearLayout) iview.findViewById(R.id.fgrk);
        fgrk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(mContext, FgrkActivity.class);
                mContext.startActivity(intent);*/
                //返工入库
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
//                intent.setClass(mContext, FgrkActivity.class);
                intent.setClass(mContext, RkjyActivity.class); //烟台未来  入库巡检
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        zj = (LinearLayout) iview.findViewById(R.id.zj);
        zj.setOnClickListener(new View.OnClickListener() {
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
                intent.setClass(mContext, CgrkActivity.class); //烟台未来采购入库
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                /*Intent intent=new Intent(mContext, ZjActivity.class);
                mContext.startActivity(intent);*/
            }
        });
        rqmdy = (LinearLayout) iview.findViewById(R.id.rqmdy);
        rqmdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                //intent.setClass(mContext, RqmdyActivity.class);//容器码打印界面
                intent.setClass(mContext, DhysActivity.class); //烟台未来到货验收
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        fz = (LinearLayout) iview.findViewById(R.id.fz);
        fz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                //intent.setClass(mContext, FzActivity.class);//容器码打印界面
                intent.setClass(mContext, PickingScreenFinishActivity.class);//烟台未来工单领料
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        mtdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, FyPickingActivity.class);//潍坊雅士 费用领料
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        tpmtdy = (LinearLayout) iview.findViewById(R.id.tpmtdy);
        tpmtdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, BjrkActivity.class);//钣金入库
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        cjcz = (LinearLayout) iview.findViewById(R.id.cjcz);
        cjcz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, KndbActivity.class);//雅士 库内调拨
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        hbcz = (LinearLayout) iview.findViewById(R.id.hbcz);
        hbcz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, OtherWgrkActivity.class);//完工入库-其他
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        sjcz = (LinearLayout) iview.findViewById(R.id.sjcz);
        sjcz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, XjActivity.class);//雅士 巡检
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        xhcz = (LinearLayout) iview.findViewById(R.id.xhcz);
        xhcz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
//                intent.setClass(mContext, XhczActivity.class);//销货操作
                intent.setClass(mContext, DdxhActivity.class);//订单销货
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        ykcz = (LinearLayout) iview.findViewById(R.id.ykcz);
        ykcz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, KccxActivity.class);//雅士 库存查询
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        mtfz = (LinearLayout) iview.findViewById(R.id.mtfz);
        mtfz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, KjdbActivity.class);//唛头复制
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        bjpl = (LinearLayout) iview.findViewById(R.id.bjpl);
        bjpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, BjplActivity.class);//钣金配料
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        glgxjl = (LinearLayout) iview.findViewById(R.id.glgxjl);
        glgxjl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                //intent = new Intent(mContext , GlgxjlActivity.class);
                intent.setClass(mContext, YsGlgxjlActivity.class);
                mContext.startActivity(intent);

            }
        });

    }

}
