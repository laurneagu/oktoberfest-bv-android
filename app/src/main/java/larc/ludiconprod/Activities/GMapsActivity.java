package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.GMapsCluster.AuthPlace;
import larc.ludiconprod.Utils.GMapsCluster.MultiDrawable;
import larc.ludiconprod.Utils.Location.ActivitiesLocationListener;
import larc.ludiconprod.Utils.util.AuthorizedLocation;
import larc.ludiconprod.ViewPagerHelper.MyFragment;
import larc.ludiconprod.ViewPagerHelper.MyPagerAdapter;

/**
 * Created by ancuta on 7/26/2017.
 */

public class GMapsActivity extends FragmentActivity implements PlaceSelectionListener, OnMapReadyCallback{

    RelativeLayout backButton;
    Marker lastAddedMarker;
    MapFragment mapFragment;
    private double selected_lat, selected_long;
    private LocationManager lm;
    public static Activity currentActivity;
    public static GoogleMap m_gmap;
    private ActivitiesLocationListener locationListener;
    double latitude=0;
    double longitude=0;
    public static ArrayList<AuthorizedLocation> authLocation=new ArrayList<AuthorizedLocation>();
    static public ArrayList<Marker> listOfMarkers =new ArrayList<Marker>();
    static DisplayMetrics dM;
    public static boolean locationSelected=false;
    public static Marker markerSelected;
    public static Marker myUnauthorizedMarker;
    String addressName="";

    public static int PAGES = 0;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;

    static public MyPagerAdapter adapter;
    static public ViewPager pager;
    static GMapsActivity context;
    static FragmentManager fragmentManager;
    static public boolean isFirstTime=false;
    Button selectLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            locationSelected=false;
            authLocation.clear();
            listOfMarkers.clear();
            dM=null;
            PAGES=0;
            adapter=null;
            pager=null;
            context=null;
            GMapsActivity.markerSelected=null;
            MyFragment.valueOfAuthorizedPlace=-1;
            myUnauthorizedMarker=null;

            fragmentManager=null;
            isFirstTime=false;
            m_gmap=null;
            currentActivity=this;
            super.onCreate(savedInstanceState);
            setContentView(R.layout.gmaps_activity);
            backButton = (RelativeLayout) findViewById(R.id.backButton);
            TextView titleText=(TextView) findViewById(R.id.titleText);
            selectLocationButton=(Button) findViewById(R.id.selectLocationButton) ;
            titleText.setText("Select Location");
            Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Medium.ttf");
            Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/Quicksand-Bold.ttf");

