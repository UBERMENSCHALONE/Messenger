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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ubermenschalone.messenger.Model.User;
import com.ubermenschalone.messenger.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;

    String lastMessage;

    public UsersAdapter(Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
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
            Picasso.get().load(user.userProfileImageURL).resize(100, 100).centerCrop().into(holder.imageViewProfileImage);
        }

        holder.textViewName.setText(user.getUserUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext, MessageActivity.class);
//                intent.putExtra("userid", user.getId());
//                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public ImageView imageViewProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewProfileImage = itemView.findViewById(R.id.imageViewProfileImage);
            textViewName = itemView.findViewById(R.id.textViewName);

        }
    }
}
