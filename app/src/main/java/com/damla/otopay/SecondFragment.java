package com.damla.otopay;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.damla.otopay.databinding.FragmentSecondBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        binding.accountCreate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.editTextTextEmailAddress.getText().toString();
                String password = binding.editPassword.getText().toString();
                String passwordRepeat = binding.editPasswordRepeat.getText().toString();
                //parola kontrolü
                if(email.indexOf("@") == -1){
                    alert("Hata","Geçerli bir E-mail girin");
                } else if(password.equals(passwordRepeat)){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String[] arrOfStr = email.split("@", 2);
                                        // Create a new user with a first and last name
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("username", arrOfStr[0]);
                                        user.put("credit", 0);

                                        // Add a new document with a generated ID
                                        db.collection("users").document(email)
                                                .set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //Log.w(TAG, "Error writing document", e);
                                                    }
                                                });
                                        alert("Başarılı","başarılı");
                                        NavHostFragment.findNavController(SecondFragment.this)
                                                .navigate(R.id.action_SecondFragment_to_DashboardFragment);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        alert("Hata",task.getException().toString());
                                    }
                                }
                            });
                }else{
                    alert(passwordRepeat,password);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void alert(String title, String message){
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}