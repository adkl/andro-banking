package zavrsni.adnan.androbanking.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import zavrsni.adnan.androbanking.R;
import zavrsni.adnan.androbanking.tasks.GetAmountTask;
import zavrsni.adnan.androbanking.tasks.GetSafeContentTask;
import zavrsni.adnan.androbanking.tasks.IPostExecutedGetSafeContentTask;
import zavrsni.adnan.androbanking.tasks.IPostExecutedSetSafeContentTask;
import zavrsni.adnan.androbanking.tasks.SetSafeContentTask;

/**
 * Created by Adnan on 5/30/2017.
 */

public class SafeFragment extends Fragment implements IPostExecutedGetSafeContentTask, IPostExecutedSetSafeContentTask {

    private Button confirmBtn;
    private EditText safeContentEditText;
    private String confirmPin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_safe, container, false);
        initializeView(v);
        initializeEvents();

        new GetSafeContentTask(this).execute();

        return v;
    }

    private void initializeView(View v) {
        confirmBtn = (Button) v.findViewById(R.id.confirmSafeEditBtn);
        safeContentEditText = (EditText) v.findViewById(R.id.safeEditText);
    }

    private void initializeEvents() {
        final IPostExecutedSetSafeContentTask delegate = this;
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText inputPin = new EditText(((Fragment)delegate).getActivity());
                inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                AlertDialog.Builder builder = new AlertDialog.Builder(((Fragment)delegate).getActivity());
                builder.setTitle("Unesite vaš PIN za potvrdu");
                builder.setView(inputPin);
                builder.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmPin = inputPin.getText().toString();
                        new SetSafeContentTask(delegate, safeContentEditText.getText().toString(), Integer.parseInt(confirmPin)).execute();
                    }
                });
                builder.setNegativeButton("Prekini", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
    }


    @Override
    public void onPostExecutedGetSafeContentTask(String content) {
        if(content != null) {
            safeContentEditText.setText(content);
        }
        else {
            Toast.makeText(this.getActivity(), "Problem u komunikaciji sa serverom!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPostExecutedSetSafeContentTask(Boolean success) {
        if(success) {
            Toast.makeText(this.getActivity(), "Uspješno izmijenjeno!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this.getActivity(), "Problem u komunikaciji sa serverom!", Toast.LENGTH_LONG).show();
        }
    }
}
