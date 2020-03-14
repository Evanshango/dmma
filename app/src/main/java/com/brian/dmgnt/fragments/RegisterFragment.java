package com.brian.dmgnt.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.brian.dmgnt.HomeActivity;
import com.brian.dmgnt.R;
import com.brian.dmgnt.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.brian.dmgnt.helpers.Constants.USERS_REF;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private TextView toLogin;
    private EditText regUsername, regEmail, regPassword, regCPassword;
    private Button btnReg;
    private FirebaseAuth mAuth;
    private CollectionReference usersCollection;

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

        btnReg.setOnClickListener(v -> registerUser());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        usersCollection = database.collection(USERS_REF);
    }

    private void registerUser() {
        String username = regUsername.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String cPass = regCPassword.getText().toString().trim();

        if (!username.isEmpty()){
            if (!email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!password.isEmpty()) {
                    if (password.equals(cPass)) {
                        if (password.length() >= 6) {
                            doRegister(username, email, password);
                        } else {
                            Toast.makeText(getContext(), "Password too short", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Password mismatch", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    regPassword.setError("Password cannot be empty");
                    regPassword.requestFocus();
                }
            } else {
                regEmail.setError("Invalid email address");
                regEmail.requestFocus();
            }
        } else {
            regUsername.setError("Username is required");
            regUsername.requestFocus();
        }
    }

    private void doRegister(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                saveUserInfo(username, email);
            } else {
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(),
                Toast.LENGTH_SHORT).show());
    }

    private void saveUserInfo(String username, String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            String userId = user.getUid();
            User mUser = new User(userId, username, email);
            usersCollection.document(userId).set(mUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    navigateToHome();
                } else {
                    Toast.makeText(getContext(), "Error saving user info", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Please try again later",
                    Toast.LENGTH_SHORT).show());
        } else {
            Log.d(TAG, "saveUserInfo: User not created");
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void navigateToLogin(NavController navController) {
        navController.navigate(R.id.action_registerFragment_to_loginFragment);
    }

    private void initView(View view) {
        toLogin = view.findViewById(R.id.txtLoginHere);
        regUsername = view.findViewById(R.id.regUsername);
        regEmail = view.findViewById(R.id.regEmail);
        regPassword = view.findViewById(R.id.regPassword);
        regCPassword = view.findViewById(R.id.confirmPassword);
        btnReg = view.findViewById(R.id.btnReg);
    }
}
