package zavrsni.adnan.androbanking;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import zavrsni.adnan.androbanking.common.Configuration;
import zavrsni.adnan.androbanking.fragments.MainFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInitialFragment();
    }

    private void setInitialFragment() {
        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout, mainFragment, "init")
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
