package com.ubermenschalone.messenger.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ubermenschalone.messenger.Interface.SwitchFragment;
import com.ubermenschalone.messenger.R;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private SwitchFragment switchFragment;

    ImageView imageViewProfileImage;
    TextView textViewSetProfileImage;
    EditText editTextName;
    EditText editTextLastname;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonSignUp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container,false);

        imageViewProfileImage = rootView.findViewById(R.id.imageViewProfileImage);
        textViewSetProfileImage = rootView.findViewById(R.id.textViewSetProfileImage);
        editTextName = rootView.findViewById(R.id.editTextName);
        editTextLastname = rootView.findViewById(R.id.editTextLastname);
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        editTextPassword = rootView.findViewById(R.id.editTextPassword);
        buttonSignUp = rootView.findViewById(R.id.buttonSignUp);

        textViewSetProfileImage.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SwitchFragment) {
            switchFragment = (SwitchFragment) context;
        }
    }

    public static RegistrationFragment newInstance(){
        return new RegistrationFragment();
    }
}
