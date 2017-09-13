package larc.ludiconprod.ViewPagerHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import larc.ludiconprod.Activities.GMapsActivity;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.util.AuthorizedLocation;

import static com.facebook.FacebookSdk.getApplicationContext;
import static larc.ludiconprod.Activities.GMapsActivity.authLocation;
import static larc.ludiconprod.Activities.GMapsActivity.listOfMarkers;
import static larc.ludiconprod.Activities.GMapsActivity.m_gmap;
import static larc.ludiconprod.Activities.GMapsActivity.markerSelected;
import static larc.ludiconprod.Activities.GMapsActivity.myUnauthorizedMarker;

public class MyFragment extends Fragment {

    public static int valueOfAuthorizedPlace=-1;
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Fragment newInstance(GMapsActivity context, int pos, float scale, AuthorizedLocation authorizedLocations) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putString("companyImage",authorizedLocations.image);
        b.putString("locationName",authorizedLocations.name);
        b.putString("adress",authorizedLocations.address);
        b.putInt("ludicoins",authorizedLocations.ludicoins);
        b.putInt("points",authorizedLocations.points);
        b.putFloat("scale", scale);
        b.putDouble("latitude",authorizedLocations.latitude);
        b.putDouble("longitude",authorizedLocations.longitude);
        b.putInt("authorizeLevel",authorizedLocations.authorizeLevel);
        b.putString("schedule",authorizedLocations.schedule);
        b.putString("phoneNumber",authorizedLocations.phoneNumber);
        return Fragment.instantiate(context, MyFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout l = (LinearLayout)
                inflater.inflate(R.layout.map_selection_location_card, container, false);

        TextView schedule=(TextView)l.findViewById(R.id.schedule);
        schedule.setText("");
        TextView phoneNumber=(TextView) l.findViewById(R.id.phoneNumber);
        phoneNumber.setText("");
        //clear adapter

        SpannableStringBuilder spanTxt = new SpannableStringBuilder("");
        final String phoneNumbers[]=this.getArguments().getString("phoneNumber").split(",");
        for(int i=0;i < phoneNumbers.length;i++) {
            spanTxt.append(phoneNumbers[i]);
            final int currentIndex=i;
            spanTxt.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_DIAL , Uri.parse("tel:" + phoneNumbers[currentIndex]));
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            }, spanTxt.length() - phoneNumbers[currentIndex].length(), spanTxt.length(), 0);
            spanTxt.append(" ");
            spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), spanTxt.length()-1, spanTxt.length(), 0);
        }
        phoneNumber.setMovementMethod(LinkMovementMethod.getInstance());
        phoneNumber.setText(spanTxt, TextView.BufferType.SPANNABLE);

        schedule.setText(this.getArguments().getString("schedule"));


        int pos = this.getArguments().getInt("pos");
        String location=this.getArguments().getString("locationName");
        TextView locationName=(TextView)l.findViewById(R.id.locationName) ;
        locationName.setText(location);
        TextView adress=(TextView)l.findViewById(R.id.adress);
        adress.setText(this.getArguments().getString("adress"));
        ImageView companyImage=(ImageView)l.findViewById(R.id.companyImage);
        if(!this.getArguments().getString("companyImage").equals("")){
            Bitmap bitmap=decodeBase64(this.getArguments().getString("companyImage"));
            companyImage.setImageBitmap(bitmap);
        }
        TextView ludicoinsNumber=(TextView)l.findViewById(R.id.ludicoinsNumber);
        ludicoinsNumber.setText(" +"+String.valueOf(this.getArguments().getInt("ludicoins")));
        TextView pointsNumber=(TextView)l.findViewById(R.id.pointsNumber);
        pointsNumber.setText(" +"+String.valueOf(this.getArguments().getInt("points")));

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<listOfMarkers.size();i++){
                    if(listOfMarkers.get(i).getPosition().latitude == MyFragment.this.getArguments().getDouble("latitude") && listOfMarkers.get(i).getPosition().longitude == MyFragment.this.getArguments().getDouble("longitude")){
                        m_gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyFragment.this.getArguments().getDouble("latitude"),MyFragment.this.getArguments().getDouble("longitude")), 15));
                        GMapsActivity.locationSelected=true;
                        if(myUnauthorizedMarker != null) {
                            myUnauthorizedMarker.remove();

                        }
                        switch (MyFragment.this.getArguments().getInt("authorizeLevel")) {
                            case 0:
                                listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1_selected));
                                valueOfAuthorizedPlace=0;
                                break;
                            case 1:
                                listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2_selected));
                                valueOfAuthorizedPlace=1;
                                break;
                            case 2:
                                listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3_selected));
                                valueOfAuthorizedPlace=2;
                                break;
                            case 3:
                                listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_4_selected));
                                valueOfAuthorizedPlace=3;
                                break;
                        }
                        markerSelected=listOfMarkers.get(i);
                    }
                    else{
                        for(int j=0;j<authLocation.size();j++){
                            int authLevel=authLocation.get(i).authorizeLevel;
                            switch (authLevel) {
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
                }
            }
        });





        MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return l;
    }
}