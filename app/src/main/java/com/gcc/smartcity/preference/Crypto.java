package com.gcc.smartcity.preference;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES Encrypt/Decrypt Utility
 */
public class Crypto {

    static final String TAG = "Encryption";

    static final String ENCRYPT_TYPE = "AES/CBC/PKCS5PADDING";

    /**
     * Encrypts the string using AES
     *
     * @param stringToEncrypt - the string which needs to be encrypted
     * @param secretKeySpec   - secret key generated based on seed and key length
     * @return - the encrypted in byte format
     */
    public static byte[] encryptAES(String stringToEncrypt, SecretKeySpec secretKeySpec) {

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encodedBytes = cipher.doFinal(stringToEncrypt.getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }

        return encodedBytes;
    }

    /**
     * Decrypts the string using AES
     * <p>
     * NOTE: Be careful, we need to pass encodedbytes for decryption
     *
     * @param toDecrypt     - the string which needs to be decrypted
     * @param secretKeySpec - secret key generated based on seed and key length
     * @return - the decrypted in byte format
     */
    public static byte[] decryptAES(byte[] toDecrypt,
                                    SecretKeySpec secretKeySpec) {
        Cipher cipher;
        // Decode the encoded data with AES
        byte[] decodedBytes = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");

            if (decodedBytes == null) {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                decodedBytes = cipher.doFinal(toDecrypt);
            }
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }

        return decodedBytes;
    }


    public static byte[] encryptAESWithCBC(String stringToEncrypt, SecretKeySpec secretKeySpec) {

        byte[] encodedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
            encodedBytes = cipher.doFinal(stringToEncrypt.getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }

        return encodedBytes;
    }

    public static byte[] decryptWithCBC(byte[] toDecrypt,
                                        SecretKeySpec secretKeySpec) {

        byte[] decodedBytes = null;
        try {

            //	decodedBytes = cipher.doFinal(toDecrypt);
            if (decodedBytes == null) {
                Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE);
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
                decodedBytes = cipher.doFinal(toDecrypt);
            }
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }

        return decodedBytes;
    }

    public static final String encryptWithmd5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}