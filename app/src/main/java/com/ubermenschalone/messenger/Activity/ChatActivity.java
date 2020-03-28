package com.ubermenschalone.messenger.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubermenschalone.messenger.R;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageViewBack;
    TextView textViewName;
    RecyclerView recyclerView;
    EditText editTextSendText;
    ImageView imageViewSend;

    Intent intent;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageViewBack = findViewById(R.id.imageViewBack);
        textViewName = findViewById(R.id.textViewName);
        recyclerView = findViewById(R.id.recyclerView);
        editTextSendText = findViewById(R.id.editTextSendText);
        imageViewSend = findViewById(R.id.imageViewSend);

        imageViewBack.setOnClickListener(this);
        imageViewSend.setOnClickListener(this);

        intent = getIntent();
        userID = intent.getStringExtra("userID");
        textViewName.setText(userID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                finish();
                break;
            case R.id.imageViewSend:
                break;
        }
    }
}
