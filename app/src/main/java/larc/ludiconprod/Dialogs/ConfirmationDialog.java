package larc.ludiconprod.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import larc.ludiconprod.R;

/**
 * Created by Cristi Nica on 13.09.2017.
 */

public class ConfirmationDialog extends Dialog {
    public TextView title;
    public TextView message;
    public Button confirm;
    public Button dismiss;

    public ConfirmationDialog(Activity activity) {
        super(activity);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirmation_dialog);
        title = (TextView) findViewById(R.id.title);
        message = (TextView) findViewById(R.id.message);
        confirm = (Button) findViewById(R.id.cofirm);
        dismiss = (Button) findViewById(R.id.dismiss);
    }
}
