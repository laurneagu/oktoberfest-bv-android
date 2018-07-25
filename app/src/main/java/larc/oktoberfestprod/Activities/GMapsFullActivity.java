/*
package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.GMapsCluster.AuthPlace;
import larc.ludiconprod.Utils.GMapsCluster.MultiDrawable;
import larc.ludiconprod.Utils.Location.ActivitiesLocationListener;
import larc.ludiconprod.Utils.util.Utils;

public class GMapsFullActivity extends Activity implements PlaceSelectionListener, OnMapReadyCallback, ClusterManager.OnClusterClickListener<AuthPlace>, ClusterManager.OnClusterInfoWindowClickListener<AuthPlace>, ClusterManager.OnClusterItemClickListener<AuthPlace>, ClusterManager.OnClusterItemInfoWindowClickListener<AuthPlace> {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private ActivitiesLocationListener locationListener;
    private LocationManager lm;

    private final GMapsFullActivity currAct = this;

    @Override
    public void onMapReady(GoogleMap map) {
        */
/*
        locationListener = new ActivitiesLocationListener(getApplication());
        locationListener.BindMap(map);

        if (ActivitiesLocationListener.hasSetPosition == true){
            SharedPreferences sharedPref = getApplication().getSharedPreferences("LocationPrefs", 0);
            String latString, longString;
            double latitude, longitude;
            latString = sharedPref.getString("curr_latitude", null);
            longString= sharedPref.getString("curr_longitude", null);

            latitude = Double.parseDouble(latString);
            longitude = Double.parseDouble(longString);

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude,longitude))
                    .title("You are here"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15));
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

        } catch (SecurityException exc) {
            exc.printStackTrace();
        }
        *//*

    }
    Marker lastAddedMarker;
    MapFragment mapFragment;
    private double selected_lat, selected_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);

            // Hide App bar
            // If the Android version is lower than Jellybean, use this call to hide
            // the status bar.
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            // remove title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.gmaps_activity);

        // Left side panel initializing -- DISABLED TEMPORARILYf
        //mDrawerList = (ListView) findViewById(R.id.leftMenu);
        //initializeLeftSidePanel();

       // User.setImage();

      // User picture and name for HEADER MENU
        Typeface segoeui = Typeface.createFromAsset(getAssets(), "fonts/seguisb.ttf");

        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()));
        userName.setTypeface(segoeui);

        TextView userSportsNumber = (TextView)findViewById(R.id.userSportsNumber);
        userSportsNumber.setText(User.getNumberOfSports(getApplicationContext()));
        userSportsNumber.setTypeface(segoeui);

     */
/*   ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);

            ImageView menuButton = (ImageView) findViewById(R.id.showPanel);
            menuButton.setVisibility(View.INVISIBLE);
            *//*

            
        mapFragment= (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        // Clear the map before
                        if (lastAddedMarker != null)
                            lastAddedMarker.remove();

                        // Get address name
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                        String addressName = "";
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            if (addresses.size() > 0) {
                                if (addressName == null || addressName.equals("")) {
                                    addressName = addresses.get(0).getAddressLine(0);
                                }
                            }
                        } catch (Exception exc) {
                            addressName = "Unknown";
                        }

                        // Add marker to the map
                        Marker selectedPoint = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(addressName)
                                .snippet("This event is not official! Tap to create!"));
                        selectedPoint.showInfoWindow();


                        // Set current marker as the last taped
                        lastAddedMarker = selectedPoint;

                        // Save values for latitude and longitude
                        selected_lat = latLng.latitude;
                        selected_long = latLng.longitude;

                        // Info window click
                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {

                                Intent intent = new Intent();
                                intent.putExtra("latitude", selected_lat);
                                intent.putExtra("longitude", selected_long);
                                intent.putExtra("isOfficial", 0);
                                intent.putExtra("comment", "Please note this is not an official event! You will get no points !");

                                // Save also this values
                                SharedPreferences sharedPref = currAct.getSharedPreferences("LocationPrefs", 0);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("sel_latitude", String.valueOf(selected_lat));
                                editor.putString("sel_longitude", String.valueOf(selected_long));
                                editor.commit();

                                setResult(CreateNewActivity.ASK_COORDS_DONE, intent);

                                // Sanity checks
                                lm = null;
                                locationListener =null;
                                finish();
                            }
                        });

                    }
                });
            }
        });



        ////////////////////////////
        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("");

        // this is it
        startDemo();
        }
        catch(Exception exc) {
            exc.printStackTrace();
            Utils.quit();
        }
    }


    @Override
    public void onBackPressed() {

        try {
            if(lm!=null)
             lm.removeUpdates(locationListener);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        // Sanity checks
        lm = null;
        locationListener =null;

        Intent intent = new Intent();
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        setResult(CreateNewActivity.ASK_COORDS_DONE, intent);

        finish();
    }

    // Left side menu

   */
