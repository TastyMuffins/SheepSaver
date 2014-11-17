package pw.monkeys.paul.sheepsaver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


public class Login extends Activity {

    private static Cipher ecipher;
    private static Cipher dcipher;
    private static final int iterationCount = 10;

    // 8-byte Salt
    private static byte[] salt = {
            (byte)0xB6,(byte)0x32,(byte)0xD5,(byte)0xF2,
            (byte)0x42, (byte)0x12, (byte)0xD7, (byte)0xE3
    };
    private String GodKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final EditText passwordText = (EditText) findViewById(R.id.loginPassword);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pop up a spinner or something
                //Go get the file
                //use password + salt to make hash key
                // provide password, salt, iteration count for generating PBEKey of fixed-key-size PBE ciphers
                KeySpec keySpec = new PBEKeySpec(passwordText.getText().toString().toCharArray(), salt, iterationCount);
                // create a secret (symmetric) key using PBE with MD5 and DES
                try {
                    SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
                    // construct a parameter set for password-based encryption as defined in the PKCS #5 standard
                    AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
                    ecipher = Cipher.getInstance(key.getAlgorithm());
                    dcipher = Cipher.getInstance(key.getAlgorithm());

                    // initialize the ciphers with the given key
                    ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                    dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
                    //read auth file into String
                    String auth = "Authorized - Access Granted";
                    byte[] d = auth.getBytes();
                    byte[] enc = ecipher.doFinal(d);
                    enc = Base64.encode(enc,Base64.DEFAULT);
                    String test = new String(enc);
                    Log.d("AuthFileContents",test);
                    String authFile = "[B@b1d93d60";
                   // byte[] decode = authFile.getBytes(); // this right now is just passing the decoded string to the encoded string
                    byte[] decode = enc;
                    decode = Base64.decode(decode,Base64.DEFAULT);
                    String decryptedAuthFile = new String(dcipher.doFinal(decode), "UTF8");
                    if(decryptedAuthFile.equals("Authorized - Access Granted"))
                    {
                        Toast.makeText(getApplicationContext(),"Authenticated!",Toast.LENGTH_SHORT).show();
                        //Decrypted text reads out correct
                        GodKey = key.toString(); //store key
                        //do intention and activity change with GodKey being passed

                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Failed to authenticate!",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show(); //a generic toast catch for all encryption error outs
                    Log.d("Custom Error",e.getMessage());// print to console too incase of crash

            }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
