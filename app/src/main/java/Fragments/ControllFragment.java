package Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ganesh.abhyahas_admin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;


public class ControllFragment extends Fragment {

    Button btnStop;
    private FirebaseFirestore firebaseFirestore;
    TextView StMsg;
    TextView UpMsg,PayMSg;
    Button UpbtnStop,PayBtn;
    ImageView Backbtn;
    ListenerRegistration registration1,registration2,registration3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_controll, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        btnStop = view.findViewById(R.id.buttonStop);
        StMsg = view.findViewById(R.id.StMsg);
        UpMsg = view.findViewById(R.id.UpMsg);
        UpbtnStop = view.findViewById(R.id.UpbuttonStop);
        PayMSg = view.findViewById(R.id.PayMsg);
        PayBtn = view.findViewById(R.id.PaymentStop);
        Backbtn = view.findViewById(R.id.backbtnControl);

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new AccessFragment()).commit();
            }
        });

        DocumentReference dref = firebaseFirestore.collection("ADMIN").document("message");
        registration1 =   dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                double St =  value.getDouble("d");
                if(St == 0){
                    StMsg.setText("App is currently stopped. Click button to start");
                    btnStop.setText("Start App");
                }else if(St == 1){
                    StMsg.setText("App is running. Click button to stop");
                    btnStop.setText("Stop App");
                }
                String check = btnStop.getText().toString();
                btnStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do You want to "+ check+" the app");
                        builder.setTitle("Confirmation!!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(check.equals("Start App")){
                                    firebaseFirestore.collection("ADMIN").document("message").update("d",1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "You have successfully started the app", Toast.LENGTH_SHORT).show();
                                            btnStop.setText("Stop App");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error!"+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else if(check.equals("Stop App")){
                                    firebaseFirestore.collection("ADMIN").document("message").update("d",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "You have successfully stopped the app", Toast.LENGTH_SHORT).show();
                                            btnStop.setText("Start App");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error!"+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });
            }
        });


        DocumentReference dref1 = firebaseFirestore.collection("ADMIN").document("upload");
     registration2 =  dref1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                double StU =  value.getDouble("d");
                if(StU == 1){
                    UpMsg.setText("Currently we are not accepting the courses. Click button to start accepting");
                    UpbtnStop.setText("Start Accepting");
                }else if(StU == 0){
                    UpMsg.setText("We are accepting the courses. Click button to stop accepting");
                    UpbtnStop.setText("Stop Accepting");
                }
                String check = UpbtnStop.getText().toString();
                UpbtnStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do you want to "+ check+" the courses");
                        builder.setTitle("Confirmation!!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(check.equals("Start Accepting")){
                                    firebaseFirestore.collection("ADMIN").document("upload").update("d",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "You have successfully started accepting the courses", Toast.LENGTH_SHORT).show();
                                            UpbtnStop.setText("Stop Accepting");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error!"+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else if(check.equals("Stop Accepting")){
                                    firebaseFirestore.collection("ADMIN").document("upload").update("d",1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "You have successfully stopped accepting the courses", Toast.LENGTH_SHORT).show();
                                            UpbtnStop.setText("Start Accepting");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error!"+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });
            }
        });

        DocumentReference dref2 = firebaseFirestore.collection("ADMIN").document("payment");
       registration3 =  dref2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Double Pay = value.getDouble("pay");
                if(Pay == 1){
                    PayMSg.setText("Currently we are accepting the payment. Click button to stop accepting.");
                    PayBtn.setText("Stop Payments");
                }else if(Pay == 0){
                    PayMSg.setText("Currently we are not accepting the payments. Click button to start accepting.");
                    PayBtn.setText("Start Payments");
                }

                String check1 = PayBtn.getText().toString();
                PayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Do you want to "+check1);
                        builder.setTitle("Confirmation!!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (check1.equals("Start Payments")){
                                    firebaseFirestore.collection("ADMIN").document("payment").update("pay",1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "You have successfully started accepting payments", Toast.LENGTH_SHORT).show();
                                            PayBtn.setText("Stop Payments");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error occured "+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else if(check1.equals("Stop Payments")){
                                    firebaseFirestore.collection("ADMIN").document("payment").update("pay",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "You have successfully stopped accepting payments", Toast.LENGTH_SHORT).show();
                                            PayBtn.setText("Start Payments");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Error occured "+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

                        
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });

            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        registration1.remove();
        registration2.remove();
        registration3.remove();
    }

    @Override
    public void onPause() {
        super.onPause();
        registration1.remove();
        registration2.remove();
        registration3.remove();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration1.remove();
        registration2.remove();
        registration3.remove();

    }
}