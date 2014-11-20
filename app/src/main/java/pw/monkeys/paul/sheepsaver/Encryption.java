package pw.monkeys.paul.sheepsaver;

import android.util.Base64;
import android.util.Log;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Created by Paul on 11/18/2014.
 */
public class Encryption {
    private static Cipher cipher;
    private static final int iterationCount = 10;

    private static byte[] salt = {
            (byte)0xB6,(byte)0x32,(byte)0xD5,(byte)0xF2,
            (byte)0x42, (byte)0x12, (byte)0xD7, (byte)0xE3
    };
    public static String ECrypt(String password,String plainText) throws Exception{
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        byte[] d = plainText.getBytes();
        byte[] enc = cipher.doFinal(d);
        enc = Base64.encode(enc, Base64.DEFAULT);
        Log.e("Encrypt",plainText+" -> "+new String(enc));
        return new String(enc);
    }
    public static String DCrypt(String password,String cipherText) throws Exception{
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        byte[] decode = cipherText.getBytes();
        decode = Base64.decode(decode,Base64.DEFAULT);
        Log.e("Decrypt",cipherText+" -> "+new String(cipher.doFinal(decode), "UTF8"));
        return new String(cipher.doFinal(decode), "UTF8");
    }
}
