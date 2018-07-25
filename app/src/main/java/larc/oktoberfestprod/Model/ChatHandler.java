/*
package larc.ludiconprod.Model;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.ChatUtils.Chat;
import larc.ludiconprod.Utils.util.DateManager;

*/
/**
 * Created by ancuta on 7/7/2017.
 *//*


public class ChatHandler {
    // Singleton
    private static ChatHandler instance = null;
    protected ChatHandler() {
    }
    public static ChatHandler getInstance() {
        if(instance == null) {
            instance = new ChatHandler();
        }
        return instance;
    }

    // Get list of chats for a particular user
    public Task<Dictionary<String, String>> getListOfChats(String userId){
        final TaskCompletionSource<Dictionary<String,String>> tcs = new TaskCompletionSource<>();

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("chats");
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dictionary<String, String> uidsChat=new Hashtable<>();

                for(DataSnapshot data: dataSnapshot.getChildren())
                    uidsChat.put(data.getKey(), data.getValue().toString());
                tcs.setResult(uidsChat);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(new IOException("ChatHandler", databaseError.toException()));
            }
        });

        return tcs.getTask();

    }

    // Parameters: list of chatIds to compare with current user id
    public boolean isFirstConversation(Enumeration<String> chatsUid , String userId){
        boolean isFirstConv = true;

        for(String chatUid : Collections.list(chatsUid)){
           if(chatUid.equalsIgnoreCase(userId)){
               isFirstConv = false;
               break;
           }
        }
        return isFirstConv;
    }
    public String generateChat(String otherUserUid,String userId){
        DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference().child("chat");
        DatabaseReference keyRef = fireRef.push();
        final String newChatID = keyRef.getKey();

        Map<String,String> map = new HashMap<>();
        map.put("Users","");
        map.put("Messages","");
        keyRef.setValue(map);
        DatabaseReference addUserUIDs = keyRef.child("Users");

        Map<String,String> userUIDMap = new HashMap<>();
        userUIDMap.put(userId, "");
        userUIDMap.put(otherUserUid, "");
        addUserUIDs.setValue(userUIDMap);

        // Create our 'model', a Chat object
        DatabaseReference newChat = keyRef.child("Messages").push();
        Chat chat = new Chat("Welcome to our chat! :)", "Ludicon", DateManager.getTimeNowInSeconds(),"ludicon-admin");
        newChat.setValue(chat);

        FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("chats").child(otherUserUid).setValue(newChatID);
        FirebaseDatabase.getInstance().getReference().child("users").child(otherUserUid).child("chats").child(userId).setValue(newChatID);

        return newChatID;
    }
    public void sendMessage(String chatId, String firstUser, String userName,String message){
        DatabaseReference mDatabaseReferenceRef=FirebaseDatabase.getInstance().getReference().child("chat").child(chatId).child("Messages");
        Chat chat = new Chat(message, userName, DateManager.getTimeNowInSeconds(), firstUser);

        // Create a new, auto-generated child of that chat location, and save our chat data there
        mDatabaseReferenceRef.push().setValue(chat);
    }
}
*/
