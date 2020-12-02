package com.github.chenxdGit.gateway.config.filter.securityFilter;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.Base64Utils;
/**
 * AES加密工具类
 * @author 陈晓东
 *
 */
public class SecurityUtils {
    public final static String letters = "abcdefghijklmnopqrstuvwxyz0123456789";

    public final static String key = "ed26d4cd99aa11e5b8a4c89cdc776729";

    private static String Algorithm = "AES";

    private static String AlgorithmProvider = "AES/ECB/PKCS5Padding";

    private final static String encoding = "UTF-8";
    /**
     * 加密
     * @param src
     * @return
     */
    public static String encrypt(String src){
        SecretKey secretKey;
		try {
			secretKey = new SecretKeySpec(key.getBytes("utf-8"), Algorithm);
			Cipher cipher = Cipher.getInstance(AlgorithmProvider);
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        byte[] cipherBytes = cipher.doFinal(src.getBytes(Charset.forName("utf-8")));
	        return Base64Utils.encodeToString(cipherBytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return src;
		}
        
    }
    /**
     * 解密
     * @param src
     * @return
     */
    public static String decrypt(String src)  {
        SecretKey secretKey;
		try {
			secretKey = new SecretKeySpec(key.getBytes("utf-8"), Algorithm);
	        //IvParameterSpec ivParameterSpec = getIv();
	        Cipher cipher = Cipher.getInstance(AlgorithmProvider);
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        byte[] hexBytes = Base64Utils.decodeFromString(src);
	        byte[] plainBytes = cipher.doFinal(hexBytes);
	        return new String(plainBytes, "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return src;
		}

    }

    public static String md5Encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return str;
        }
    }
    public static String encryptKey(String key) throws Exception {
        String encryptedKey = "";
        String[] array = key.split("");
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            encryptedKey += array[i];
            for (int j = 0; j < i % 2 + 1; j++) {
                int index = random.nextInt(letters.length());
                encryptedKey += letters.substring(index, index + 1);
            }
        }
        return Base64Utils.encodeToString(new StringBuilder(encryptedKey).reverse().toString().getBytes(encoding)).replaceAll("\n", "");
    }

    public static String decryptKey(String encryptedKey) {
        encryptedKey = new String(Base64Utils.decodeFromString(encryptedKey));
        String key = "";
        char[] c = new StringBuilder(encryptedKey).reverse().toString().toCharArray();
        for (int i = 0, j = 0; i < encryptedKey.length(); i++) {
            key += c[i];
            i += (j++ % 2 + 1);
        }
        return key;
    }
}