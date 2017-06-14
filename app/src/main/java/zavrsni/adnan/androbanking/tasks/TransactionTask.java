package zavrsni.adnan.androbanking.tasks;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
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

public class TransactionTask extends AsyncTask<Void, Void, Boolean> {
    private IPostExecutedTransactionTask caller_;
    private Double amount;
    private String account;
    private int pin;

    private final String path_ = "/transaction";

    public TransactionTask(IPostExecutedTransactionTask pCaller, Double pAmount, String pAccount, int pPin) {
        caller_ = pCaller;
        amount = pAmount;
        account = pAccount;
        pin = pPin;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
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
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            String token = Configuration.decryptFromSharedPrefs("jwt", ((Fragment) caller_).getActivity());
            connection.setRequestProperty("Authorization", token);
            connection.setRequestMethod("POST");

            JSONObject body = new JSONObject();
            body.put("account", account);
            body.put("amount", amount);
            body.put("pin", pin);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body.toString());
            writer.close();

            int status = connection.getResponseCode();
            if(status == 200) {
                return true;
            }
            return false;

        }
        catch (Exception e) {
            Log.d("TransactionTask", e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean response) {
        caller_.onPostExecutedTransactionTask(response);
    }


}
