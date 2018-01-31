package mx.com.azteca.home.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.R;

public class Gallery extends LinearLayout {

    private List<GalleryItem> listItems;

    private ViewPager viewPager;

    private Context context;


    private MainPagerAdapter pagerAdapter = null;


    public Gallery(Context context, FragmentManager fragmentManager) {
        super(context);
        this.context = context;

        LinearLayout layout = (LinearLayout) View.inflate(context, R.layout.widget_gallery,null);
        this.viewPager = (ViewPager) layout.findViewById(R.id.pager);

        layout.findViewById(R.id.btnAdd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        pagerAdapter = new MainPagerAdapter();
        this.viewPager.setAdapter(pagerAdapter);

        this.addView(layout);
    }

    public void setImages(final List<GalleryItem> listItems) {
        this.listItems = listItems;

        for (int contador = 0; contador < pagerAdapter.getCount(); contador++) {
            pagerAdapter.removeView(viewPager, contador);
            pagerAdapter.notifyDataSetChanged();
        }

        for (final GalleryItem item : listItems) {
            View rootView = View.inflate(context, R.layout.fragment_gallery_main, null);

            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1500));

            TextView lblTitulo = (TextView) rootView.findViewById(R.id.textView);
            lblTitulo.setText(item.getTitle());

            Glide.with(getContext())
                    .load(item.getPath())
                    .into((ImageView) rootView.findViewById(R.id.imageView));

            rootView.findViewById(R.id.imageView).setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    modificar(listItems.indexOf(item));
                    return false;
                }
            });

            pagerAdapter.addView (rootView, 0);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    public void modificar(int index) {
    }


    public class GalleryItem  {

        private String title;
        private String path;
        private Object data;
        public int index;


        public void setTitle(String title) {
            this.title = title;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getTitle() {
            return title;
        }

        public String getPath() {
            return path;
        }

        public Object getData() {
            return data;
        }
    }

    protected void addItem() {
    }

    public void clearItems() {
        listItems.clear();

    }


    public class MainPagerAdapter extends PagerAdapter
    {
        // This holds all the currently displayable views, in order from left to right.
        private ArrayList<View> views = new ArrayList<View>();

        //-----------------------------------------------------------------------------
        // Used by ViewPager.  "Object" represents the page; tell the ViewPager where the
        // page should be displayed, from left-to-right.  If the page no longer exists,
        // return POSITION_NONE.
        @Override
        public int getItemPosition (Object object)
        {
            int index = views.indexOf (object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

        //-----------------------------------------------------------------------------
        // Used by ViewPager.  Called when ViewPager needs a page to display; it is our job
        // to add the page to the container, which is normally the ViewPager itself.  Since
        // all our pages are persistent, we simply retrieve it from our "views" ArrayList.
        @Override
        public Object instantiateItem (ViewGroup container, int position)
        {
            View v = views.get (position);
            container.addView (v);
            return v;
        }

        //-----------------------------------------------------------------------------
        // Used by ViewPager.  Called when ViewPager no longer needs a page to display; it
        // is our job to remove the page from the container, which is normally the
        // ViewPager itself.  Since all our pages are persistent, we do nothing to the
        // contents of our "views" ArrayList.
        @Override
        public void destroyItem (ViewGroup container, int position, Object object)
        {
            container.removeView (views.get (position));
        }

        //-----------------------------------------------------------------------------
        // Used by ViewPager; can be used by app as well.
        // Returns the total number of pages that the ViewPage can display.  This must
        // never be 0.
        @Override
        public int getCount ()
        {
            return views.size();
        }

        //-----------------------------------------------------------------------------
        // Used by ViewPager.
        @Override
        public boolean isViewFromObject (View view, Object object)
        {
            return view == object;
        }

        //-----------------------------------------------------------------------------
        // Add "view" to right end of "views".
        // Returns the position of the new view.
        // The app should call this to add pages; not used by ViewPager.
        public int addView (View v)
        {
            return addView (v, views.size());
        }

        //-----------------------------------------------------------------------------
        // Add "view" at "position" to "views".
        // Returns position of new view.
        // The app should call this to add pages; not used by ViewPager.
        public int addView (View v, int position)
        {
            views.add (position, v);
            return position;
        }

        //-----------------------------------------------------------------------------
        // Removes "view" from "views".
        // Retuns position of removed view.
        // The app should call this to remove pages; not used by ViewPager.
        public int removeView (ViewPager pager, View v)
        {
            return removeView (pager, views.indexOf (v));
        }

        //-----------------------------------------------------------------------------
        // Removes the "view" at "position" from "views".
        // Retuns position of removed view.
        // The app should call this to remove pages; not used by ViewPager.
        public int removeView (ViewPager pager, int position)
        {
            // ViewPager doesn't have a delete method; the closest is to set the adapter
            // again.  When doing so, it deletes all its views.  Then we can delete the view
            // from from the adapter and finally set the adapter to the pager again.  Note
            // that we set the adapter to null before removing the view from "views" - that's
            // because while ViewPager deletes all its views, it will call destroyItem which
            // will in turn cause a null pointer ref.
            pager.setAdapter (null);
            views.remove (position);
            pager.setAdapter (this);

            return position;
        }

        //-----------------------------------------------------------------------------
        // Returns the "view" at "position".
        // The app should call this to retrieve a view; not used by ViewPager.
        public View getView (int position)
        {
            return views.get (position);
        }

        // Other relevant methods:

        // finishUpdate - called by the ViewPager - we don't care about what pages the
        // pager is displaying so we don't use this method.
    }

}