/* public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_gmapsfull);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, GMapsFullActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), GMapsFullActivity.this);

        final ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
               // mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Toggle efect on left side panel
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }*//*



    */
/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*//*

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

    */
/**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     *//*

    private class PersonRenderer extends DefaultClusterRenderer<AuthPlace> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final TextView mClusterNumElems;
        private final int mDimension;

        public PersonRenderer(GoogleMap mapparam) {

            super(getApplicationContext(), mapparam, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            mClusterNumElems= (TextView) multiProfile.findViewById(R.id.nrClusters);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(AuthPlace authPlace, MarkerOptions markerOptions) {
            // Draw a single authPlace.
            // Set the info window to show their name.
            int picId = getResources().getIdentifier(authPlace.profilePhoto, "drawable", getPackageName());
            mImageView.setImageResource(picId);

            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(authPlace.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<AuthPlace> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            int uniquePics=0;

            for (AuthPlace p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;

                // Laur Neagu
                // Check if it is already added
                boolean sameId = false;
                int picId = getResources().getIdentifier(p.profilePhoto, "drawable", getPackageName());

                for (int i = 0; i < profilePhotos.size(); i++) {
                    Drawable currPhoto = profilePhotos.get(i);
                    // Already added
                    if (currPhoto.getLevel() == picId) {
                        sameId = true;
                        break;
                    }
                }
                if(!sameId) {

                    Drawable drawable = getResources().getDrawable(picId);

                    // Laur Neagu
                    drawable.setLevel(picId);
                    drawable.setBounds(0, 0, width, height);
                    profilePhotos.add(drawable);
                    uniquePics++;
                }
            }

            // resize the pics if it's 2 or 3
            if(uniquePics>1 && uniquePics<4){
                for (int i = 0; i < profilePhotos.size(); i++) {
                    Drawable drawable = profilePhotos.get(i);
                    drawable.setBounds(width/4, height/6, 2*width/3, 2*height/3);
                }
            }

            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            mClusterNumElems.setText(cluster.getSize()+"");
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<AuthPlace> cluster) {
        // Show a toast with some info when the cluster is clicked.
        Toast.makeText(this, "Here are " + cluster.getSize() + " locations ! Zoom in and select just one for the event !" , Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<AuthPlace> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(AuthPlace item) {

        // Laur Neagu
        //Toast.makeText(this, "Location :" + item.getPosition().longitude + " -- " + item.getPosition().latitude , Toast.LENGTH_SHORT).show();

        clickedClusterItem = item;
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(AuthPlace item) {
        // Does nothing, but you could go into the user's profile page, for example.

        Intent intent = new Intent();
        intent.putExtra("latitude", item.getPosition().latitude);
        intent.putExtra("longitude", item.getPosition().longitude);
        intent.putExtra("isOfficial", 1);
        intent.putExtra("address",item.name);
        intent.putExtra("comment", "This is an official event you create in " + item.name + " ! You will get points if you attend it!");

        // Save also this values
        SharedPreferences sharedPref = currAct.getSharedPreferences("LocationPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sel_latitude", String.valueOf(item.getPosition().latitude));
        editor.putString("sel_longitude", String.valueOf(item.getPosition().longitude));
        editor.commit();

        setResult(CreateNewActivity.ASK_COORDS_DONE, intent);

        // Sanity checks
        lm = null;
        locationListener =null;
        finish();
    }

    private GMapsFullActivity curr_context = this;
    private  double latitude=44.4333, longitude=26.1000;

    protected void startDemo() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                locationListener = new ActivitiesLocationListener(getApplication());
                locationListener.BindMap(googleMap);

                if (ActivitiesLocationListener.hasSetPosition == true){
                    SharedPreferences sharedPref = getApplication().getSharedPreferences("LocationPrefs", 0);
                    String latString, longString;

                    latString = sharedPref.getString("sel_latitude", null);
                    longString= sharedPref.getString("sel_longitude", null);

                    if(latString==null || longString==null) {
                        sharedPref = getApplication().getSharedPreferences("UserDetails", 0);

                        latString = sharedPref.getString("current_latitude", null);
                        longString= sharedPref.getString("current_longitude", null);

                    }

                    try{
                         latitude = Double.parseDouble(latString);
                         longitude = Double.parseDouble(longString);
                    }catch(NullPointerException e) {
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, latString + longString + "  Error", duration);
                        toast.show();
                    }

                    */
