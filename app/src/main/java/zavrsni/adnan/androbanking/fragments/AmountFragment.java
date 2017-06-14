package zavrsni.adnan.androbanking.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import zavrsni.adnan.androbanking.R;
import zavrsni.adnan.androbanking.tasks.GetAmountTask;
import zavrsni.adnan.androbanking.tasks.IPostExecutedGetAmountTask;

/**
 * Created by Adnan on 5/30/2017.
 */

public class AmountFragment extends Fragment implements IPostExecutedGetAmountTask {
    private TextView amountTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_amount, container, false);

        initializeView(v);

        //po pokretanju fragmenta
        //pokreće se i operacija dobavljanja stanja računa
        new GetAmountTask(this).execute();

        return v;
    }

    private void initializeView(View v) {
        amountTV = (TextView) v.findViewById(R.id.amountTV);
        amountTV.setText("");
    }

    @Override
    public void onPostExecutedGetAmountTask(String amount) {
        if(amount != null) {
            amountTV.setText(amount);
        }
        else {
            amountTV.setText("Problem sa dobavljanjem stanja računa!");
        }

    }
}
