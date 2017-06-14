package zavrsni.adnan.androbanking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import zavrsni.adnan.androbanking.common.Configuration;
import zavrsni.adnan.androbanking.tasks.IPostExecutedLoginTask;
import zavrsni.adnan.androbanking.tasks.LoginTask;


/**
 * Created by Adnan on 5/29/2017.
 */

public class LoginActivity extends Activity implements IPostExecutedLoginTask {
    private EditText username;
    private EditText password;
    private EditText pin;

    private Button loginBtn;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        pin = (EditText) findViewById(R.id.pinEditText);

        loginBtn = (Button) findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
//        if(username.getText().length() > 20 ||
//                username.getText().length() < 6 ||
//                password.getText().length() < 8 ||
//                password.getText().length() > 20 ||
//                pin.getText().length() > 4 ||
//                pin.getText().length() < 4) {
//            Toast.makeText(this, "Provjerite polja za unos", Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }
        String s_username = username.getText().toString();
        String s_pw = password.getText().toString();
        String s_pin = pin.getText().toString();

        new LoginTask(this, s_username, s_pw, s_pin)
                .execute();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Molimo sačekajte...");
        progressDialog.setCancelable(true);
        progressDialog.show();

    }

    //Ova metoda je pozvana kada je završena operacija prijave korisnika
    @Override
    public void onPostExecutedLoginTask(String token) {
        if(token != null) {
            //Poziv metode za enkriptovanje tokena i njegovo spremanje u Shared Preferences
            Configuration.encryptToSharedPrefs("jwt", token, this);
            Intent intent = new Intent(this, MainActivity.class);
            //Prelazi se u glavnu aktivnost, dok se trenutna aktivnost završava
            startActivity(intent);
            this.finish();
        }
        else {
            Toast.makeText(this, "Server nije dostupan ili neispravni korisnički podaci!", Toast.LENGTH_LONG)
                .show();
        }
        progressDialog.cancel();

    }
}
