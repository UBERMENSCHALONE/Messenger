package com.ubermenschalone.messenger.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ubermenschalone.messenger.Adapter.MessageAdapter;
import com.ubermenschalone.messenger.Model.Message;
import com.ubermenschalone.messenger.Model.User;
import com.ubermenschalone.messenger.Notification.Client;
import com.ubermenschalone.messenger.Notification.Data;
import com.ubermenschalone.messenger.Notification.MyResponse;
import com.ubermenschalone.messenger.Service.NotificationService;
import com.ubermenschalone.messenger.Notification.Sender;
import com.ubermenschalone.messenger.Notification.Token;
import com.ubermenschalone.messenger.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageViewBack;
    ImageView imageViewStatus;
    TextView textViewName;
    RecyclerView recyclerView;
    EditText editTextSendText;
    ImageView imageViewSend;

    Intent intent;
    String userID;

    NotificationService notificationService;

    FirebaseUser firebaseUser;

    boolean notify = false;

    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceStatus;

    List<Message> listMessage;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewStatus = findViewById(R.id.imageViewStatus);
        textViewName = findViewById(R.id.textViewName);
        recyclerView = findViewById(R.id.recyclerView);
        editTextSendText = findViewById(R.id.editTextSendText);
        imageViewSend = findViewById(R.id.imageViewSend);

        imageViewBack.setOnClickListener(this);
        imageViewSend.setOnClickListener(this);

        intent = getIntent();
        userID = intent.getStringExtra("userID");



       notificationService = Client.getClient("https://fcm.googleapis.com/").create(NotificationService.class);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setStatus("online");

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                textViewName.setText(user.getUserName() + " " + user.getUserLastname());

                if(user.getUserStatus().equals("online")){
                    imageViewStatus.setVisibility(View.VISIBLE);
                }else{
                    imageViewStatus.setVisibility(View.GONE);
                }


                readMesagges(firebaseUser.getUid(), userID, user.getUserProfileImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                finish();
                break;
            case R.id.imageViewSend:
                notify = true;
                String msg = editTextSendText.getText().toString();
                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), userID, msg);
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                editTextSendText.setText("");
                break;
        }
    }

    private void sendMessage(String sender, final String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);


        android.text.format.DateFormat df = new android.text.format.DateFormat();

        hashMap.put("time", df.format("hh:mm", new java.util.Date()));
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);


        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userID);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userID)
                .child(firebaseUser.getUid());
        chatRefReceiver.child("id").setValue(firebaseUser.getUid());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUserName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl){
        listMessage = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listMessage.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if (message.getReceiver().equals(myid) && message.getSender().equals(userid) ||
                            message.getReceiver().equals(userid) && message.getSender().equals(myid)){
                        listMessage.add(message);
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this, listMessage, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message", userID);

                    Sender sender = new Sender(data, token.getToken());

                    notificationService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setStatus(String status){
        databaseReferenceStatus = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userStatus", status);
        databaseReferenceStatus.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        setStatus("offline");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        setStatus("offline");
//    }
}
