package zavrsni.adnan.androbanking.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import zavrsni.adnan.androbanking.R;

/**
 * Created by Adnan on 5/30/2017.
 */

public class MainFragment extends Fragment {

    private Button amountBtn;
    private Button transactionBtn;
    private Button safeBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        initializeViews(v);
        initializeEvents();

        return v;
    }

    private void initializeViews(View v) {
        amountBtn = (Button) v.findViewById(R.id.amountBtn);
        transactionBtn = (Button) v.findViewById(R.id.transactionBtn);
        safeBtn = (Button) v.findViewById(R.id.safeBtn);
    }

    private void initializeEvents() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.amountBtn:
                        addFragment(new AmountFragment());
                        break;
                    case R.id.transactionBtn:
                        addFragment(new TransactionFragment());
                        break;
                    case R.id.safeBtn:
                        addFragment(new SafeFragment());
                        break;
                }
            }
        };

        amountBtn.setOnClickListener(listener);
        transactionBtn.setOnClickListener(listener);
        safeBtn.setOnClickListener(listener);
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getActivity().getFragmentManager();
        fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout, fragment)
                        .addToBackStack(null)
                        .commit();

    }
}
