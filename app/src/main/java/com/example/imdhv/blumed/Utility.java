package com.example.imdhv.blumed;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.transition.CircularPropagation;
import android.util.Log;
import android.widget.Toast;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.R.attr.publicKey;

/**
 * Created by imdhv on 26-Feb-17.
 */

public class Utility {
    public static final String serverurl = "https://blumed.000webhostapp.com/androidsupport.php";
    private final static String ALGORITM_CLIENT = "Blowfish";
    private final static String KEY_CLIENT = "2356a3a42ba5781f80a72dad3f90aeee8ba93c7637aaf218a8b8c18c";

    public static byte[] encryptClient(String plainText) throws GeneralSecurityException {

        SecretKey secret_key = new SecretKeySpec(KEY_CLIENT.getBytes(), ALGORITM_CLIENT);

        Cipher cipher = Cipher.getInstance(ALGORITM_CLIENT);
        cipher.init(Cipher.ENCRYPT_MODE, secret_key);

        return cipher.doFinal(plainText.getBytes());
    }

    public static String decryptClient(byte[] encryptedText) throws GeneralSecurityException {

        SecretKey secret_key = new SecretKeySpec(KEY_CLIENT.getBytes(), ALGORITM_CLIENT);

        Cipher cipher = Cipher.getInstance(ALGORITM_CLIENT);
        cipher.init(Cipher.DECRYPT_MODE, secret_key);

        byte[] decrypted = cipher.doFinal(encryptedText);

        return new String(decrypted);
    }

    public static KeyPair getKeys() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,BadPaddingException
    {
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(32);
        KeyPair kp = kpg.genKeyPair();
        return  kp;
    }
    public static String ServerEncrypt (String plain,String private_key,String public_key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
        byte[] encryptedBytes;
        String encrypted;
        Cipher cipher;
        cipher = Cipher.getInstance("RSA");
        PublicKey pb= KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(public_key.getBytes()));
        cipher.init(Cipher.ENCRYPT_MODE,pb);
        encryptedBytes = cipher.doFinal(plain.getBytes());
        encrypted = bytesToHex(encryptedBytes);
        return encrypted;
    }

    public static String ServerDecrypt (final String result,String private_key,String public_key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
    {
        byte[] decryptedBytes;
        String decrypted;
        Cipher cipher1;
        cipher1=Cipher.getInstance("RSA");
        PrivateKey pr= KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(private_key.getBytes()));
        cipher1.init(Cipher.DECRYPT_MODE,pr);
        decryptedBytes = cipher1.doFinal(result.getBytes());
        decrypted = bytesToHex(decryptedBytes);
        return decrypted;
    }

    public static String bytesToHex(byte[] data) {

        if (data == null)
            return null;

        String str = "";

        for (int i = 0; i < data.length; i++) {
            if ((data[i] & 0xFF) < 16)
                str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
            else
                str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
        }

        return str;

    }

    public static void createdb(Context c){
        try{
            SQLiteDatabase database = c.openOrCreateDatabase("/sdcard/userlists.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS USERS (Name TEXT,Number TEXT,key String);");
            database.execSQL("CREATE TABLE IF NOT EXISTS MESSAGE (id integer primary key autoincrement,frommobile TEXT, tomobile text, data text, creationtime text,senderttl int,status text,action text);");
            database.execSQL("CREATE TABLE IF NOT EXISTS CHATLIST (Name TEXT,Number TEXT,key String);");
        }
        catch(Exception e1){
            Log.e("",e1+"");
  //          Toast.makeText(getActivity(), "ERROR "+e1.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
