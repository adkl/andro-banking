package zavrsni.adnan.androbanking.tasks

import android.app.Fragment
import android.content.res.Resources
import android.os.AsyncTask
import android.util.Log

import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

import zavrsni.adnan.androbanking.R
import zavrsni.adnan.androbanking.common.Configuration


/**
 * Created by Adnan on 6/3/2017.
 */

class GetSafeContentTask(private val caller: IPostExecutedGetSafeContentTask) : AsyncTask<Void, Void, String>() {
    private val path = "/safe"

    override fun doInBackground(vararg params: Void): String? {
        try {
            val res = (caller as Fragment).activity.resources
            val url = URL(res.getString(R.string.baseURL) + path)

            val connection = url.openConnection() as HttpsURLConnection
            connection.hostnameVerifier = HostnameVerifier { hostname, session ->
                if (hostname == res.getString(R.string.hostname)) {
                    true
                } else false
            }
            connection.doInput = true
            connection.setRequestProperty("Accept", "application/json")
            val token = Configuration.decryptFromSharedPrefs("jwt", (caller as Fragment).activity)
            connection.setRequestProperty("Authorization", token)
            connection.setRequestProperty("X-Pin", "1234")
            connection.requestMethod = "GET"

            val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
            val stringBuilder = StringBuilder()
            var line: String
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n")
            }
            bufferedReader.close()

            return stringBuilder.toString()
        } catch (e: Exception) {
            Log.d("GetSafeContentTask", e.localizedMessage)
            return null
        }

    }

    override fun onPostExecute(response: String?) {
        try {
            if (response != null) {
                val safe = JSONObject(response)
                caller.onPostExecutedGetSafeContentTask(safe.getString("safe"))
            } else {
                caller.onPostExecutedGetSafeContentTask(null)
            }
        } catch (e: Exception) {
            Log.d("onGetSafeContentTask", e.localizedMessage)
            caller.onPostExecutedGetSafeContentTask(null)
        }

    }
}
