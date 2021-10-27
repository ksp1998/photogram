package com.mca.imagegallery.helper;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    public static final String PASSWORD = "IMAGE-GALLERY";
    private static final String AES = "AES";

    public static String encrypt(String text) {
        try {
            SecretKeySpec key = generateKey();
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            String encryptedText = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            return encryptedText;
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }

        return text;
    }

    private static SecretKeySpec generateKey() throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = PASSWORD.getBytes(StandardCharsets.UTF_8);
        md.update(bytes, 0, bytes.length);
        byte[] key = md.digest();
        SecretKeySpec keySpec = new SecretKeySpec(key, AES);
        return keySpec;
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec key = generateKey();
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
            decryptedBytes = cipher.doFinal(decryptedBytes);
            String decryptedText = new String(decryptedBytes);
            return decryptedText;
        }
        catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | BadPaddingException
                | IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }

        return encryptedText;
    }
}