/*
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude,longitude))
                            .title("You are here"));
                    *//*

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15));
                }

                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                try {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

                } catch (SecurityException exc) {
                    exc.printStackTrace();
                }


                mClusterManager = new ClusterManager<>(getApplicationContext(), googleMap);
                googleMap.setOnCameraChangeListener(mClusterManager);
                googleMap.setOnMarkerClickListener(mClusterManager);
                googleMap.setOnInfoWindowClickListener(mClusterManager);

                // TEST
                googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

                mClusterManager.setRenderer(new PersonRenderer(googleMap));

                // Initialize everything
                mClusterManager.setOnClusterClickListener(curr_context);
                mClusterManager.setOnClusterInfoWindowClickListener(curr_context);

                addItems();
                mClusterManager.cluster();

                mClusterManager.setOnClusterItemClickListener(curr_context);
                mClusterManager.setOnClusterItemInfoWindowClickListener(curr_context);


                // Initialize autocomplete fragment

                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                        .build();
                autocompleteFragment.setFilter(typeFilter);

                autocompleteFragment.setOnPlaceSelectedListener(currAct);
                autocompleteFragment.setHint("Search for a location");
                autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(latitude - (0.01), longitude - (0.01)), new LatLng(latitude + (0.01), longitude + (0.01))));

            }
        });
    }

    private void addItems() {
        // Create the ArrayList of Authorized Places
        final ArrayList<AuthPlace> authorizedPlaces = new ArrayList<AuthPlace>();

        // Get and update total number of points for user in sport
        DatabaseReference authorizedPlacesRef = User.firebaseRef.child("authorized-place");
        authorizedPlacesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSN : snapshot.getChildren()) {

                    double latitude = 0;
                    double longitude = 0;
                    String name ="";
                    String details ="";
                    int priority=0;
                    ArrayList<String> sports = new ArrayList<String>();
                    ArrayList<Integer> points = new ArrayList<Integer>();

                    for (DataSnapshot data : dataSN.getChildren()) {

                            if (data.getKey() != null && data.getKey() != " ") {
                                if (data.getKey().equalsIgnoreCase("name")) {
                                    name = data.getValue().toString();
                                }

                                if (data.getKey().equalsIgnoreCase("details")) {
                                    details = data.getValue().toString();
                                }

                                if (data.getKey().equalsIgnoreCase("priority")) {
                                    priority = Integer.parseInt(data.getValue().toString());
                                }

                                if (data.getKey().equalsIgnoreCase("location")) {
                                    for (DataSnapshot dataLocation : data.getChildren()) {
                                        if(dataLocation.getKey() != null){
                                            if (dataLocation.getKey().equalsIgnoreCase("latitude"))
                                                latitude = Double.parseDouble(dataLocation.getValue().toString());
                                            if (dataLocation.getKey().equalsIgnoreCase("longitude"))
                                                longitude = Double.parseDouble(dataLocation.getValue().toString());
                                        }
                                    }
                                }

                                if (data.getKey().equalsIgnoreCase("sport")){
                                    for (DataSnapshot dataSport : data.getChildren()) {
                                        if(dataSport.getKey() != null){
                                            sports.add(dataSport.getKey().toString());
                                            points.add(Integer.parseInt(dataSport.getValue().toString()));
                                        }
                                    }
                                }

                           }
                    }

                    authorizedPlaces.add(new AuthPlace(new LatLng(latitude,longitude), name, details, sports.get(0), priority));

                }

                // Add them to the cluster !! :)
                mClusterManager.addItems(authorizedPlaces);

                // not here
                mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                        new MyCustomAdapterForItems());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }

    private AuthPlace clickedClusterItem;

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyCustomAdapterForItems() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.info_window_auth, null);
        }
        @Override
        public View getInfoWindow(Marker marker) {

            // Clear the map before
            if (lastAddedMarker != null)
                lastAddedMarker.remove();


            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.txtTitle));
            TextView tvSnippet = ((TextView) myContentsView
                    .findViewById(R.id.txtSnippet));
            TextView tvTapToCr = ((TextView) myContentsView
                    .findViewById(R.id.txtTapToCreate));

            tvTitle.setText(clickedClusterItem.name);
            tvSnippet.setText(clickedClusterItem.details);
            tvTapToCr.setText("Tap to create!");

            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
*/
