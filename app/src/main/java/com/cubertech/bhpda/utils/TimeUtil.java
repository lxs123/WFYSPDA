package com.cubertech.bhpda.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class TimeUtil {
    public static void showDatePickerDialog(Activity activity, int themeResId, final TextView tv, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity
                ,  themeResId
                // 绑定监听器(How the parent is notified that the date is set.)
                ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                /*tv.setText("您选择了：" + year + "年" + monthOfYear
                        + "月" + dayOfMonth + "日");*/
                String month=monthOfYear<9?"0"+String.valueOf(monthOfYear+1):String.valueOf(monthOfYear+1);
                String day=dayOfMonth<10?"0"+String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);

                tv.setText(String.valueOf(year)+month+day);
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                ,calendar.get(Calendar.MONTH)
                ,calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

}
