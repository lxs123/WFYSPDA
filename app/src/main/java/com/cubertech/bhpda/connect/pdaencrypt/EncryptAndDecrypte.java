package com.cubertech.bhpda.connect.pdaencrypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class EncryptAndDecrypte {
    /// 采用 3DES 加解密方式。
    static String strKey = "fdbc4y6hdhKlf4M3mjgGrMC3PbryXrxw";
    static String strIV = "RfnMfrpec48=";

    /// 默认加密字符串。
    public static String EncryptString(String encryptString) {
        byte[] key;
        try {
            key = new BASE64Decoder().decodeBuffer(strKey);
            byte[] keyiv = new BASE64Decoder().decodeBuffer("RfnMfrpec48=");
            byte[] data = encryptString.getBytes("UTF-8");
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(key);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyiv);
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] bOut = cipher.doFinal(data);
            return new BASE64Encoder().encode(bOut);
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }


    }

}
