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
import zavrsni.adnan.androbanking.tasks.IPostExecutedTransactionTask;
import zavrsni.adnan.androbanking.tasks.TransactionTask;

/**
 * Created by Adnan on 5/30/2017.
 */

public class TransactionFragment extends Fragment implements IPostExecutedTransactionTask {

    private Button confirmBtn;
    private EditText accountNumberET;
    private EditText amountET;
    private String confirmPin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_transaction, container, false);
        initializeView(v);
        initializeEvents();

        return v;
    }

    private void initializeView(View v) {
        confirmBtn = (Button) v.findViewById(R.id.confirmTransactionBtn);
        accountNumberET = (EditText) v.findViewById(R.id.accNumberEditText);
        amountET = (EditText) v.findViewById(R.id.amountEditText);
    }

    private void initializeEvents() {
        final IPostExecutedTransactionTask delegate = this;
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountNumberET.getText().length() != 16 ||
                        amountET.getText().length() == 0) {
                    return;
                }

                final String accountNumber = accountNumberET.getText().toString();
                final Double amount = Double.parseDouble(amountET.getText().toString());

                final EditText inputPin = new EditText(((Fragment)delegate).getActivity());
                inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                AlertDialog.Builder builder = new AlertDialog.Builder(((Fragment)delegate).getActivity());
                builder.setTitle("Unesite vaš PIN za potvrdu");
                builder.setView(inputPin);
                builder.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmPin = inputPin.getText().toString();
                        new TransactionTask(delegate, amount, accountNumber, Integer.parseInt(confirmPin))
                                .execute();
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

    private void resetForm() {
        accountNumberET.setText("");
        amountET.setText("");
    }

    @Override
    public void onPostExecutedTransactionTask(Boolean success) {
        if(success) {
            Toast.makeText(getActivity(), "Transakcija je uspješno izvršena! Provjerite stanje na računu", Toast.LENGTH_LONG)
                    .show();
            resetForm();
        }
        else {
            Toast.makeText(this.getActivity(), "Transakciju je nemoguće izvršiti. Molimo Vas da pokušate kasnije.", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
