package larc.ludicon.ChatUtils;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import larc.ludicon.R;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use DatabaseReferenceListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String mUsername;
    private boolean isGroupChat;

    public ChatListAdapter(Query ref, Activity activity, int layout, String mUsername, boolean isGroupChat) {
        super( activity,Chat.class, layout, ref);
        this.mUsername = mUsername;
        this.isGroupChat = isGroupChat;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>DatabaseReferenceListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Chat chat, int position) {
        // Map a Chat object to an entry in our listview
        String author = chat.getAuthor();
        LinearLayout linLayoutRight;
        TextView msgDateRight;
        TextView msgTextRight;
        LinearLayout linLayoutLeft;
        TextView msgDateLeft;
        TextView msgTextLeft;

        if (author != null && author.equals(mUsername)) {
            linLayoutRight = (LinearLayout) view.findViewById(R.id.content_with_background_right);
            linLayoutRight.setBackgroundResource(R.drawable.in_message_bg);
            linLayoutRight.setAlpha(1);
            msgDateRight = (TextView) view.findViewById(R.id.message_date_right);
            msgTextRight = (TextView) view.findViewById(R.id.message_text_right);

            msgDateRight.setText(chat.date);
            msgTextRight.setText(chat.getMessage());
            linLayoutLeft = (LinearLayout) view.findViewById(R.id.content_with_background_left);
            linLayoutLeft.setAlpha(0);
            msgDateLeft = (TextView) view.findViewById(R.id.message_date_left);
            msgTextLeft = (TextView) view.findViewById(R.id.message_text_left);
            msgDateLeft.setText("");
            msgTextLeft.setText("");
        } else {
            linLayoutLeft = (LinearLayout) view.findViewById(R.id.content_with_background_left);
            linLayoutLeft.setAlpha(1);
            linLayoutLeft.setBackgroundResource(R.drawable.out_message_bg);
            msgDateLeft = (TextView) view.findViewById(R.id.message_date_left);
            msgTextLeft = (TextView) view.findViewById(R.id.message_text_left);
            msgDateLeft.setText(chat.date);
            if( this.isGroupChat )
                msgTextLeft.setText(author + ": " + chat.getMessage());
            else msgTextLeft.setText(chat.getMessage());
            linLayoutRight = (LinearLayout) view.findViewById(R.id.content_with_background_right);
            linLayoutRight.setAlpha(0);
            msgDateRight = (TextView) view.findViewById(R.id.message_date_right);
            msgTextRight = (TextView) view.findViewById(R.id.message_text_right);
            msgDateRight.setText("");
            msgTextRight.setText("");
        }
    }
}
