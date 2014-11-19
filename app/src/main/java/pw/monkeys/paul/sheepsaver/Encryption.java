package pw.monkeys.paul.sheepsaver;

import android.util.Base64;

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
    private static Cipher ecipher;
    private static Cipher dcipher;
    private static final int iterationCount = 10;

    private static byte[] salt = {
            (byte)0xB6,(byte)0x32,(byte)0xD5,(byte)0xF2,
            (byte)0x42, (byte)0x12, (byte)0xD7, (byte)0xE3
    };
    public static String ECrypt(String password,String plainText) throws Exception{
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        ecipher = Cipher.getInstance(key.getAlgorithm());
        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        String auth = "Authorized - Access Granted";
        byte[] d = auth.getBytes();
        byte[] enc = ecipher.doFinal(d);
        enc = Base64.encode(enc, Base64.DEFAULT);
        return new String(enc);
    }
    public static String DCrypt(String password,String cipherText) throws Exception{
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        dcipher = Cipher.getInstance(key.getAlgorithm());
        dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        byte[] decode = cipherText.getBytes();
        decode = Base64.decode(decode,Base64.DEFAULT);
        return new String(dcipher.doFinal(decode), "UTF8");
    }
}