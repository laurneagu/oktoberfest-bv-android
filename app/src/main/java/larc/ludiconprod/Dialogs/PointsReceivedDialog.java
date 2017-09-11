package larc.ludiconprod.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import larc.ludiconprod.R;

/**
 * Created by ancuta on 9/8/2017.
 */

public class PointsReceivedDialog extends DialogFragment {

    public  PointsReceivedDialog(){

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View v=inflater.inflate(R.layout.dialog_save_points, null);

        Button okayButton=(Button)v.findViewById(R.id.okayButton);
        TextView ludicoinsText=(TextView)v.findViewById(R.id.ludicoinsText);
        TextView levelText=(TextView)v.findViewById(R.id.levelText);
        int ludicoins=getArguments().getInt("ludicoins");
        int points=getArguments().getInt("points");
        int level=getArguments().getInt("level");
        levelText.setText(String.valueOf(level));
        ludicoinsText.setText(String.valueOf(ludicoins));
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PointsReceivedDialog.this.getDialog().cancel();
            }
        });
                // Add action buttons
        /*
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LoginDialogFragment.this.getDialog().cancel();
            }
        });
        */
        // Create the AlertDialog object and return it
        builder.setView(v);
        return builder.create();
    }
}