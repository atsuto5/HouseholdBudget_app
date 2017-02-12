package com.kii.sample.balance.samples;

import android.content.Context;
import android.util.Log;

import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Atsuto5 on 2016/09/25.
 */
public class FacebookConcealSample {

    // encrypt/decryptのときに同じ文字列を渡す必要がある
    String key = "key";

    public byte[] encryption(String password, Context context) {

        Crypto crypto = new Crypto(
                new SharedPrefsBackedKeyChain(context),
                new SystemNativeCryptoLibrary());

        if (!crypto.isAvailable()) {
            return null;
        }

        byte[] cipherPass = new byte[0];
        try {
            // UTF8でbyte[]に変換して、暗号化する
            cipherPass = crypto.encrypt(password.getBytes("utf-8"), new Entity(key));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cipherPass;
    }

    public String decryption(byte[] cipherPass, Context context) {

        Crypto crypto = new Crypto(
                new SharedPrefsBackedKeyChain(context),
                new SystemNativeCryptoLibrary());
        if (!crypto.isAvailable()) {
            return null;
        }
        String password = null;
        try {
            byte[] decrypted = crypto.decrypt(cipherPass, new Entity(key));
            password = new String(decrypted, "utf-8");

            Log.v("test", "decrypted data is " + password);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return password;
    }

}
