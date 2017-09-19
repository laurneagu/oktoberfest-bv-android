package larc.ludiconprod.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;

/**
 * Created by ancuta on 9/8/2017.
 */

public class PointsReceivedDialog extends DialogFragment {

    public  PointsReceivedDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater


        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.dialog_save_points, null);

        Button okayButton = (Button)v.findViewById(R.id.okayButton);
        TextView levelUPText = (TextView)v.findViewById(R.id.levelUPText);
        TextView ludicoinsForNoUP = (TextView)v.findViewById(R.id.ludicoinsForNoUP);
        ImageView ludicoinsImageForNoUp = (ImageView)v.findViewById(R.id.ludicoinsImageForNoUp);
        TextView gainedText = (TextView) v.findViewById(R.id.gainedText);
        ImageView ludicoinsImage = (ImageView) v.findViewById(R.id.ludicoinsImage);
        TextView pointsText = (TextView) v.findViewById(R.id.pointsText);
        ImageView pointsImage = (ImageView) v.findViewById(R.id.pointsImage);
        TextView ludicoinsText = (TextView) v.findViewById(R.id.ludicoinsText);
        TextView levelText = (TextView) v.findViewById(R.id.levelText);
        int ludicoins = getArguments().getInt("ludicoins");
        int points = getArguments().getInt("points");
        int level = getArguments().getInt("level");
        String message = getArguments().getString("message");

        if (message != null) {
            levelUPText.setText("You gained:");
            ludicoinsForNoUP.setText("+" + String.valueOf(ludicoins));
            v.findViewById(R.id.ludicoinsLayout).getLayoutParams().height = 0;

            ViewGroup.LayoutParams paramsludicoinsForNoUP = ludicoinsForNoUP.getLayoutParams();
            paramsludicoinsForNoUP.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            paramsludicoinsForNoUP.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            ludicoinsForNoUP.setLayoutParams(paramsludicoinsForNoUP);

            ViewGroup.LayoutParams paramsludicoinsImageForNoUp = ludicoinsImageForNoUp.getLayoutParams();
            paramsludicoinsImageForNoUp.height =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
            paramsludicoinsImageForNoUp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
            ludicoinsImageForNoUp.setLayoutParams(paramsludicoinsImageForNoUp);
        } else if (level == Persistance.getInstance().getUserInfo(getActivity()).level) {
            levelUPText.setText("You gained:");

            ViewGroup.LayoutParams paramsludicoinsForNoUP = ludicoinsForNoUP.getLayoutParams();
            paramsludicoinsForNoUP.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            paramsludicoinsForNoUP.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            ludicoinsForNoUP.setLayoutParams(paramsludicoinsForNoUP);

            ViewGroup.LayoutParams paramsludicoinsImageForNoUp = ludicoinsImageForNoUp.getLayoutParams();
            paramsludicoinsImageForNoUp.height =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
            paramsludicoinsImageForNoUp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
            ludicoinsImageForNoUp.setLayoutParams(paramsludicoinsImageForNoUp);

            ViewGroup.LayoutParams paramsludicoinsText = ludicoinsText.getLayoutParams();
            paramsludicoinsText.height = 0;
            paramsludicoinsText.width = 0;
            ludicoinsText.setLayoutParams(paramsludicoinsText);

            ViewGroup.LayoutParams paramsludicoinsImage = ludicoinsImage.getLayoutParams();
            paramsludicoinsImage.height = 0;
            paramsludicoinsImage.width =0;
            ludicoinsImage.setLayoutParams(paramsludicoinsImage);

            ludicoinsForNoUP.setText("+" + String.valueOf(ludicoins));
            gainedText.setText("And:  ");
            pointsText.setText("+" + points + " ");
        } else {
            ludicoinsText.setText("+" + String.valueOf(ludicoins));
            pointsText.setText("+" + String.valueOf(points));
        }
        if (message == null) {
            levelText.setText("Level " + String.valueOf(level));
        } else {
            levelText.setText(message);
        }
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointsReceivedDialog.this.getDialog().cancel();
            }
        });
        // Create the AlertDialog object and return it
        builder.setView(v);
        return builder.create();
    }
}