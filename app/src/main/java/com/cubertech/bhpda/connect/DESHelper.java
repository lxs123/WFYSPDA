package com.cubertech.bhpda.connect;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 字符串工具集合
 * @author Liudong
 */
public class DESHelper {
    //private static final String PASSWORD_CRYPT_KEY = XmlUtil.getConfig().getPasswdKey().substring(0,8);
    //解密数据
    public static String decrypt(String message,String key) throws Exception {

        byte[] bytesrc =convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    //加密
    public static byte[] encrypts(String message, String key)
            throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }
    public static String encrypt(String value,String key){
        String result="";
        try{
            value=java.net.URLEncoder.encode(value, "utf-8");
            result=toHexString(encrypts(value, key)).toUpperCase();
        }catch(Exception ex){
            ex.printStackTrace();
            return "";
        }
        return result;
    }
    public static byte[] convertHexString(String ss)
    {
        byte digest[] = new byte[ss.length() / 2];
        for(int i = 0; i < digest.length; i++)
        {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte)byteValue;
        }
        return digest;
    }
    public static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }
        return hexString.toString();
    }
    public static void main(String[] args) throws Exception {
        String value="123456";
        System.out.println("加密数据:"+value);
        //System.out.println("密码为:"+XmlUtil.getConfig().getPasswdKey());
        String a=encrypt(value,"TNB12345");
        System.out.println("加密后的数据为:"+a);
    }
}
