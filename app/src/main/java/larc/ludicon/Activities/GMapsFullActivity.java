package larc.ludicon.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import larc.ludicon.Adapters.LeftPanelItemClicker;
import larc.ludicon.Adapters.LeftSidePanelAdapter;
import larc.ludicon.R;
import larc.ludicon.UserInfo.User;
import larc.ludicon.Utils.GMapsCluster.MultiDrawable;
import larc.ludicon.Utils.GMapsCluster.Person;
import larc.ludicon.Utils.Location.ActivitiesLocationListener;

public class GMapsFullActivity extends Activity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<Person>, ClusterManager.OnClusterInfoWindowClickListener<Person>, ClusterManager.OnClusterItemClickListener<Person>, ClusterManager.OnClusterItemInfoWindowClickListener<Person> {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private ActivitiesLocationListener locationListener;
    private LocationManager lm;

    @Override
    public void onMapReady(GoogleMap map) {
        locationListener = new ActivitiesLocationListener(getApplication());
        locationListener.BindMap(map);

        if (ActivitiesLocationListener.hasSetPosition == true){
            SharedPreferences sharedPref = getApplication().getSharedPreferences("LocationPrefs", 0);
            String latString, longString;
            double latitude=0, longitude=0;
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

    }
    MapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmaps_full);

        // Left side panel initializing
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);


        mapFragment= (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent();
                intent.putExtra("latitude", latLng.latitude);
                intent.putExtra("longitude", latLng.longitude);

                setResult(CreateNewActivity.ASK_COORDS_DONE, intent);

                // Sanity checks
                lm = null;
                locationListener =null;
                finish();
            }
        });

        ////////////////////////////
        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("Pick location");

        // this is it
        startDemo();
    }


    @Override
    public void onBackPressed() {

        try {
            lm.removeUpdates(locationListener);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        // Sanity checks
        lm = null;
        locationListener =null;
        finish();
    }

    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_gmapsfull);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, GMapsFullActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), GMapsFullActivity.this);

        final ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mDrawerLayout.openDrawer(Gravity.LEFT);
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

    }

    @Override
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

    // Cluster code !
    private ClusterManager<Person> mClusterManager;
    private Random mRandom = new Random(1984);

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PersonRenderer extends DefaultClusterRenderer<Person> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final TextView mClusterNumElems;
        private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), mapFragment.getMap(), mClusterManager);

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
        protected void onBeforeClusterItemRendered(Person person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            int uniquePics=0;

            for (Person p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;


                // Laur Neagu
                // Check if it is already added
                boolean sameId = false;
                for (int i = 0; i < profilePhotos.size(); i++) {
                    Drawable currPhoto = profilePhotos.get(i);
                    // Already added
                    if (currPhoto.getLevel() == p.profilePhoto) {
                        sameId = true;
                        break;
                    }
                }
                if(!sameId) {

                    Drawable drawable = getResources().getDrawable(p.profilePhoto);
                    // Laur Neagu
                    drawable.setLevel(p.profilePhoto);
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
    public boolean onClusterClick(Cluster<Person> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Person> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(Person item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Person item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    protected void startDemo() {
        mClusterManager = new ClusterManager<Person>(this, mapFragment.getMap());
        mClusterManager.setRenderer(new PersonRenderer());
        mapFragment.getMap().setOnCameraChangeListener(mClusterManager);
        mapFragment.getMap().setOnMarkerClickListener(mClusterManager);
        mapFragment.getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    private void addItems() {

        // TODO must take them from Firebase
        mClusterManager.addItem(new Person(new LatLng(44.4367192,26.0874813), "Parc Cismigiu", R.drawable.cycling));

        mClusterManager.addItem(new Person(new LatLng(44.4057588,26.1404763), "Squash 4 All", R.drawable.squash));

        mClusterManager.addItem(new Person(new LatLng(44.4373334,26.0537076), "Baza Sportivă Libra Phoenix Sport", R.drawable.tennis));

        mClusterManager.addItem(new Person(new LatLng(44.4122762,26.0314658), "Stadion Ghencea", R.drawable.football));

        mClusterManager.addItem(new Person(new LatLng(44.4057214,26.103872), "Parc Tineretului", R.drawable.jogging));

        mClusterManager.addItem(new Person(new LatLng(44.4465908,26.1544343), "Terenurile Delfin Arena", R.drawable.football));

        mClusterManager.addItem(new Person(new LatLng(44.44318,26.1443157), "Centrul Național de Tenis", R.drawable.tennis));

        mClusterManager.addItem(new Person(new LatLng(44.4057214,26.103872), "Parc Tineretului", R.drawable.volley));

        mClusterManager.addItem(new Person(new LatLng(44.4195959,26.1548647), "Parc Titan", R.drawable.tennis));
    }

    private LatLng position() {
        return new LatLng(random(44.4529505,44.4119505), random(26.0522823, 26.0112823));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
