package Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import Interface.SubOnClickInterface;
import model.CollegeItemModel;
import model.ItemModel;

public class Home extends Fragment {

    String creatorname, creatorimage, UserID,email;

    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter;
    private FirestoreRecyclerAdapter adapter2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_btn_home);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new UsersFragment()).commit();

            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = view.findViewById(R.id.OuterRec_View);

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Query query = firebaseFirestore.collection("HOME").whereGreaterThan("pending", 0);

        FirestoreRecyclerOptions<CollegeItemModel> options = new FirestoreRecyclerOptions.Builder<CollegeItemModel>()
                .setQuery(query, CollegeItemModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CollegeItemModel, CollegeViewHolder>(options) {
            @NonNull
            @Override
            public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_item, parent, false);
                return new CollegeViewHolder(view1);

            }

            @Override
            protected void onBindViewHolder(@NonNull CollegeViewHolder holder, int position, @NonNull CollegeItemModel model) {

                Query query1 = firebaseFirestore.collection("HOME").document(model.getCollege_Name()).collection("LISTITEM").whereEqualTo("Status", "pending");
                FirestoreRecyclerOptions<ItemModel> options1 = new FirestoreRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query1, ItemModel.class)
                        .build();


                adapter2 = new FirestoreRecyclerAdapter<ItemModel, ItemViewHolder>(options1) {
                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                        return new ItemViewHolder(v);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder1, int position, @NonNull ItemModel model1) {

                        holder.list_Colname.setVisibility(View.VISIBLE);
                        holder.list_Colname.setText(model.getCollege_Name());

                        holder1.txt_Videoname.setText(model1.getCourse_Name());
                        Picasso.get().load(model1.getCourse_image()).into(holder1.image_item);
                        if (model1.getCourse_Price().equals("FREE")) {
                            holder1.PriceId.setText("Free");
                        } else {
                            holder1.PriceId.setText("Rs. " + model1.getCourse_Price() + "/-");
                        }

                        Bundle bundle8 = new Bundle();



                        DocumentReference dref = firebaseFirestore.collection("USERS").document(model1.getCreater_ID());

                        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                creatorname = documentSnapshot.getString("Full_Name");
                                email = documentSnapshot.getString("Email");

                                holder1.txt_item__title.setText("by "+creatorname);

                                creatorimage = documentSnapshot.getString("Image");

                                bundle8.putString("CreName",creatorname);
                                bundle8.putString("creator",creatorimage);
                                bundle8.putString("Email",email);
                                bundle8.putString("ViName",model1.getCourse_Name());
                                bundle8.putString("Pri",model1.getCourse_Price());
                                bundle8.putString("PDF",model1.getCourse_PDF());
                                bundle8.putString("Vurl",model1.getIntro_Url());
                                bundle8.putString("IName",model1.getIntro_Name());
                                bundle8.putString("Desc",model1.getCourse_Description());
                                bundle8.putString("CoID",model.getCollege_Name());
                                bundle8.putString("enrol",model1.getEnrolments());
                                bundle8.putString("CreID",model1.getCreater_ID());

                            }
                        });


                        holder1.SubInterfaceClick(new SubOnClickInterface() {
                            @Override
                            public void OnClick(View view, boolean isLongPressed) {

                                Accessingvideos frag8 = new Accessingvideos();
                                frag8.setArguments(bundle8);
                                getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container,frag8)
                                        .commit();

                            }
                        });

                    }
                };
                adapter2.startListening();
                adapter2.notifyDataSetChanged();
                holder.RecViewInner.setAdapter(adapter2);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        mFirestoreList.setAdapter(adapter);

        setHasOptionsMenu(true);

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
        TextView txt_item__title;
        TextView txt_Videoname, PriceId;
        ImageView image_item;
        private SubOnClickInterface subOnClickInterface;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_item__title = itemView.findViewById(R.id.tv_sub_Title);
            txt_Videoname = itemView.findViewById(R.id.tvTitle);
            image_item = itemView.findViewById(R.id.itemImage);
            PriceId = itemView.findViewById(R.id.item_price);

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