package com.cubertech.bhpda.connect;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

//import org.tini.kxml2.kdom.Element;
//import org.tini.kxml2.kdom.Node;

/**
 * Created by Administrator on 2017/3/17.
 */

public class LastestWebServiceUtils {


    /**
     * @param url        WebService服务器地址
     * @param methodName WebService的调用方法名
     * @param strProps   WebService的参数
     *  所有 调用webservice通过该方法
     */
    @SuppressWarnings("finally")
    public static SoapObject callWebService(String url, String methodName,
                                            String strProps, String nameSpace, String Data) {//原
    /*public static SoapObject callWebService(String url, String methodName,
                                            SimpleArrayMap<String, String> strProps, String nameSpace, String Data) {*/


        SoapObject resultSoapObject = null;
        // 创建HttpTransportSE对象，传递WebService服务器地址
        HttpTransportSE httpTransportSE = null;
        try {
            httpTransportSE = new HttpTransportSE(url, 8000);
            //httpTransportSE = new HttpTransportSE(url);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
            return resultSoapObject;
        }
        // 创建SoapObject对象
        SoapObject soapObject = new SoapObject(nameSpace, methodName);

        // SoapObject添加参数
        if (strProps != null) {
            Log.i("申请的值为：",strProps);
            soapObject.addProperty("strJson", strProps);//原

          /*  for (int index = 0; index < strProps.size(); index++) {
                String key = strProps.keyAt(index);
                String value = strProps.get(key);
                soapObject.addProperty(key, value);
            }*/
        }


        // soapheader在这里
        // soapheader在这里各位如果你们的服务端没有Header验证这一块可以去掉
        Element[] header = new Element[1];
        header[0] = new Element().createElement(nameSpace, "MySoapHeader");
        Element json = new Element().createElement(nameSpace, "Json");
        json.addChild(Node.TEXT, Data);
        header[0].addChild(Node.ELEMENT, json);
        Element version = new Element().createElement(nameSpace, "Version");
        version.addChild(Node.TEXT, "1.81");
        header[0].addChild(Node.ELEMENT, version);
        Element actionType = new Element().createElement(nameSpace, "ActionType");
        actionType.addChild(Node.TEXT, "1001");
        header[0].addChild(Node.ELEMENT, actionType);

    /*    Element[] header = new Element[1];
        header[0] = new Element().createElement(nameSpace, "MySoapHeader");

        Element username = new Element().createElement(nameSpace, "UserName");
        username.addChild(Node.TEXT, "SoapHeader");
        header[0].addChild(Node.ELEMENT, username);
        Element pass = new Element().createElement(nameSpace, "PassWord");
        pass.addChild(Node.TEXT, "123456");
        header[0].addChild(Node.ELEMENT, pass);*/


        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        // 设置是否调用的是.Net开发的WebService
        //原
        //soapEnvelope.setOutputSoapObject(soapObject);
        soapEnvelope.bodyOut=soapObject;

        soapEnvelope.dotNet = true;

        soapEnvelope.headerOut = header;//验证header;

        httpTransportSE.debug = true;

        try {
            httpTransportSE.call(nameSpace + methodName, soapEnvelope);
            if (soapEnvelope.getResponse() != null) {
                // 获取服务器响应返回的SoapObject
                resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
            }

        } catch (Exception e) {
            String ex=e.toString();
            if (ex.contains("ECONNRESET")
                    && ex.contains("Connection reset by peer")){

                resultSoapObject=new SoapObject("error","error");
;
            }
            //e.printStackTrace();
            // 如果是这个错误重新访问java.net.SocktException:sendto
            // failed:ECONNRESET(Connection reset by peer)
        } finally {
            return resultSoapObject;
        }
    }

}
