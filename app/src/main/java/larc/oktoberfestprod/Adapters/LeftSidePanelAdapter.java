/*
package larc.ludiconprod.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;

*/
/**
 * Created by LaurUser on 12/28/2015.
 *//*

public class LeftSidePanelAdapter  extends BaseAdapter implements View.OnClickListener {

    private Context m_context;
    private Activity m_activity;
    private List<LeftSidePanelElements> m_listElements;

    public LeftSidePanelAdapter(Context i_context, Activity i_activity) {
        m_context = i_context;
        m_activity = i_activity;
        m_listElements = LeftSideElement.Init();
    }

    public int getCount() {
        return m_listElements.size();
    }

    public Object getItem(int position) {
        return m_listElements.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final LeftSidePanelElements entry = m_listElements.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) m_context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.leftside_elem, null);
        }

        Typeface segoeui = Typeface.createFromAsset(m_activity.getAssets(), "fonts/seguisb.ttf");

        TextView entry_name = (TextView) convertView.findViewById(R.id.currElemName);
        entry_name.setText(entry.getName());
        entry_name.setTypeface(segoeui);

        ImageView entry_pic = (ImageView)convertView.findViewById(R.id.elemPic);
        entry_pic.setImageResource(entry.getPictureId());
        entry_pic.setScaleType(ImageView.ScaleType.FIT_XY);
        return convertView;
    }

    @Override
    public void onClick(View view) {
    }
}*/
