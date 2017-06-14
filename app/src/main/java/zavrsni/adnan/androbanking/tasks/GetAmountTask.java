package zavrsni.adnan.androbanking.tasks;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import zavrsni.adnan.androbanking.R;
import zavrsni.adnan.androbanking.common.Configuration;

/**
 * Created by Adnan on 5/30/2017.
 */

public class GetAmountTask extends AsyncTask<Void, Void, String> {

    private IPostExecutedGetAmountTask caller_;

    private final String path_ = "/amount";

    public GetAmountTask(IPostExecutedGetAmountTask caller_) {
        this.caller_ = caller_;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            final Resources res = ((Fragment) caller_).getActivity().getResources();
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
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            //dobavljanje dekriptovanog tokena
            String token = Configuration.decryptFromSharedPrefs("jwt", ((Fragment) caller_).getActivity());
            //postavljanje "Authorization" polja zaglavlja u zahtjevu ka serveru
            connection.setRequestProperty("Authorization", token);
            connection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();

            return stringBuilder.toString();
        }
        catch (Exception e) {
            Log.d("GetAmountTask", e.getLocalizedMessage());
            return null;
        }
    }



    @Override
    protected void onPostExecute(String response) {
        try {
            if(response != null) {
                JSONObject amount = new JSONObject(response);
                caller_.onPostExecutedGetAmountTask(amount.getString("amount"));
            }
            else {
                caller_.onPostExecutedGetAmountTask(null);
            }
        }
        catch (Exception e) {
            Log.d("onPostExecuteAmountTask", e.getLocalizedMessage());
            caller_.onPostExecutedGetAmountTask(null);
        }

    }
}
