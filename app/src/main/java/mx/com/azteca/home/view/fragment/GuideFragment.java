package mx.com.azteca.home.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import mx.com.azteca.home.R;

public class GuideFragment extends Fragment {

    private int image;

    public static GuideFragment newInstance(int image) {
        GuideFragment fragmentFirst = new GuideFragment();
        Bundle args = new Bundle();
        args.putInt("image", image);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.image = getArguments().getInt("image");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        if (view != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setBackgroundResource(image);
        }
        return view;
    }

}

