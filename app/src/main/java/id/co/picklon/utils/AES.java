package id.co.picklon.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private static final SecretKeySpec sks = new SecretKeySpec(MD5.encode().getBytes(), "AES");

    public static String encrypt(String pureData) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/ZeroBytePadding");
            c.init(Cipher.ENCRYPT_MODE, sks);
            return Base64.encodeToString(c.doFinal(pureData.getBytes()), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/ZeroBytePadding");
            c.init(Cipher.DECRYPT_MODE, sks);
            byte[] decodedValue = Base64.decode(encrypted.getBytes(), Base64.NO_WRAP);
            return new String(c.doFinal(decodedValue), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}