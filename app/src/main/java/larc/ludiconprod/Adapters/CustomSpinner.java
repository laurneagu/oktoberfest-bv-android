package larc.ludiconprod.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import larc.ludiconprod.R;

/**
 * Created by ancuta on 7/31/2017.
 */

public class CustomSpinner extends BaseAdapter {
    Context context;
    int images[];
    String[] footballNames;
    LayoutInflater inflater;

    public CustomSpinner(Context applicationContext, int[] images, String[] footballNames) {
        this.context = applicationContext;
        this.images = images;
        this.footballNames = footballNames;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_items, null);
        ImageView icon = (ImageView) view.findViewById(R.id.sportImage);
        TextView names = (TextView) view.findViewById(R.id.sportText);
        icon.setImageResource(images[i]);
        names.setText(footballNames[i]);
        return view;
    }
}
