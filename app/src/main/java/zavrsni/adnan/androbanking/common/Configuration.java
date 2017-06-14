package zavrsni.adnan.androbanking.common;


import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPairGenerator;
import java.security.KeyStore;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;


/**
 * Created by Adnan on 5/30/2017.
 */

public class Configuration {

    //definirane konstante
    private static final String KEY_ALIAS = "androbankingkey";
    private static final String SHARED_PREFERENCES = "androbankingsharedprefs";

    private static String getSharedPreferencesEntry(String key, Context context) {
        SharedPreferences preferences = context.getApplicationContext()
                                                .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
    private static void setSharedPreferencesEntry(String key, String value, Context context) {
        SharedPreferences preferences = context.getApplicationContext()
                                                .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void deleteSharedPreferencesEntry(String key, Context context) {
        SharedPreferences preferences = context.getApplicationContext()
                                                .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    //metoda koja kreira par kljuceva za enkripciju i dekripciju
    private static void createKey() {
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            //koristi se RSA algoritam za generisanje kljuceva
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            kpg.initialize(new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512) // hash one-way funkcije za osiguranje integriteta poruke
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .build()
            );
            kpg.generateKeyPair();
        }
        catch (Exception e){
            Log.d("Conf.createKey", e.getLocalizedMessage());
        }
    }

    public static Boolean encryptToSharedPrefs(String key, String value, Context context){
        try {
            //Učitavanje KeyStore instance koja pripada AndroBanking aplikaciji
            //Prolazak kroz postojeće ključeve, te ukoliko ključevi ne postoje,
            //poziva se metoda za pravljenje ključeva
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            Enumeration<String> aliases = keyStore.aliases();
            PublicKey publicKey = null;
            while(aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if(alias.equals(KEY_ALIAS)) {
                    publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();
                    break;
                }
            }
            if(publicKey == null) {
                createKey();
            }
            keyStore.load(null);
            //Dobavlja se javni ključ koji služi za enkripciju, te se potom vrši enkripcija
            publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            cipherOutputStream.write(value.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte[] val = outputStream.toByteArray();
            String encryptedValue = Base64.encodeToString(val, Base64.DEFAULT);
            //Enkriptovana informacija se smješta u Shared Preferences
            setSharedPreferencesEntry(key, encryptedValue, context);
            return true;
        }
        catch (Exception e) {
            Log.d("Configuration:", e.getLocalizedMessage());
            return false;
        }
    }

    public static String decryptFromSharedPrefs(String key, Context context) {
        try {
            //Najprije se dobavlja enkriptovana informacija iz Shared Preferences
            String token = getSharedPreferencesEntry(key, context);
            //Zatim se inicijalizira KeyStore instanca
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            //Dobavlja se privatni ključ kojim će se izvršiti dekripcija sadržaja
            PrivateKey privateKey = (PrivateKey)keyStore.getKey(KEY_ALIAS, null);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] buffer = Base64.decode(token, Base64.DEFAULT);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }
            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }
            //Rezultat dekripcije
            String decryptedToken = new String(bytes, 0, bytes.length, "UTF-8");
            return decryptedToken;
        }
        catch (Exception e) {
            Log.d("Configuration:", e.getLocalizedMessage());
            return null;
        }
    }

    private static void deleteKeyFromKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            keyStore.deleteEntry(KEY_ALIAS);
        }
        catch (Exception e) {
            Log.d("Configuration.deleteKey", e.getLocalizedMessage());
        }

    }
}
