package com.ubermenschalone.messenger.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ubermenschalone.messenger.Interface.SwitchFragment;
import com.ubermenschalone.messenger.R;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private SwitchFragment switchFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forgot_password_fragment, container,false);
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

    public static ForgotPasswordFragment newInstance(){
        return new ForgotPasswordFragment();
    }
}