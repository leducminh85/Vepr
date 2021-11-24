package com.example.vepr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEmail;
    private EditText mPass;
    private EditText mName;
    private EditText mPhone;
    private String UserID;

    private FirebaseFirestore mData;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnRegister=findViewById(R.id.idButtonRegister);


        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = (EditText)findViewById(R.id.idEmailRegister);
                String Email = mEmail.getText().toString().trim();

                mPass = (EditText)findViewById(R.id.idPasswordRegister);
                String Pass = mPass.getText().toString().trim();

                mName = (EditText)findViewById(R.id.idNameRegister);
                String Name = mName.getText().toString();

                mPhone = (EditText) findViewById(R.id.idPhoneRegister);
                String Phone = mPhone.getText().toString();

                if (TextUtils.isEmpty(Email)) {
                    RegisterActivity.this.mEmail.setError("Email is required!");
                    return;
                }

                if (TextUtils.isEmpty(Pass)) {
                    RegisterActivity.this.mPass.setError("Password is required!");
                    return;
                }

                if (Pass.length() < 6){
                    RegisterActivity.this.mPass.setError("Password must be >=6 characters!");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener((task)->{
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "User Creates.",Toast.LENGTH_SHORT).show();

                        UserID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = mData.collection("Users").document(UserID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("Name", Name);
                        user.put("Phone", Phone);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "Create Profile Successfully!");
                            }
                        });
                        Intent intent =new Intent(RegisterActivity.this,MapsActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Error!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }

    @Override
    public void onClick(View v) {

    }
}