package Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ganesh.abhyahas_admin.EmailActivity;
import com.ganesh.abhyahas_admin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TransactionFragment extends Fragment {

    String CreaterName,CourseName,CollegeName,UpiId;
    String currentDate, currentTime;
    String PaymentMethod,Email;
    Double Amount_Pending;
    TextView Statement,UPIID;
    ImageView backbtn;
    FirebaseFirestore firebaseFirestore;
    EditText TransId;
    Button Settledbtn;
    RadioButton PhnPay,GPay,Paytm,NetBanking,Others;
    RadioGroup PaymentGroup;
    Double Noft,amount_status;
    ProgressDialog pd;

    Map<String, Object> details = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle!= null){
            CreaterName = (String) bundle.get("CreName");
            CourseName = (String) bundle.get("CourseName");
            Amount_Pending = (Double) bundle.get("Amount_Pending");
            CollegeName = (String) bundle.get("CollegeName");
            UpiId = (String) bundle.get("Upiid");
            Email = (String) bundle.get("Email");
        }

        backbtn = view.findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new AccessFragment()).commit();
            }
        });
        Statement = view.findViewById(R.id.Statement);
        Statement.setText("Rs."+Amount_Pending+" have to be setteled for "+CreaterName+" to the Course: "+CourseName+".");

        TransId = view.findViewById(R.id.TransactionId);
        PhnPay = view.findViewById(R.id.PhonePay);
        GPay = view.findViewById(R.id.GooglePay);
        Paytm = view.findViewById(R.id.Paytm);
        NetBanking = view.findViewById(R.id.NetBanking);
        Others = view.findViewById(R.id.OtherMode);
        Settledbtn = view.findViewById(R.id.settledbtn);
        PaymentGroup = view.findViewById(R.id.PaymentMode);
        UPIID = view.findViewById(R.id.UPIID);

        pd = new ProgressDialog(getContext());

        UPIID.setText("Upi Id: "+UpiId);


        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());





        Settledbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Id = TransId.getText().toString().trim();

                if(TextUtils.isEmpty(Id)) {
                    TransId.setError("Transaction Id is Required.");
                    return;
                }
                int isSelected = PaymentGroup.getCheckedRadioButtonId();
                if(isSelected == -1){
                    Toast.makeText(getContext(), "You have not selected Payment Mode", Toast.LENGTH_SHORT).show();
                    return;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirmation!!");
                builder.setMessage("Are you sure that you have settled amount");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        pd.setTitle("Progress");
                        pd.setMessage("please wait until all changes occurs");
                        pd.setCancelable(false);
                        pd.show();

                        if (PhnPay.isChecked()){
                            PaymentMethod = "PhonePe";
                        }else if(GPay.isChecked()){
                            PaymentMethod = "Google Pay";
                        }else if(Paytm.isChecked()){
                            PaymentMethod = "Paytm";
                        }else if(NetBanking.isChecked()){
                            PaymentMethod = "Net Banking";
                        }else if(Others.isChecked()){
                            PaymentMethod = "Others";
                        }

                        SettleAmount();

                /*        details.put("transaction_id",TransId.getText().toString());
                        details.put("transaction_method",PaymentMethod+"("+UpiId+")");
                        details.put("transaction_date",currentDate);
                        details.put("transaction_time",currentTime);

                        HashMap<String,Double> order = new HashMap<>();
                        HashMap<String,Double> extra = new HashMap<>();

                        DocumentReference dref = firebaseFirestore.collection("HOME").document(CollegeName).collection("LISTITEM").document(CourseName).collection("TRANSACTIONS").document("No_of_Transactions");
                        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Noft = documentSnapshot.getDouble("No_of_Transactions");
                                if(Noft == null){
                                    Noft = 0.00;
                                }
                                order.put("transaction_order",Noft+1.00);
                                extra.put("No_of_Transactions",Noft+1.00);
                            }
                        });


                        order.put("amount_settled",Amount_Pending);


                        DocumentReference dref1 = firebaseFirestore.collection("HOME").document(CollegeName).collection("LISTITEM").document(CourseName).collection("TRANSACTIONS").document(currentDate+" "+currentTime);
                        dref1.set(details, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               dref1.set(order,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                   //    dref.update("No_of_Transactions",Noft+1.00);
                                       dref.set(extra,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               firebaseFirestore.collection("HOME").document(CollegeName).collection("LISTITEM").document(CourseName).update("amount_pending",0)
                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                           @Override
                                                           public void onSuccess(Void aVoid) {
                                                               DocumentReference dref2 = firebaseFirestore.collection("HOME").document(CollegeName);
                                                               dref2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                   @Override
                                                                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                       amount_status = documentSnapshot.getDouble("amount_status");
                                                                       dref2.update("amount_status",amount_status-1.00).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void aVoid) {
                                                                               pd.dismiss();
                                                                               Intent intent = new Intent(getContext(), EmailActivity.class);
                                                                               intent.putExtra("Email",Email);
                                                                               intent.putExtra("from","Transaction");
                                                                               intent.putExtra("amount",Amount_Pending);
                                                                               startActivity(intent);
                                                                           }
                                                                       }).addOnFailureListener(new OnFailureListener() {
                                                                           @Override
                                                                           public void onFailure(@NonNull Exception e) {
                                                                               Toast.makeText(getContext(), "Unable to update amount status", Toast.LENGTH_SHORT).show();
                                                                           }
                                                                       });
                                                                   }
                                                               });

                                                           }
                                                       }).addOnFailureListener(new OnFailureListener() {
                                                   @Override
                                                   public void onFailure(@NonNull Exception e) {
                                                       Toast.makeText(getContext(), "Error in making pending to 0 "+e, Toast.LENGTH_SHORT).show();
                                                   }
                                               });
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               Toast.makeText(getContext(), "Error in Updating Count "+e, Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(getContext(), "Error in putting order "+e, Toast.LENGTH_SHORT).show();
                                   }
                               });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error in putting details "+e, Toast.LENGTH_SHORT).show();
                            }
                        });  */

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


        return view;
    }

    private void SettleAmount() {

        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentReference TransRef = firebaseFirestore.collection("HOME").document(CollegeName).collection("LISTITEM").document(CourseName).collection("TRANSACTIONS").document("No_of_Transactions");
                DocumentSnapshot Transnap = transaction.get(TransRef);
                DocumentReference TimeRef = firebaseFirestore.collection("HOME").document(CollegeName).collection("LISTITEM").document(CourseName).collection("TRANSACTIONS").document(currentDate+" "+currentTime);
                DocumentSnapshot TimeSanp = transaction.get(TimeRef);
                DocumentReference CourseRef = firebaseFirestore.collection("HOME").document(CollegeName).collection("LISTITEM").document(CourseName);
                DocumentSnapshot CourseSnap = transaction.get(CourseRef);
                DocumentReference CollRef = firebaseFirestore.collection("HOME").document(CollegeName);
                DocumentSnapshot CollSnap = transaction.get(CollRef);

                Noft = Transnap.getDouble("No_of_Transactions");
                amount_status = CollSnap.getDouble("amount_status");

                details.put("transaction_id",TransId.getText().toString());
                details.put("transaction_method",PaymentMethod+"("+UpiId+")");
                details.put("transaction_date",currentDate);
                details.put("transaction_time",currentTime);

                HashMap<String,Double> order = new HashMap<>();
                HashMap<String,Double> extra = new HashMap<>();

                if(Noft == null){
                    Noft = 0.00;
                }
                order.put("transaction_order",Noft+1.00);
                extra.put("No_of_Transactions",Noft+1.00);
                order.put("amount_settled",Amount_Pending);

                transaction.set(TimeRef,details,SetOptions.merge());
                transaction.set(TimeRef,order,SetOptions.merge());
                transaction.set(TransRef,extra,SetOptions.merge());
                transaction.update(CourseRef,"amount_pending",0);
                transaction.update(CollRef,"amount_status",amount_status-1.00);


                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Intent intent = new Intent(getContext(), EmailActivity.class);
                intent.putExtra("Email",Email);
                intent.putExtra("from","Transaction");
                intent.putExtra("amount",Amount_Pending);
                intent.putExtra("CreName",CreaterName);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error in Settlement "+e, Toast.LENGTH_SHORT).show();
            }
        });

    }
}