            mapFragment= (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            pager = (ViewPager) findViewById(R.id.locationListView);
            dM = getResources().getDisplayMetrics();
            context=this;
            fragmentManager=this.getSupportFragmentManager();



            backButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            selectLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(markerSelected != null) {
                        Intent intent = new Intent();
                        intent.putExtra("latitude", markerSelected.getPosition().latitude);
                        intent.putExtra("longitude",  markerSelected.getPosition().longitude);
                        intent.putExtra("AuthorizeEventLevel",MyFragment.valueOfAuthorizedPlace);
                        if(MyFragment.valueOfAuthorizedPlace != -1){
                            for(int i=0;i< authLocation.size();i++){
                                if(markerSelected.getPosition().latitude == authLocation.get(i).latitude && markerSelected.getPosition().longitude == authLocation.get(i).longitude){
                                    intent.putExtra("placeName",authLocation.get(i).name);
                                    intent.putExtra("address",authLocation.get(i).address);
                                    intent.putExtra("image",authLocation.get(i).image);
                                    intent.putExtra("ludicoins",authLocation.get(i).ludicoins);
                                    intent.putExtra("points",authLocation.get(i).points);
                                            break;
                                }
                            }
                        }else{
                            intent.putExtra("placeName","Unauthorized location");
                            intent.putExtra("address",addressName);
                        }


                        setResult(CreateNewActivity.ASK_COORDS_DONE, intent);
                        finish();
                    }
                    else{
                        Toast.makeText(currentActivity,"Please select a location!",Toast.LENGTH_LONG).show();
                    }
                }
            });





        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // Cluster code !
    private ClusterManager<AuthPlace> mClusterManager;
    private Random mRandom = new Random(1984);

    @Override
    public void onPlaceSelected(Place pl) {
        final Place place = pl;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))
                        .title(place.getName().toString()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            }
        });

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        latitude=Double.valueOf(getIntent().getStringExtra("latitude"));
        longitude=Double.valueOf(getIntent().getStringExtra("longitude"));


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                m_gmap = googleMap;
                if(markerSelected != null){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(markerSelected.getPosition().latitude, markerSelected.getPosition().longitude), 15));
                }else {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
                }
                LatLng sW = googleMap.getProjection().getVisibleRegion().nearLeft;
                LatLng nE=googleMap.getProjection().getVisibleRegion().farRight;
                getAuthLocation(sW,nE);
                m_gmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        if(!locationSelected) {

                            LatLng sW = googleMap.getProjection().getVisibleRegion().nearLeft;
                            LatLng nE = googleMap.getProjection().getVisibleRegion().farRight;
                            getAuthLocation(sW, nE);
                        }
                        locationSelected=false;
                    }
                });

                m_gmap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);


                        try {
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            if (addresses.size() > 0) {

                                    addressName = addresses.get(0).getAddressLine(0);

                            }
                        } catch (Exception exc) {
                            addressName = "Unknown";
                        }
                        if(myUnauthorizedMarker != null) {
                            myUnauthorizedMarker.remove();
                        }
                       myUnauthorizedMarker= m_gmap.addMarker(new MarkerOptions()
                                .position(new LatLng(latLng.latitude,latLng.longitude)).title("Location").snippet(addressName));
                        myUnauthorizedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected));
                        myUnauthorizedMarker.showInfoWindow();
                        if(MyFragment.valueOfAuthorizedPlace != -1){
                            switch (MyFragment.valueOfAuthorizedPlace) {
                                case 0:
                                    markerSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_normal));
                                    break;
                                case 1:
                                    markerSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2_normal));
                                    break;
                                case 2:
                                    markerSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3_normal));
                                    break;
                                case 3:
                                    markerSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_4_normal));
                                    break;
                            }
                        }
                        MyFragment.valueOfAuthorizedPlace = -1;
                        markerSelected=myUnauthorizedMarker;
                        locationSelected=true;

                    }
                });

            }
        });

    }

    public void getAuthLocation( LatLng sW,LatLng nE){
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(GMapsActivity.this).authKey);
        urlParams.put("latitudeNE", String.valueOf(nE.latitude+0.2));
        urlParams.put("longitudeNE", String.valueOf(nE.longitude+0.2));
        urlParams.put("latitudeSW", String.valueOf(sW.latitude-0.2));
        urlParams.put("longitudeSW", String.valueOf(sW.longitude-0.2));
        urlParams.put("sportCode",getIntent().getStringExtra("sportCode"));
        HTTPResponseController.getInstance().getAuthorizeLocations(params, headers, GMapsActivity.this,urlParams);
    }

    public static void putMarkers(ArrayList<AuthorizedLocation> authLocation){
        PAGES=authLocation.size();
        try {
            if (!isFirstTime && authLocation.size() > 0) {
                adapter = new MyPagerAdapter(context, fragmentManager, authLocation);
                pager.setAdapter(adapter);
                pager.setPageTransformer(false, adapter);

                // Set current item to the middle page so we can fling to both
                // directions left and right
                pager.setCurrentItem(FIRST_PAGE);

                // Necessary or the pager will only have one extra page to show
                // make this at least however many pages you can see
                pager.setOffscreenPageLimit(3);

                // Set margin for pages as a negative number, so a part of next and
                // previous pages will be showed

                int widthOfScreen = dM.widthPixels;
                int widthOfView = 240; //in DP
                int spaceBetweenViews =16; // in DP
                float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthOfView + spaceBetweenViews, dM);
                pager.setPageMargin((int) (0)); //for view on entire page,-witdhofScreen+offset for 3 custom vi
                isFirstTime = true;
            } else if(authLocation.size() > 0){
                adapter.notifyDataSetChanged();
            }


            for (int i = 0; i < listOfMarkers.size(); i++) {
                boolean markerExists = false;
                for (int j = 0; j < authLocation.size(); j++) {
                    if (listOfMarkers.get(i).getPosition().latitude == authLocation.get(j).latitude && listOfMarkers.get(i).getPosition().longitude == authLocation.get(j).longitude) {
                        markerExists = true;
                    }

                }
                if (!markerExists) {
                    listOfMarkers.get(i).remove();
                    listOfMarkers.remove(i);
                }
            }
            System.out.println(listOfMarkers.size() + " number of markers before");


            for (int i = 0; i < authLocation.size(); i++) {
                boolean markerExists = false;
                for (int j = 0; j < listOfMarkers.size(); j++) {
                    if (listOfMarkers.get(j).getPosition().latitude == authLocation.get(i).latitude && listOfMarkers.get(j).getPosition().longitude == authLocation.get(i).longitude) {
                        markerExists = true;
                    }

                }
                if (!markerExists) {
                    listOfMarkers.add(m_gmap.addMarker(new MarkerOptions()
                            .position(new LatLng(authLocation.get(i).latitude, authLocation.get(i).longitude))));
                    switch (authLocation.get(i).authorizeLevel) {
                        case 0:
                            listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_normal));
                            break;
                        case 1:
                            listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2_normal));
                            break;
                        case 2:
                            listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3_normal));
                            break;
                        case 3:
                            listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_4_normal));
                            break;
                    }
                }
            }

            System.out.println(listOfMarkers.size() + " number of markers after");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {

        finish();
    }

}
