package com.cubertech.bhpda.Activity.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cubertech.bhpda.Activity.PasswordActivity;
import com.cubertech.bhpda.Activity.PrinterPackage.BlueToothPrinterActivity;
import com.cubertech.bhpda.Activity.PrinterPackage.WifiPrinterActivity;
import com.cubertech.bhpda.EntitysInfo.AccountInfo;
import com.cubertech.bhpda.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private View Iview;

    private Activity mActivity;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_DATA = "DATA";
    private static final String ARG_NAME = "Name";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String mData;//账套
    private String mName;//用户登录名

    private OnFragmentInteractionListener mListener;

    public SetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetFragment newInstance(String param1, String param2,String datas, String names) {
        SetFragment fragment = new SetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        args.putString(ARG_DATA, datas);//账套
        args.putString(ARG_NAME, names);//用户登录名

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Iview = inflater.inflate(R.layout.fragment_set, container, false);
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
           /* throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_wifi_print:
                mContext.startActivity(new Intent(mContext, WifiPrinterActivity.class));
                break;
            case R.id.setting_bluetooth_print:
                mContext.startActivity(new Intent(mContext, BlueToothPrinterActivity.class));
                break;
        }
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

    Button btxx;

    private void initView(View iview) {
        btxx = (Button) iview.findViewById(R.id.btxx);
        btxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AccountInfo ai = new AccountInfo();
                ai.setName(mName);//用户名
                ai.setData(mData);//账套
                Bundle bundle = new Bundle();
                bundle.putParcelable("account", ai);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(mContext, PasswordActivity.class);
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                //Intent intent = new Intent(mContext, PasswordActivity.class);
                //mContext.startActivity(intent);
            }
        });
        TextView tv_wifi_printer = (TextView) iview.findViewById(R.id.setting_wifi_print);
        TextView tv_bluetooth_printer = (TextView) iview.findViewById(R.id.setting_bluetooth_print);
        tv_wifi_printer.setOnClickListener(this);
        tv_bluetooth_printer.setOnClickListener(this);
    }
}
