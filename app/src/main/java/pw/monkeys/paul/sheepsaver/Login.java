package pw.monkeys.paul.sheepsaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    //AlertDialog alertDialog;

    private static Cipher ecipher;
    private static Cipher dcipher;
    private static final int iterationCount = 10;

    // 8-byte Salt
    private static byte[] salt = {
            (byte)0xB6,(byte)0x32,(byte)0xD5,(byte)0xF2,
            (byte)0x42, (byte)0x12, (byte)0xD7, (byte)0xE3
    };
    private String GodKey;
    private String authFile = new String("");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final EditText passwordText = (EditText) findViewById(R.id.loginPassword);
        //alertDialog = new AlertDialog.Builder(this).create();

        //delete the file to cause the prompt every startup
        //deleteFile("AuthFile.dat");

        InputStream inputStream = null; //try to load authfile
        try {
            inputStream = openFileInput("AuthFile.dat");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                authFile = stringBuilder.toString();
                Log.e("Debug", "AuthFileContents: " + authFile);
            }
        } catch (FileNotFoundException e) {
            //If file not found then user has not set a password yet for app!
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.newpassword,null);
            final EditText newPassword1 = (EditText)layout.findViewById(R.id.newPassword1);
            final EditText newPassword2 = (EditText)layout.findViewById(R.id.newPassword2);
            final Button acceptButton = (Button)layout.findViewById(R.id.button);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            builder.setCancelable(false);
            builder.setTitle("Welcome!");
            builder.setMessage("" +
                    "Hello and welcome to SheepSaver the password managment app! \n" +
                    "Please set your password, this password will be used to encrypt all of your passwords so choose wisely! ");
            final AlertDialog alertDialog = builder.show();
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newPassword1.getText().toString().equals(newPassword2.getText().toString())) {
                        try {
                            Log.e("Debug", "Creating Auth File with password: " + newPassword1.getText().toString());
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("AuthFile.dat", Context.MODE_PRIVATE));
                            outputStreamWriter.write(Encryption.ECrypt(newPassword1.getText().toString(),"Authorized - Access Granted"));
                            outputStreamWriter.close();
                            Toast.makeText(getApplicationContext(), "Your Password Has Been Set!", Toast.LENGTH_LONG).show();
                            //Dismiss dialog and restart activity to reload AuthFile
                            alertDialog.dismiss();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show(); //a generic toast catch for all encryption error outs
                            Log.d("Custom Error", e.getMessage());// print to console too incase of crash
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


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
                    Log.e("Debug", "Checking against: " + authFile);
                    if(Encryption.DCrypt(passwordText.getText().toString(),authFile).equals("Authorized - Access Granted"))
                    {
                        Toast.makeText(getApplicationContext(),"Authenticated!",Toast.LENGTH_SHORT).show();
                        //Decrypted text reads out correct
                        GodKey = passwordText.getText().toString(); //store key plain text (MUST FIX)
                        //do intention and activity change with GodKey being passed
                        Intent intent = new Intent(getApplicationContext(), Shepherd.class);
                        intent.putExtra("GodKey",GodKey);
                        finish();
                        startActivity(intent);

                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Failed to authenticate!",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Password failed",Toast.LENGTH_SHORT).show(); //a generic toast catch for all encryption error outs
                    Log.d("Custom Error", "Fuck error?" + e.getMessage());// print to console too in case of crash
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
