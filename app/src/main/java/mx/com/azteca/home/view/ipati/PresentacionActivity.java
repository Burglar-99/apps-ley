package mx.com.azteca.home.view.ipati;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.azteca.home.R;

public class PresentacionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        android.support.v4.app.Fragment fragment = new android.support.v4.app.Fragment(){
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                return inflater.inflate(R.layout.fragment_presentacion,container,false);
            }
        };
        fragment.setRetainInstance(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layout_container, fragment).commit();
        super.onCreateActivity(PresentacionActivity.class);
    }
}
