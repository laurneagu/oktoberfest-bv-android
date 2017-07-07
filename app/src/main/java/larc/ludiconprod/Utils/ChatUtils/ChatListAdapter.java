package larc.ludiconprod.Utils.ChatUtils;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import larc.ludiconprod.Activities.EventDetails;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.util.DateManager;

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
    protected void populateView(final View view, Chat chat, int position) {
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

            // Format date to the template Today/Yesterday
            Date messageDate = DateManager.convertFromSecondsToDate(chat.date);
            String formattedDate = formatMessageDate(messageDate);

            msgDateRight.setText(formattedDate);
            msgDateRight.setTypeface(null, Typeface.ITALIC);
            if(chat.getMessage().contains("[%##")) {
                final String eventID=chat.getMessage().substring(4,chat.getMessage().length()-4);

                msgTextRight.setMovementMethod(LinkMovementMethod.getInstance());
                msgTextRight.setText("You are invited to this event!!", TextView.BufferType.SPANNABLE);
                Spannable mySpannable = (Spannable) msgTextRight.getText();
                ClickableSpan myClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(mActivity, EventDetails.class);
                        intent.putExtra("eventUid", eventID);
                        mActivity.startActivity(intent);
                    }
                };
                mySpannable.setSpan(myClickableSpan, 19, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else{

                msgTextRight.setText(chat.getMessage());
            }
            linLayoutLeft = (LinearLayout) view.findViewById(R.id.content_with_background_left);
            linLayoutLeft.setAlpha(0);
            msgDateLeft = (TextView) view.findViewById(R.id.message_date_left);
            msgTextLeft = (TextView) view.findViewById(R.id.message_text_left);
            msgDateLeft.setText("");
            msgDateLeft.setTypeface(null, Typeface.ITALIC);
            msgTextLeft.setText("");
        } else {
            linLayoutLeft = (LinearLayout) view.findViewById(R.id.content_with_background_left);
            linLayoutLeft.setAlpha(1);
            linLayoutLeft.setBackgroundResource(R.drawable.out_message_bg);
            msgDateLeft = (TextView) view.findViewById(R.id.message_date_left);
            msgDateLeft.setTypeface(null, Typeface.ITALIC);
            msgTextLeft = (TextView) view.findViewById(R.id.message_text_left);


            // Format date to the template Today/Yesterday
            Date messageDate = DateManager.convertFromSecondsToDate(chat.date);
            String formattedDate = formatMessageDate(messageDate);

            msgDateLeft.setText(formattedDate);
            if( this.isGroupChat)
                msgTextLeft.setText(author + ": " + chat.getMessage());
             if(chat.getMessage().contains("[%##")) {
               final String eventID=chat.getMessage().substring(4,chat.getMessage().length()-4);

                msgTextLeft.setMovementMethod(LinkMovementMethod.getInstance());
                msgTextLeft.setText("You are invited to this event!!", TextView.BufferType.SPANNABLE);
                Spannable mySpannable = (Spannable) msgTextLeft.getText();
                ClickableSpan myClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(mActivity, EventDetails.class);
                        intent.putExtra("eventUid", eventID);
                        mActivity.startActivity(intent);
                    }
                };
                mySpannable.setSpan(myClickableSpan, 19, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
                else{

                    msgTextLeft.setText(chat.getMessage());
                }
            }
            linLayoutRight = (LinearLayout) view.findViewById(R.id.content_with_background_right);
            linLayoutRight.setAlpha(0);
            msgDateRight = (TextView) view.findViewById(R.id.message_date_right);
            msgTextRight = (TextView) view.findViewById(R.id.message_text_right);
            msgDateRight.setText("");
            msgDateRight.setTypeface(null, Typeface.ITALIC);
            msgTextRight.setText("");
        }


    public static DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

    private String formatMessageDate(Date messageDate) {

        Date currDate = new Date();
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTime(currDate);

        Calendar calMessage = Calendar.getInstance();
        calMessage.setTime(messageDate);

        String result ="";
        if (calCurrent.get(Calendar.DAY_OF_MONTH) == calMessage.get(Calendar.DAY_OF_MONTH) &&
                calCurrent.get(Calendar.MONTH) == calMessage.get(Calendar.MONTH) &&
                calCurrent.get(Calendar.YEAR) == calMessage.get(Calendar.YEAR)){

                String minutes="";
                if(messageDate.getMinutes() < 10){
                    minutes =  "0" + messageDate.getMinutes();
                }
                else{
                    minutes = messageDate.getMinutes() + "";
                }
                result = "Today, " + messageDate.getHours() + ":" + minutes;
        }
        else if((calCurrent.get(Calendar.DAY_OF_MONTH) == calMessage.get(Calendar.DAY_OF_MONTH) + 1) &&
                calCurrent.get(Calendar.MONTH) == calMessage.get(Calendar.MONTH) &&
                calCurrent.get(Calendar.YEAR) == calMessage.get(Calendar.YEAR)){
                String minutes="";
                if(messageDate.getMinutes() < 10){
                    minutes =  "0" + messageDate.getMinutes();
                }
                else{
                    minutes = messageDate.getMinutes() + "";
                }
                result = "Yesterday, " + messageDate.getHours() + ":" + minutes;
        }
        else{
            result = dateFormat.format(messageDate);
        }

        return result;
    }
}
