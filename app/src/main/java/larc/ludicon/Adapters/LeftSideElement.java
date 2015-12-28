package larc.ludicon.Adapters;

import java.util.ArrayList;
import java.util.List;

import larc.ludicon.R;

/**
 * Created by LaurUser on 12/28/2015.
 */
public class LeftSideElement {
    public static List<LeftSidePanelElements> Init(){
        List<LeftSidePanelElements> elems =  new ArrayList<LeftSidePanelElements>();

        String[] elemNames = new String[]{"Statistics", "Friends", "Chats", "Rankings", "Settings"};
        int[] elemPicRes = new int[]{R.drawable.stats, R.drawable.friends,  R.drawable.chat, R.drawable.ranks, R.drawable.settings };

        for(int i = 0; i < 5 ; i++ ){
            elems.add(new LeftSidePanelElements(elemNames[i],elemPicRes[i]));
        }

        return elems;
    }
}