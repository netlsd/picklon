package id.co.picklon.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5 {

    public static String encode() {
        String key = "#@SAD@#%*(*Dsd3246dsajfhdkjf435DFm,rerew4532978dskfkdasrGYH%YUGFDERWrtw";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte message : messageDigest) {
                String hex = Integer.toHexString(0xFF & message);
                if (hex.length() == 1)
                    hexString.append('0');

                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
