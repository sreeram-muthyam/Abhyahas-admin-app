package com.ganesh.abhyahas_admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login_Activity extends AppCompatActivity {

    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mresetBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore mfirestore;
    ImageView imgPassword;
    boolean isShowPassword = false;
    ProgressDialog pdd;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mLoginBtn = findViewById(R.id.login);
        mEmail = findViewById(R.id.email_id);
        mPassword = findViewById(R.id.password);
        mresetBtn = findViewById(R.id.forgot);
        imgPassword = findViewById(R.id.eyelogin);

        pdd = new ProgressDialog(com.ganesh.abhyahas_admin.Login_Activity.this);

        fAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),Container_Activity.class));
            finish();
        }

        imgPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowPassword) {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_visibility_24);
                    isShowPassword = false;
                } else{
                    mPassword.setTransformationMethod(null);
                    ((ImageView)(v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    isShowPassword = true;
                }
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters.");
                    return;
                }
                //authenticate the user

                pdd.setCancelable(false);
                pdd.setTitle("Abhyahas LogIn");
                pdd.show();
                pdd.setMessage("Logging into your account. Wait for a while");

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                                pdd.dismiss();

                                userID = fAuth.getCurrentUser().getUid();
                               DocumentReference dref = mfirestore.collection("USERS").document(userID);

                               dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.getString("isAdmin") == null) {
                                            Toast.makeText(com.ganesh.abhyahas_admin.Login_Activity.this, "You are not an admin to access", Toast.LENGTH_SHORT).show();


                                        } else if(documentSnapshot.getString("isAdmin").equals("Yes")) {
                                            Toast.makeText(com.ganesh.abhyahas_admin.Login_Activity.this,"Log In Success", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),Container_Activity.class));
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(com.ganesh.abhyahas_admin.Login_Activity.this, "No user found "+e, Toast.LENGTH_SHORT).show();
                                }
                            });




                        }else {
                            pdd.dismiss();
                            Toast.makeText(com.ganesh.abhyahas_admin.Login_Activity.this, "Error ! " + task.getException().getMessage(), Toast. LENGTH_SHORT).show();

                        }

                    }
                });
            }
        });

        mresetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password? ");
                passwordResetDialog.setMessage("Enter your registered email to receive link.");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setCancelable(false);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /// extract the email and send reset link

                        String mail = resetMail.getText().toString();

                        if (!(mail.isEmpty())) {
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(com.ganesh.abhyahas_admin.Login_Activity.this, "Reset link Sent to your mail." , Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(com.ganesh.abhyahas_admin.Login_Activity.this, "Error:Link is Not Sent" + e.getMessage() , Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            Toast.makeText(com.ganesh.abhyahas_admin.Login_Activity.this, "Email Id is mandatory", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                passwordResetDialog.create().show();

            }
        });

    }

}