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

public class LoginFragment extends Fragment {

    private TextView toRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        toRegister.setOnClickListener(v -> navigateToRegister(navController));
    }

    private void navigateToRegister(NavController navController) {
        navController.navigate(R.id.action_loginFragment_to_registerFragment);
    }

    private void initViews(View view){
        toRegister = view.findViewById(R.id.txtRegHere);
    }
}
