package larc.ludiconprod.Utils.LeaderboardUtils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import larc.ludiconprod.Activities.LeaderboardActivity;
import larc.ludiconprod.Adapters.Leaderboard.LeaderboardAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.UserPosition;

/**
 * Created by alex_ on 23.08.2017.
 */

public class LeaderboardTab extends Fragment {

    private ArrayList<UserPosition> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.leaderboard_tab, container, false);

        try {
            new LeaderboardAdapter(this.users, v, (LeaderboardActivity) getParentFragment());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }
}
