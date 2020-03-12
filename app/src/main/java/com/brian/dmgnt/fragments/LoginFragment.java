package com.brian.dmgnt.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.dmgnt.HomeActivity;
import com.brian.dmgnt.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private TextView toRegister;
    private Button btnLogin;
    private EditText loginEmail, loginPassword;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        toRegister.setOnClickListener(v -> navigateToRegister(navController));

        btnLogin.setOnClickListener(v -> loginUser());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    toHomeActivity();
                } else {
                    Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Log.d(TAG, "loginUser: Error " + e));
        } else {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void toHomeActivity() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    private void navigateToRegister(NavController navController) {
        navController.navigate(R.id.action_loginFragment_to_registerFragment);
    }

    private void initViews(View view) {
        toRegister = view.findViewById(R.id.txtRegHere);
        btnLogin = view.findViewById(R.id.btnLogin);
        loginEmail = view.findViewById(R.id.loginEmail);
        loginPassword = view.findViewById(R.id.loginPassword);
    }
}
