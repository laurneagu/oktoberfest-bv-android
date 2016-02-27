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

        String[] elemNames = new String[]{"New Activity", "Statistics", "Friends", "Chats", "Rankings", "Settings"};
        int[] elemPicRes = new int[]{R.drawable.lsp1,R.drawable.lsp2, R.drawable.lsp3,  R.drawable.lsp4, R.drawable.lsp5, R.drawable.lsp6 };

        for(int i = 0; i < 6 ; i++ ){
            elems.add(new LeftSidePanelElements(elemNames[i],elemPicRes[i]));
        }

        return elems;
    }
}