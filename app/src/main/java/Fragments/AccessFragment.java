package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ganesh.abhyahas_admin.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import Interface.SubOnClickInterface;
import model.CollegeItemModel;
import model.ItemModel;


public class AccessFragment extends Fragment {

    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter adapter3;
    private FirestoreRecyclerAdapter adapter4;
    String CreaterName,UPIid;
    double amount_pending;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_access, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = view.findViewById(R.id.fab_btn_access);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControllFragment fragment = new ControllFragment();
                getFragmentManager()
                .beginTransaction().replace(R.id.fragment_container,new ControllFragment()).commit();
            }
        });

        mFirestoreList = view.findViewById(R.id.AmountRec);

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Query query = firebaseFirestore.collection("HOME").whereGreaterThan("amount_status",0);

        FirestoreRecyclerOptions<CollegeItemModel> options = new FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query, CollegeItemModel.class)
                .build();
        adapter3 = new FirestoreRecyclerAdapter<CollegeItemModel,CollegeViewHolder>(options){

            @NonNull
            @Override
            public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_item, parent, false);
                return new CollegeViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull CollegeViewHolder holder, int position, @NonNull CollegeItemModel model) {
                Query query1 = firebaseFirestore.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereGreaterThan("amount_pending",0);
                FirestoreRecyclerOptions<ItemModel> options1 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query1, ItemModel.class)
                        .build();

                adapter4 = new FirestoreRecyclerAdapter<ItemModel,ItemViewHolder>(options1){

                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.amount_item, parent, false);
                        return new ItemViewHolder(view2);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder3, int position, @NonNull ItemModel model3) {
                        holder.list_Colname.setVisibility(View.VISIBLE);
                        holder.list_Colname.setText(model.getCollege_Name());

                        holder3.txt_CourseName.setText(model3.getCourse_Name());
                        holder3.Price.setText(model3.getCourse_Price());

                        Bundle bundle = new Bundle();

                        DocumentReference dref = firebaseFirestore.collection("USERS").document(model3.getCreater_ID());

                        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                 CreaterName =  documentSnapshot.getString("Full_Name");
                                 UPIid = documentSnapshot.getString("Phone");
                                holder3.txt_CreaterName.setText(CreaterName);
                                bundle.putString("CreName",CreaterName);
                                bundle.putString("Upiid",UPIid);
                                bundle.putString("Email",documentSnapshot.getString("Email"));
                            }
                        });


                        DocumentReference dref2 =firebaseFirestore.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").document(model3.getCourse_Name());
                                dref2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                         amount_pending = documentSnapshot.getDouble("amount_pending");
                                        holder3.Pendingamount.setText("Amount Pending: "+ amount_pending);
                                        bundle.putDouble("Amount_Pending",amount_pending);
                                    }
                                });



                                bundle.putString("CourseName",model3.getCourse_Name());
                                bundle.putString("CollegeName",model.getCollege_Name());


                                holder3.SubInterfaceClick(new SubOnClickInterface() {
                                    @Override
                                    public void OnClick(View view, boolean isLongPressed) {
                                        TransactionFragment fragment = new TransactionFragment();
                                        fragment.setArguments(bundle);
                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                                    }
                                });

                    }
                };
                adapter4.startListening();
                adapter4.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter4);
            }
        };

        adapter3.startListening();
        adapter3.notifyDataSetChanged();
        mFirestoreList.setAdapter(adapter3);


        return view;
    }

    private class CollegeViewHolder extends RecyclerView.ViewHolder {
        private TextView list_Colname;
        View vv2;
        RecyclerView RecViewInner;
        public RecyclerView.LayoutManager manager;

        public CollegeViewHolder(@NonNull View itemView) {
            super(itemView);
            vv2 = itemView.findViewById(R.id.vv2);
            list_Colname = itemView.findViewById(R.id.list_Col_name);
            RecViewInner = itemView.findViewById(R.id.recycler_view_list);
            list_Colname.setVisibility(View.GONE);
            manager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            RecViewInner.setLayoutManager(manager);

        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_CourseName;
        TextView txt_CreaterName, Price, Pendingamount;
        private SubOnClickInterface subOnClickInterface;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_CourseName = itemView.findViewById(R.id.Coursename);
            txt_CreaterName = itemView.findViewById(R.id.createrName);
            Price = itemView.findViewById(R.id.amountPrice);
            Pendingamount = itemView.findViewById(R.id.SettlementAmount);

            itemView.setOnClickListener(this);

        }

        public void SubInterfaceClick(SubOnClickInterface subOnClickInterface) {
            this.subOnClickInterface = subOnClickInterface;
        }

        @Override
        public void onClick(View vi) {
            subOnClickInterface.OnClick(vi, false);
        }
    }

}