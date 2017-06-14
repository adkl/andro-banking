package zavrsni.adnan.androbanking.tasks;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import zavrsni.adnan.androbanking.R;
import zavrsni.adnan.androbanking.common.Configuration;


/**
 * Created by Adnan on 6/3/2017.
 */

public class GetSafeContentTask extends AsyncTask<Void, Void, String> {

    private IPostExecutedGetSafeContentTask caller;
    private final String path = "/safe";

    public GetSafeContentTask(IPostExecutedGetSafeContentTask caller) {
        this.caller = caller;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            final Resources res = ((Fragment) caller).getActivity().getResources();
            URL url = new URL(res.getString(R.string.baseURL) + path);

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
            String token = Configuration.decryptFromSharedPrefs("jwt", ((Fragment) caller).getActivity());
            connection.setRequestProperty("Authorization", token);
            connection.setRequestProperty("X-Pin", "1234");
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
            Log.d("GetSafeContentTask", e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        try {
            if(response != null) {
                JSONObject safe = new JSONObject(response);
                caller.onPostExecutedGetSafeContentTask(safe.getString("safe"));
            }
            else {
                caller.onPostExecutedGetSafeContentTask(null);
            }
        }
        catch (Exception e) {
            Log.d("onGetSafeContentTask", e.getLocalizedMessage());
            caller.onPostExecutedGetSafeContentTask(null);
        }

    }
}
