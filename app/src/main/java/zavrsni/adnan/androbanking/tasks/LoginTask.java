package zavrsni.adnan.androbanking.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import zavrsni.adnan.androbanking.R;
import zavrsni.adnan.androbanking.common.Configuration;

/**
 * Created by Adnan on 5/29/2017.
 */
public class LoginTask extends AsyncTask<Void, Void, String> {

    //delegat
    private IPostExecutedLoginTask caller_;
    private String pin_;
    private String username_;
    private String password_;
    private final String path_ = "/login";

    public LoginTask(IPostExecutedLoginTask caller, String username, String password, String pin) {
        caller_ = caller;
        username_ = username;
        password_ = password;
        pin_ = pin;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            final Resources res = ((Context)caller_).getResources();
            URL url = new URL(res.getString(R.string.baseURL) + path_);

            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    if(hostname.equals(res.getString(R.string.hostname))) {
                        return true;
                    }
                    return false;
                }
            });
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            JSONObject body = new JSONObject();
            body.put("username", username_);
            body.put("password", password_);
            body.put("pin", pin_);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body.toString());
            writer.flush();
            writer.close();

            //Provjerava se status kod odgovora od servera,
            //a potom se vrijednost zaglavlja "Authorization" vraća kao rezultat operacije
            int status = connection.getResponseCode();
            if(status == 200) {
                String auth = connection.getHeaderField("Authorization");
                return auth;
            }

            return null;

        }
        catch (Exception e){
            Log.d("LoginTask", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        try {
            if (response != null) {
                //Ovdje je implementiran Delegation pattern kako bi se metoda izvršila na Threadu
                //pozivaoca operacije za prijavu korisnika
                caller_.onPostExecutedLoginTask(response);
            }
            else {
                Log.d("LoginTask:", "LoginResponse is null");
                caller_.onPostExecutedLoginTask(null);
            }
        }
        catch (Exception e){
            Log.d("LoginTask:", e.getMessage());
            caller_.onPostExecutedLoginTask(null);
        }


    }
}
