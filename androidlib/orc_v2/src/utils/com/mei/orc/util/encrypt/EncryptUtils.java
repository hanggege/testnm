package com.mei.orc.util.encrypt;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Description: 从AndroidUtilCode项目拷贝修改而来
 * Create by lxj, at 2018/12/4
 */
public final class EncryptUtils {

    private EncryptUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * DES, key length must be 8
     *
     * @param input
     * @param key   key length must be 8
     * @return
     */
    public static String encryptDES(String input, String key) {
        return encryptBySymmetrical("DES", input, key);
    }

    /**
     * DES, key length must be 8
     *
     * @param input
     * @param key   key length must be 8
     * @return
     */
    public static String decryptDES(String input, String key) {
        return decryptBySymmetrical("DES", input, key);
    }

    private static String encryptBySymmetrical(String name, String input, String key) {
        try {
            Cipher cipher = Cipher.getInstance(name);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), name);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] bytes = cipher.doFinal(input.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String decryptBySymmetrical(String name, String input, String key) {
        try {
            Cipher cipher = Cipher.getInstance(name);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), name);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] bytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT));
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}