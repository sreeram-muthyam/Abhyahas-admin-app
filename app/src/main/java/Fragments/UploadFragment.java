package Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ganesh.abhyahas_admin.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import Interface.SubOnClickInterface;
import model.CollegeItemModel;
import model.ItemModel;

public class UploadFragment extends Fragment {

    FloatingActionButton fab;
    String creatorname, userID, creatorImg;
    private RecyclerView UploadRec;
    private FirebaseFirestore firebaseFirestore2;
    private StorageReference mstorage;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter6;
    private FirestoreRecyclerAdapter adapter7;
    Double num3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_upload, container, false);

        UploadRec = view.findViewById(R.id.Upload_Rec);
        firebaseFirestore2 = FirebaseFirestore.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        UploadRec.setHasFixedSize(true);
        UploadRec.setLayoutManager(new LinearLayoutManager(view.getContext()));

        userID = mAuth.getCurrentUser().getUid();


        Query query = firebaseFirestore2.collection("HOME").whereGreaterThan("deleted",0);

        FirestoreRecyclerOptions<CollegeItemModel> options7 = new  FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query,CollegeItemModel.class)
                .build();

        adapter7 = new FirestoreRecyclerAdapter<CollegeItemModel,UpColViewHolder>(options7){
            @NonNull
            @Override
            public UpColViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_item,parent,false);
                return new UpColViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull UpColViewHolder holder, int position, @NonNull CollegeItemModel model) {

                Query query6 = firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereEqualTo("Status","deleted").whereEqualTo("Enrolments","0");

                FirestoreRecyclerOptions<ItemModel> options6 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query6,ItemModel.class)
                        .build();

                adapter6 = new FirestoreRecyclerAdapter<ItemModel,UploadViewHolder>(options6){
                    @NonNull
                    @Override
                    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
                        return new UploadViewHolder(v);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull UploadViewHolder holder6, int position, @NonNull ItemModel model6) {

                        holder.list_Colname.setVisibility(View.VISIBLE);
                        holder.list_Colname.setText(model.getCollege_Name());




                        DocumentReference dref = firebaseFirestore2.collection("USERS").document(model6.getCreater_ID());

                        dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {




                                creatorname = value.getString("Full_Name");

                                holder6.txt_item__title.setText("by "+creatorname);
                                creatorImg = value.getString("Image");




                            }
                        });

                        DocumentReference dref1 =firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name());
                        dref1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String project = documentSnapshot.getString("project_name");
                                holder6.ProjectName.setText(project);
                                holder6.ProjectName.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Unable to fetch project name "+ e, Toast.LENGTH_SHORT).show();
                            }
                        });


                        holder6.txt_Videoname.setText(model6.getCourse_Name());
                        Picasso.get().load(model6.getCourse_image()).into(holder6.image_item);
                        holder6.PriceId.setText("Rs."+ model6.getCourse_Price());

                        holder6.SubInterfaceClick(new SubOnClickInterface() {
                            @Override
                            public void OnClick(View view, boolean isLongPressed) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Confirmation!!");
                                builder.setMessage("Are you want to delete this course. Once deleted can not be restored");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name()).collection("ALL VIDEOS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot d : list){
                                                    String id = d.getId();
                                                    firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name()).collection("ALL VIDEOS").document(d.getId())
                                                            .delete();

                                                }
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name()).collection("TRANSACTIONS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();
                                                        for (DocumentSnapshot d1 : list1){
                                                            String id = d1.getId();
                                                            firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name()).collection("TRANSACTIONS").document(d1.getId())
                                                                    .delete();

                                                        }
                                                    }
                                                });
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model6.getCourse_Name()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(view.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                        firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                num3 = documentSnapshot.getDouble("deleted");
                                                                firebaseFirestore2.collection("HOME").document(model.getCollege_Name()).update("deleted",num3-1.00);
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(view.getContext(), "Faied in deleting doc"+e, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
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
                };

                adapter6.startListening();
                adapter6.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter6);

            }
        };

        adapter7.startListening();
        adapter7.notifyDataSetChanged();
        UploadRec.setAdapter(adapter7);

        setHasOptionsMenu(true);

        return view;
    }

    private class UploadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_item__title;
        TextView txt_Videoname,PriceId,ProjectName;
        ImageView image_item;
        private SubOnClickInterface subOnClickInterface;

        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_item__title = itemView.findViewById(R.id.tv_sub_Title);
            txt_Videoname = itemView.findViewById(R.id.tvTitle);
            ProjectName = itemView.findViewById(R.id.ProjectName);
            image_item = itemView.findViewById(R.id.itemImage);
            PriceId = itemView.findViewById(R.id.item_price);

            itemView.setOnClickListener(this);

        }
        public void SubInterfaceClick(SubOnClickInterface subOnClickInterface){
            this.subOnClickInterface = subOnClickInterface;
        }

        @Override
        public void onClick(View vi) {
            subOnClickInterface.OnClick(vi,false);
        }
    }

    private class UpColViewHolder extends RecyclerView.ViewHolder {
        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;
        private TextView list_Colname;
        View v;

        public UpColViewHolder(@NonNull View itemView) {
            super(itemView);
            RecViewInner = itemView.findViewById(R.id.recycler_view_list);

            manager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL,false);
            RecViewInner.setLayoutManager(manager);

            list_Colname = itemView.findViewById(R.id.list_Col_name);
            list_Colname.setVisibility(View.INVISIBLE);
        }
    }

}