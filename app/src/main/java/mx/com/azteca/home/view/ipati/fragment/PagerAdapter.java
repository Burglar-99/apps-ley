package mx.com.azteca.home.view.ipati.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> listFragment = new ArrayList<>();

    public PagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public int getItemPosition (Object object) {
        int index = listFragment.indexOf (object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView(listFragment.get(position).getView());
    }


    @Override
    public Fragment getItem(int position) {
        if (listFragment.size() == 0) {
            return null;
        }
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }

    public void addItem(Fragment fragment) {
        listFragment.add(fragment);
    }
}