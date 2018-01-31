package mx.com.azteca.home.view;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;

import com.pixelcan.inkpageindicator.InkPageIndicator;

import github.chenupt.springindicator.viewpager.ScrollerViewPager;
import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.CUsuario;
import mx.com.azteca.home.view.fragment.GuideFragment;
import mx.com.azteca.home.view.ipati.DocManagerActivity;


public class GuideActivity extends BaseActivity {

    private Button btnIniciar;
    private ScrollerViewPager viewPager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        this.btnIniciar = (Button) findViewById(R.id.btnIniciar);
        if (btnIniciar != null) {
            this.btnIniciar.setOnClickListener(view -> {
                try {
                    if (btnIniciar.getText().toString().equals("Iniciar")) {
                        super.setData("GuideCompleted", true);
                      //  super.iniciarNewThread(DocManagerActivity.class);
                        super.iniciarNewThread(MapActivity.class);
                    }
                    else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                }
                catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            });
        }

        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        InkPageIndicator inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        GuideAdapter adapter = new GuideAdapter(getSupportFragmentManager());
        if (viewPager != null && inkPageIndicator != null) {
            viewPager.setAdapter(adapter);
            viewPager.fixScrollSpeed();
            viewPager.setOffscreenPageLimit(adapter.getCount());
            inkPageIndicator.setViewPager(viewPager);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    btnIniciar.setText( position == 3 ? "Iniciar" : "Continuar");
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            super.onCreateActivity(CUsuario.class);
        }
    }

    private static class GuideAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        private GuideAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GuideFragment.newInstance(R.mipmap.img_guide);
                case 1:
                    return GuideFragment.newInstance(R.mipmap.img_guide);
                case 2:
                    return GuideFragment.newInstance(R.mipmap.img_guide);
                case 3:
                    return GuideFragment.newInstance(R.mipmap.img_guide);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
