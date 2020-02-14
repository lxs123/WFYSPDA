package com.cubertech.bhpda.Activity.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cubertech.bhpda.Activity.RelevancePackage.GlgxjlActivity;
import com.cubertech.bhpda.Activity.RelevancePackage.YjcxActivity;
import com.cubertech.bhpda.Activity.TransferPackage.GxzyActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;
import com.cubertech.bhpda.TKApplication;
import com.cubertech.bhpda.connect.SiteService;


/**
 * 关联关系
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RelevanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RelevanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelevanceFragment extends Fragment {
    private SiteService siteService;
    private Context mContext;


    TKApplication application;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_DATA="DATA";
    private static final String ARG_NAME="Name";
    private static final String ARG_PER="PER";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String mData;//账套
    private String mName;//用户登录名
    private String mPer;//权限

    private OnFragmentInteractionListener mListener;
    private View Iview;

    public RelevanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RelevanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RelevanceFragment newInstance(String param1, String param2,
                                                String datas, String names, String per) {
        RelevanceFragment fragment = new RelevanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        args.putString(ARG_DATA,datas);//账套
        args.putString(ARG_NAME,names);//用户登录名
        args.putString(ARG_PER,per);//权限
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            mData=getArguments().getString(ARG_DATA);//账套
            mName=getArguments().getString(ARG_NAME);//用户登录名
            mPer=getArguments().getString(ARG_PER);//权限
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Iview=inflater.inflate(R.layout.fragment_relevance, container, false);
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

    LinearLayout ly_glgxjl,ly_yjcx;
    LinearLayout glgxjl,yjcx;
    private void initView(View iview){

        ly_glgxjl=(LinearLayout) iview.findViewById(R.id.ly_glgxjl);
        ly_yjcx=(LinearLayout) iview.findViewById(R.id.ly_yjcx);
        //mPer = "GLGXJLGLGXCX";
        if(mPer.indexOf("GLGXJL")>=0){
            ly_glgxjl.setVisibility(View.VISIBLE);//显示关联关系按钮
        }
        if(mPer.indexOf("GLGXCX")>=0){
            ly_yjcx.setVisibility(View.VISIBLE);//显示元件供应商查询
        }
        glgxjl=(LinearLayout) iview.findViewById(R.id.glgxjl);
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
                intent.setClass(mContext, GlgxjlActivity.class);
                mContext.startActivity(intent);

            }
        });

        yjcx=(LinearLayout) iview.findViewById(R.id.yjcx);
        yjcx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                //intent=new Intent(mContext, YjcxActivity.class);
                intent.setClass(mContext,YjcxActivity.class);
                mContext.startActivity(intent);
            }
        });

    }
}
