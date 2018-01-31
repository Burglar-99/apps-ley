package mx.com.azteca.home.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;


public abstract class ListAdapter extends BaseAdapter {

    private List<?> entry;
    private int R_layout_IdView;
    private Context context;

    public ListAdapter(Context context, int R_layout_IdView, List<?> entry) {
        super();
        this.context = context;
        this.entry = entry;
        this.R_layout_IdView = R_layout_IdView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        onEntry(entry.get(position), view, position);
        return view;
    }

    @Override
    public int getCount() {
        return entry.size();
    }

    @Override
    public Object getItem(int position) {
        return entry.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract void onEntry(Object entry, View view, int position);

    public List<?> getEntryData() {
        return entry;
    }

}