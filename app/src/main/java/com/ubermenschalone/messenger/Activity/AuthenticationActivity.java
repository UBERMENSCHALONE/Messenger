package com.ubermenschalone.messenger.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.ubermenschalone.messenger.Fragment.LoginFragment;
import com.ubermenschalone.messenger.Interface.SwitchFragment;
import com.ubermenschalone.messenger.R;

public class AuthenticationActivity extends AppCompatActivity implements View.OnClickListener, SwitchFragment {

    TextView textViewTitle;
    ImageView imageViewBack;

    Fragment fragmentBack;
    String titleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        setFragment(new LoginFragment(), "Login");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                setFragment(fragmentBack, titleBack);
                break;
        }
    }

    @Override
    public void setFragment(Fragment fragment, String fragmentTitle) {
        textViewTitle.setText(fragmentTitle);
        switchTo(fragment);

        if (fragmentTitle.equals("Login")) {
            imageViewBack.setVisibility(View.GONE);
            fragmentBack = fragment;
            titleBack = fragmentTitle;
        } else {
            imageViewBack.setVisibility(View.VISIBLE);
        }
    }

    private void switchTo(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        setFragment(fragmentBack, titleBack);
    }
}
