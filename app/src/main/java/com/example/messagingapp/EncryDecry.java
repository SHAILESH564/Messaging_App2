package com.example.messagingapp;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryDecry {
    String AES = "AES";
    public String Encrypt(String Data, String Password) throws  Exception{

        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);

        return  encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes ,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
    public String Decrypt(String Data, String Password) throws Exception{
        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue = Base64.decode(Data,Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String DecryptedValue = new String(decValue);
        return DecryptedValue;
    }
}
