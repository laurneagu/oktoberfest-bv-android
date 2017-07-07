package larc.ludiconprod.Gamification;

import larc.ludiconprod.LocationHelper.LocationChecker;

/**
 * Created by LaurUser on 7/6/2017.
 */

public class RewardLayer {

    // Singleton
    private static RewardLayer instance = null;
    protected RewardLayer() {
    }
    public static RewardLayer getInstance() {
        if(instance == null) {
            instance = new RewardLayer();
        }
        return instance;
    }

    public int rewardPointsByEventPriority(int priority) {
        if (priority == 0) {
            return 1;
        } else {
            return 2;
        }
    }

}
