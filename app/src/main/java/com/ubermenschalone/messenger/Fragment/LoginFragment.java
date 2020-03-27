package com.ubermenschalone.messenger.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ubermenschalone.messenger.Activity.TabbedActivity;
import com.ubermenschalone.messenger.Interface.SwitchFragment;
import com.ubermenschalone.messenger.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private SwitchFragment switchFragment;

    EditText editTextEmail;
    EditText editTextPassword;
    TextView textViewForgotPassword;
    Button buttonLogIn;
    Button buttonSignUp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container,false);

        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        editTextPassword = rootView.findViewById(R.id.editTextPassword);
        textViewForgotPassword = rootView.findViewById(R.id.textViewForgotPassword);
        buttonLogIn = rootView.findViewById(R.id.buttonLogIn);
        buttonSignUp = rootView.findViewById(R.id.buttonSignUp);

        textViewForgotPassword.setOnClickListener(this);
        buttonLogIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewForgotPassword:
                switchFragment.setFragment(new ForgotPasswordFragment(), "Forgot Password");
                break;

            case R.id.buttonLogIn:
                Intent intent = new Intent(getActivity(), TabbedActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case R.id.buttonSignUp:
                switchFragment.setFragment(new RegistrationFragment(), "Registration");
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SwitchFragment) {
            switchFragment = (SwitchFragment) context;
        }
    }
    public static LoginFragment newInstance(){
        return new LoginFragment();
    }
}
