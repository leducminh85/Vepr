package com.example.vepr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText mEmail;
    private EditText mPass;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("swR", true);
            intent.putExtra("swL",true);
            intent.putExtra("key", R.raw.standard11);
            intent.putExtra("ST",true);
            intent.putExtra("DK", false);
            intent.putExtra("AU", false);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnLogin = findViewById(R.id.idButtonLogin);
        TextView register = findViewById(R.id.idRegisterText);
        TextView ForgotPass = findViewById(R.id.forgotPass);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.idEmailLogin);


        mPass = (EditText) findViewById(R.id.idPassLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String pass = mPass.getText().toString();
                if (email.isEmpty() || pass.isEmpty())
                    Toast.makeText(MainActivity.this, "Wrong email or password!.",
                            Toast.LENGTH_SHORT).show();
                else
                  check(email,pass );
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                intent.putExtra("swR", true);
                intent.putExtra("swL",true);
                intent.putExtra("key", R.raw.standard11);
                intent.putExtra("ST",true);
                intent.putExtra("DK", false);
                intent.putExtra("AU", false);
                startActivity(intent);
            }
        });

        ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_getpass);

                Button btnGet = (Button)findViewById(R.id.idButtonget);
                btnGet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText email = (EditText)findViewById(R.id.idEmailGet);


                        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Sent confirm email!.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(MainActivity.this, "Your email incorrect!.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });



            }
        });

    Button GgLog = (Button) findViewById(R.id.idGoogleLogin);
    GgLog.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "App hasn't supported this function!",Toast.LENGTH_SHORT).show();
        }
    });

    Button fbLog = (Button) findViewById(R.id.idFacebookLogin);
    fbLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "App hasn't supported this function!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    public void check(String mail, String pass) {
        mAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            intent.putExtra("key", R.raw.standard11);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Wrong email or password!.",
                                    Toast.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }
                    }
                });

    }

}