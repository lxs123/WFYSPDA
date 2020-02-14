package com.cubertech.bhpda.connect.encrypt;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017/4/27.
 */

public class AESUtilMy {
    /**
     * 加密--把加密后的byte数组先进行二进制转16进制在进行base64编码
     *
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            throw new IllegalArgumentException("Argument sKey is null.");
        }
        if (sKey.length() != 16) {
            throw new IllegalArgumentException(
                    "Argument sKey'length is not 16.");
        }
        byte[] raw = sKey.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
        String tempStr = parseByte2HexStr(encrypted);

        //Base64Encoder encoder = new Base64Encoder();
        //return encoder.encode(tempStr.getBytes("UTF-8"));
        return tempStr;
    }

    /**
     * 解密--先 进行base64解码，在进行16进制转为2进制然后再解码
     *
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String sSrc, String sKey) throws Exception {

        if (sKey == null) {
            throw new IllegalArgumentException("499");
        }
        if (sKey.length() != 16) {
            throw new IllegalArgumentException("498");
        }

        byte[] raw = sKey.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        //Base64Encoder encoder = new Base64Encoder();
        //byte[] encrypted1 = encoder.decode(sSrc);

        //String tempStr = new String(encrypted1, "utf-8");
        byte[] encrypted1 = sSrc.getBytes();
        String tempStr = new String(encrypted1, "utf-8");
        encrypted1 = parseHexStr2Byte(tempStr);
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original, "utf-8");
        return originalString;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


   /* // 测试
    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        //String key = "W8p102YW9AZQ117g4t4z241pr6IM9oF49Q3L4pwsuWRE0E7Z04GM1819A217";
        String key = "W8p102YW9AZQ117g";
        String data = "8F932B37DECD7DD898DA63DC03BCDEB8D9B77F07AC4FA02918876BE0E2652DFBFCB2FA0F3D6B09193DC3800E4C9DE5FAB6C7AE8D24E24524D7CC39421C7A988031FD07C83844B395B36EC9AF863D956E394308612519EF237DB705786BDB41927012BEFA2DE93A626C4492432DB83366C1B22F65BA60DA464446CC9F551B8BF6410B078F51511880E679200004D2F1550BFC5F099331EFFE934227BE7A68F3B097FB988E656F3EEB68A7E18D4BB7ADD50E6CBCCDD6C2275699BF867656670BF6";
        String keyForAES = key.substring(0, 16);
        try {
            String decryptData = AESUtilMy.decrypt(data, keyForAES);
            System.out.println("decryptData : " + decryptData);

            System.out.println("共用时:" + (System.currentTimeMillis() - startTime) + "ms");

            String dec = AESUtilMy.encrypt(decryptData, keyForAES);

            System.out.println("dec : " + dec);

            System.out.println("共用时:" + (System.currentTimeMillis() - startTime) + "ms");

            String decryptData1 = AESUtilMy.decrypt(dec, keyForAES);
            System.out.println("decryptData1 : " + decryptData1);

            System.out.println("共用时:" + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {

        }
    }*/
}
