package larc.ludiconprod.Utils.GMapsCluster;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by LaurUser on 6/2/2016.
 */
public class AuthPlace implements ClusterItem {
    public final String name;
    public String details;
    public final String profilePhoto;
    public int priority=5;
    private final LatLng mPosition;

    public AuthPlace(LatLng position, String i_name, String pictureResource) {
        name = i_name;
        profilePhoto = pictureResource;
        mPosition = position;
    }

    public AuthPlace(LatLng position, String i_name, String i_details, String pictureResource, int i_priority) {
        name = i_name;
        details = i_details;
        profilePhoto = pictureResource;
        mPosition = position;
        priority = i_priority;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
