package larc.ludiconprod.Utils.MainPageUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import larc.ludiconprod.Activities.CreateNewActivity;
import larc.ludiconprod.Adapters.MainActivity.AroundMeAdapter;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Event;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab1 extends Fragment {
    View v;
    ImageView heartImageAroundMe;
    TextView noActivitiesTextFieldAroundMe;
    TextView pressPlusButtonTextFieldAroundMe;
    ProgressBar progressBarAroundMe;
    public AroundMeAdapter fradapter;
    public  ListView frlistView;
    public ArrayList<Event> aroundMeEventList = new ArrayList<Event>();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.tab1,container,false);

        aroundMeEventList= Persistance.getInstance().getAroundMeActivities(getActivity());
        fradapter = new AroundMeAdapter(aroundMeEventList, getActivity().getApplicationContext(), getActivity(), getResources());
        updateListOfEventsAroundMe();

        return v;
    }

    public void updateListOfEventsAroundMe() {
        fradapter.notifyDataSetChanged();
        frlistView = (ListView) v.findViewById(R.id.events_listView2);
        heartImageAroundMe = (ImageView) v.findViewById(R.id.heartImageAroundMe);
        progressBarAroundMe = (ProgressBar) v.findViewById(R.id.progressBarAroundMe);
        noActivitiesTextFieldAroundMe = (TextView) v.findViewById(R.id.noActivitiesTextFieldAroundMe);
        pressPlusButtonTextFieldAroundMe = (TextView) v.findViewById(R.id.pressPlusButtonTextFieldAroundMe);
        frlistView.setAdapter(fradapter);
        heartImageAroundMe.setVisibility(View.INVISIBLE);
        noActivitiesTextFieldAroundMe.setVisibility(View.INVISIBLE);
        pressPlusButtonTextFieldAroundMe.setVisibility(View.INVISIBLE);

    }
}