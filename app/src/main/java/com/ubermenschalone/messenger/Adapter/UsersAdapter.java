package com.ubermenschalone.messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ubermenschalone.messenger.Activity.ChatActivity;
import com.ubermenschalone.messenger.Model.Message;
import com.ubermenschalone.messenger.Model.User;
import com.ubermenschalone.messenger.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;
    String lastMessage;

    public UsersAdapter(Context mContext, List<User> mUsers, boolean ischat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);

        if (user.getUserProfileImageURL().equals("STANDARD")){
            holder.imageViewProfileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.userProfileImageURL).override(256, 256).into(holder.imageViewProfileImage);
        }


        if (ischat){
            lastMessage(user.getUserID(), holder.textViewLastMessage);
            holder.textViewLastMessage.setVisibility(View.VISIBLE);
        } else {
            holder.textViewLastMessage.setVisibility(View.GONE);
        }

        if(ischat){
            if(user.getUserStatus().equals("online")){
                holder.imageViewStatus.setVisibility(View.VISIBLE);
            }else{
                holder.imageViewStatus.setVisibility(View.GONE);
            }
        }else{
            holder.imageViewStatus.setVisibility(View.GONE);
        }


        holder.textViewName.setText(user.getUserUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userID", user.getUserID());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public TextView textViewLastMessage;
        public ImageView imageViewProfileImage;
        public ImageView imageViewStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewProfileImage = itemView.findViewById(R.id.imageViewProfileImage);
            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg){
        lastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if (firebaseUser != null && message != null) {
                        if (message.getReceiver().equals(firebaseUser.getUid()) && message.getSender().equals(userid) ||
                                message.getReceiver().equals(userid) && message.getSender().equals(firebaseUser.getUid())) {
                            lastMessage = message.getMessage();
                        }
                    }
                }

                switch (lastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(lastMessage);
                        break;
                }

                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
