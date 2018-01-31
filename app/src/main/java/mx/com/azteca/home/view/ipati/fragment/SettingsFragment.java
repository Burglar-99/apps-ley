package mx.com.azteca.home.view.ipati.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import mx.com.azteca.home.R;


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_user);
    }

}
