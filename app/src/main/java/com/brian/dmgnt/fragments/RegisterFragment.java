package com.brian.dmgnt.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brian.dmgnt.R;

public class RegisterFragment extends Fragment {

    private TextView toLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        toLogin.setOnClickListener(v -> navigateToLogin(navController));
    }

    private void navigateToLogin(NavController navController) {
        navController.navigate(R.id.action_registerFragment_to_loginFragment);
    }

    private void initView(View view) {
        toLogin = view.findViewById(R.id.txtLoginHere);
    }
}
