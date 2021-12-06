package Fragments;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ganesh.abhyahas_admin.EmailActivity;
import com.ganesh.abhyahas_admin.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import Interface.SubOnClickInterface;
import model.ContentModel;

import static java.lang.String.valueOf;

//import com.google.firebase.auth.FirebaseAuth;

public class Accessingvideos extends Fragment {

    ImageView back, resize, btFullscreen,profile;
    Button info,content,pdf, remove, reject;
    View pdfview;
    View infoview,leftside,rightside, heading, contentview;
    String CreName,creator,Videoname,DESC,Price,VideoUrl,Pdf,CoId,UserID,enrol,Intro,email,CreID;
    TextView CN,VN,Descp,PRICE,ListVideoAc;
    PDFView pdfView;
    PlayerView playerView2;
    SimpleExoPlayer exoPlayer2;
    RecyclerView RecAccess;
    FirebaseFirestore dbs2;
    FirebaseAuth mAuth;
    FirebaseStorage mfirebaseStorage;
    StorageReference mstorage;
    SecretKey skey;
    Double num,num1;
    Double num2,num4;

    String CourseUplaod;
  //  FirebaseAuth mAuth;
    private FirestoreRecyclerAdapter adapter10;
    boolean flag = false;
    int size = 0 ;
    private boolean playWhenReady = true;
    ProgressDialog progressDialog;
    RadioGroup speedgroup,ProjectGroup;
    String Project;

    private SharedPreferences sharedPref;

    List<Uri> downloadlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accessingvideos, container, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        dbs2 = FirebaseFirestore.getInstance();
        mfirebaseStorage = FirebaseStorage.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

       UserID = mAuth.getCurrentUser().getUid();
        downloadlist = new ArrayList<>();

        back = view.findViewById(R.id.rev);
        info = view.findViewById(R.id.infobtn1);
        content = view.findViewById(R.id.contentbtn1);
        pdf = view.findViewById(R.id.pdfbtn1);
        pdfview = view.findViewById(R.id.pdf1);
        infoview = view.findViewById(R.id.info1);
        contentview = view.findViewById(R.id.content1);
        playerView2 = view.findViewById(R.id.player_view3);
        RecAccess = view.findViewById(R.id.Access_Rec_View);
        leftside = view.findViewById(R.id.leftside3);
        rightside = view.findViewById(R.id.rightside3);
        heading = view.findViewById(R.id.headingg);
        profile = view.findViewById(R.id.profile_imageav);

        CN = view.findViewById(R.id.Access_CreaterName);
        VN = view.findViewById(R.id.Access_CourseName);
        Descp = view.findViewById(R.id.Access_CourseDesc);
        PRICE = view.findViewById(R.id.Access_CoursePrice);
        ListVideoAc = view.findViewById(R.id.List_VideoName_Access);
        remove = view.findViewById(R.id.remove);
        reject = view.findViewById(R.id.reject);
        resize = playerView2.findViewById(R.id.resize);
        btFullscreen = playerView2.findViewById(R.id.bt_fullscreen);
        speedgroup = playerView2.findViewById(R.id.RadioSpeed);
        ProjectGroup = view.findViewById(R.id.ProjectSelect);



        byte[] encodedKey     = Base64.decode( "fRIxFDSHKrBDUjseir8TSg==\n"+"    ", Base64.DEFAULT);
        skey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        Bundle bundle8 = this.getArguments();
        if(bundle8!=null){

            CreName =(String) bundle8.get("CreName");
            creator =(String) bundle8.get("creator");
            Videoname =(String) bundle8.get("ViName");
            DESC =(String)bundle8.get("Desc");
            Price = (String) bundle8.get("Pri");
            VideoUrl=(String) bundle8.get("Vurl");
            Pdf = (String)bundle8.get("PDF");
            CoId = (String) bundle8.get("CoID");
            enrol = (String) bundle8.get("enrol");
            Intro = (String) bundle8.get("IName");
            email = (String) bundle8.get("Email");
            CreID =(String) bundle8.get("CreID");

            Picasso.get().load(Uri.parse(creator)).into(profile);
            CN.setText(CreName);
            VN.setText(Videoname);
            Descp.setText(DESC);
            PRICE.setText("Course Price : Rs." + Price+"/-");

        }

        if(Pdf.equals("No PDF")){
            pdf.setVisibility(View.INVISIBLE);
        }



        ProjectGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.Project1:
                        Project = "Project 1";
                        break;
                    case R.id.Project2:
                        Project = "Project 2";
                        break;
                    case R.id.Project3:
                        Project = "Project 3";
                        break;
                    case R.id.Project4:
                        Project = "Project 4";
                        break;
                    case R.id.Project5:
                        Project = "Project 5";
                        break;
                    case R.id.Project6:
                        Project = "Project 6";
                        break;
                    case R.id.Project7:
                        Project = "Project 7";
                        break;
                    case R.id.Project8:
                        Project = "Project 8";
                        break;
                    case R.id.Project9:
                        Project = "Project 9";
                        break;
                    case R.id.Project10:
                        Project = "Project 10";
                        break;
                }
            }
        });


        remove.setOnClickListener(new View.OnClickListener() {

            Double num,num1;

            File delfile2 = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")));

            @Override
            public void onClick(View view) {

                int isSelected = ProjectGroup.getCheckedRadioButtonId();
                if(isSelected == -1) {
                    Toast.makeText(getContext(), "You have not selected Project", Toast.LENGTH_SHORT).show();
                    return;
                }



                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirmation!!");
                builder.setMessage("Are you sure want to accept this course");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        remove.setVisibility(View.GONE);
                        AcceptCourse();
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

        reject.setOnClickListener(new View.OnClickListener() {

            File delfile2 = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")));

            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirmation!!");
                builder.setMessage("Are you sure want to reject this course");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reject.setVisibility(View.GONE);
                        RejectCourse();

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


        ChipNavigationBar navBar = getActivity().findViewById(R.id.bottom_nav_menu_id);

        initializePlayer(VideoUrl);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftt = getFragmentManager().beginTransaction();
                ftt.replace(R.id.fragment_container, new Fragments.Home()).commit();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoview.setVisibility(View.VISIBLE);
                pdfview.setVisibility(View.GONE);
                contentview.setVisibility(View.GONE);
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoview.setVisibility(View.GONE);
                pdfview.setVisibility(View.VISIBLE);
                contentview.setVisibility(View.GONE);

                pdfView=(PDFView) view.findViewById(R.id.pdf_Access);

                //    new RetrievePDFStream().execute(Pdf);
                File pdffile = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")),"Course Pdf");

                if (pdffile.exists()){
                    try {
                        decryptPdfFile(String.valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/Course Pdf")),skey,"Pdf");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }else{
                    downladPdf();
                }

            }
        });




        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoview.setVisibility(View.GONE);
                pdfview.setVisibility(View.GONE);
                contentview.setVisibility(View.VISIBLE);
                RecAccess.setHasFixedSize(true);
                RecAccess.setLayoutManager(new LinearLayoutManager(view.getContext()));

                Query query10 = dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname).collection("ALL VIDEOS").orderBy("Order");
                FirestoreRecyclerOptions<ContentModel> options10 = new FirestoreRecyclerOptions.Builder<ContentModel>()
                        .setQuery(query10, ContentModel.class)
                        .build();
                adapter10 = new FirestoreRecyclerAdapter<ContentModel,AcContentViewHolder>(options10) {
                    @NonNull
                    @Override
                    public AcContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View Vew1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item,parent,false);
                        return new AcContentViewHolder(Vew1);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AcContentViewHolder holder10, int position, @NonNull ContentModel model10) {

                        holder10.Text.setText(model10.getVideo_Name());
                        holder10.SubInterfaceClick(new SubOnClickInterface() {
                            @Override
                            public void OnClick(View view, boolean isLongPressed) {
                                VideoUrl= model10.getVideo_Url();
                                releasePlayer();
                                ListVideoAc.setText("Playing : "+model10.getVideo_Name());
                                initializePlayer(VideoUrl);

                            }
                        });

                    }
                };
                adapter10.startListening();
                adapter10.notifyDataSetChanged();
                RecAccess.setAdapter(adapter10);

            }
        });


        btFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(flag){
                    btFullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen));

                    if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                    }
                    ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView2.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * view.getContext().getResources().getDisplayMetrics().density);
                    playerView2.setLayoutParams(params);

                    CN.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                    navBar.setVisibility(View.VISIBLE);
                    heading.setVisibility(View.VISIBLE);
                    leftside.setVisibility(View.VISIBLE);
                    rightside.setVisibility(View.VISIBLE);
                    flag = false;
                }else {
                    btFullscreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit));

                    if ( ((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                    }

                    ((AppCompatActivity)getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView2.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView2.setLayoutParams(params);
                    CN.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    navBar.setVisibility(View.GONE);
                    heading.setVisibility(View.GONE);
                    leftside.setVisibility(View.GONE);
                    rightside.setVisibility(View.GONE);

                    flag = true;
                }
            }
        });

        resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resizefun();
            }
        });

        speedgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){

                    case R.id.Speed0_5x:
                        PlaybackParams param1 = new PlaybackParams();
                        param1.setSpeed(0.5f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param1);
                        break;
                    case R.id.Speed1x:
                        PlaybackParams param2 = new PlaybackParams();
                        param2.setSpeed(1f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param2);
                        break;
                    case R.id.Speed1_5x:
                        PlaybackParams param3 = new PlaybackParams();
                        param3.setSpeed(1.5f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param3);
                        break;
                    case R.id.Speed2x:
                        PlaybackParams param4 = new PlaybackParams();
                        param4.setSpeed(2f);// 1f is 1x, 2f is 2x
                        exoPlayer2.setPlaybackParams(param4);
                        break;

                }
            }
        });


        return view;

    }

    private void RejectCourse(){

        File delfile2 = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")));

        dbs2.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentReference CouRef =  dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname);
                DocumentReference ColRef = dbs2.collection("HOME").document(CoId);
                DocumentSnapshot ColSnap = transaction.get(ColRef);
                DocumentReference UserRef = dbs2.collection("USERS").document(CreID);
                DocumentSnapshot UserSnap = transaction.get(UserRef);

                num2 = ColSnap.getDouble("pending");
                num4 = ColSnap.getDouble(CreID);

                CourseUplaod = UserSnap.getString("courses_uploaded");

                HashMap<String,Double> Reduce = new HashMap<>();
                Reduce.put("pending",num2-1.00);
                Reduce.put(CreID,num4-1.00);


                transaction.delete(CouRef);
                transaction.set(ColRef,Reduce, SetOptions.merge());
                transaction.update(UserRef,"courses_uploaded",String.valueOf(Integer.parseInt(CourseUplaod)-1));

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname).collection("ALL VIDEOS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list){
                            String id = d.getId();
                            dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname).collection("ALL VIDEOS").document(d.getId())
                                    .delete();
                            final StorageReference vidref = mstorage.child("Courses").child(Videoname).child(id);
                            vidref.delete();
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        final StorageReference mstorageReference = mstorage.child("Courses").child(Videoname).child("Course Image");
                        mstorageReference.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Image deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to del image"+e, Toast.LENGTH_SHORT).show();
                                Log.d("Tag1","Failed"+e);
                            }
                        });

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        final StorageReference mstorageReference = mstorage.child("Courses").child(Videoname).child("Course PDF");
                        mstorageReference.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "PDF deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to del pdf"+e, Toast.LENGTH_SHORT).show();
                                Log.d("Tag1","Failed"+e);
                            }
                        });

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final StorageReference intrref = mstorage.child("Courses").child(Videoname).child(Intro);
                        intrref.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Intro Video deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to del Intro Video"+e, Toast.LENGTH_SHORT).show();
                                Log.d("Tag1","Failed"+e);
                            }
                        });
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        deleteDirectory(delfile2);

                        Intent intent = new Intent(getContext(), EmailActivity.class);
                        intent.putExtra("Email",email);
                        intent.putExtra("from","reject");
                        intent.putExtra("amount",0);
                        intent.putExtra("CreName",CreName);
                        startActivity(intent);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Unable to reject course "+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AcceptCourse() {

        File delfile2 = new File(valueOf(getActivity().getExternalFilesDir("Purchased Courses/"+UserID+Videoname+"Purchased/")));

        dbs2.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference ColRef =  dbs2.collection("HOME").document(CoId);
                DocumentSnapshot Colsnap = transaction.get(ColRef);
                DocumentReference CourseRef = dbs2.collection("HOME").document(CoId).collection("LISTITEM").document(Videoname);
                DocumentSnapshot CourseSnap = transaction.get(CourseRef);
                num = Colsnap.getDouble("accepted");
                num1 = Colsnap.getDouble("pending");
                if (num==null) {
                    num=0.00;
                }
                HashMap<String, Double> Colup = new HashMap<>();
                Colup.put("accepted",num+1.00);
                Colup.put("pending",num1-1.00);
               transaction.set(ColRef,Colup, SetOptions.merge());

                HashMap<String,String> Pro = new HashMap<>();
                Pro.put("project_name",Project);
                Pro.put("Status","accepted");
                transaction.set(CourseRef,Pro, SetOptions.merge());
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              deleteDirectory(delfile2);

                Intent intent = new Intent(getContext(), EmailActivity.class);
                intent.putExtra("Email",email);
                intent.putExtra("from","Accept");
                intent.putExtra("amount",0);
                intent.putExtra("CreName",CreName);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error occurred in Accepting course "+ e, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void downladPdf(){
        StorageReference pdfref = mstorage.child("Courses").child(Videoname).child("Course PDF");

        pdfref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadFile(getContext(),"Course Pdf","Purchased Courses/"+UserID+Videoname+"Purchased/",uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error occured in Downloading Pdf"+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadFile(Context context, String fileName, String destinationDirectory, Uri uri) {
        if(downloadlist.contains(uri)) {
    //        progressDialog.dismiss();
            Toast.makeText(context, "Enabling offline mode for "+fileName+". Wait for a while!", Toast.LENGTH_SHORT).show();
        } else {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName);
//            progressDialog.dismiss();
            downloadManager.enqueue(request);
            downloadlist.add(uri);
        }
    }


    private class AcContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Text;
        private SubOnClickInterface subOnClickInterface;
        public AcContentViewHolder(@NonNull View itemView) {
            super(itemView);
            Text = itemView.findViewById(R.id.Content_Video);
            itemView.setOnClickListener(this);
        }
        public void SubInterfaceClick(SubOnClickInterface subOnClickInterface){
            this.subOnClickInterface = subOnClickInterface;
        }

        @Override
        public void onClick(View view) {
            subOnClickInterface.OnClick(view,false);
        }
    }

    public void resizefun() {

        switch (size) {

            case 0 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                Toast.makeText(getView().getContext(), "ZOOM mode", Toast.LENGTH_SHORT).show();
                size = 1;
                break;

            case 1 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Toast.makeText(getView().getContext(), "FIT mode", Toast.LENGTH_SHORT).show();
                size = 2;
                break;

            case 2 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                Toast.makeText(getView().getContext(), "FILL mode", Toast.LENGTH_SHORT).show();
                size = 3;
                break;

            case 3 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                Toast.makeText(getView().getContext(), "FIXED HEIGHT mode", Toast.LENGTH_SHORT).show();
                size = 4;
                break;

            case 4 :
                playerView2.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                Toast.makeText(getView().getContext(), "FIXED WIDTH mode", Toast.LENGTH_SHORT).show();
                size = 0;
                break;

        }

    }

    private void decryptPdfFile(String encryptFilePath, SecretKey secretKey, String decnamevideo) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        int read;
        File outfile = new File(encryptFilePath);
        File dec = new File(valueOf(getActivity().getExternalFilesDir("/New Folder/Decrypted Files")));
        File decfile = new File(dec,decnamevideo);
        if(!decfile.exists())
            decfile.createNewFile();

        FileOutputStream decfos = new FileOutputStream(decfile);
        FileInputStream encfis = new FileInputStream(outfile);

        Cipher decipher = Cipher.getInstance("AES");

        decipher.init(Cipher.DECRYPT_MODE, secretKey);
        CipherOutputStream cos = new CipherOutputStream(decfos,decipher);

        byte[] buffer = new byte[1024]; // buffer can read file line by line to increase speed
        while((read=encfis.read(buffer)) >= 0)
        {
            cos.write(buffer, 0, read);
            cos.flush();
        }
        cos.close();
     //   pdfView.fromFile(decfile).defaultPage(1).enableSwipe(true).load();
        pdfView.fromFile(decfile).defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(getContext()))
                .load();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                decfile.delete();
            }
        },1500);


    }



    static public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    private void initializePlayer(String video) {
        exoPlayer2 = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView2.setPlayer(exoPlayer2);

        exoPlayer2.setPlayWhenReady(false);

        Uri uri = Uri.parse(video);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer2.prepare(mediaSource,false, false);
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(getActivity(),"Exoplayer-local")).
                createMediaSource(uri);
    }

    private void releasePlayer() {
        if (exoPlayer2 != null) {
            playWhenReady = exoPlayer2.getPlayWhenReady();
            //      playbackPosition = exoPlayer2.getCurrentPosition();
            //     currentWindow = exoPlayer2.getCurrentWindowIndex();
            exoPlayer2.release();
            exoPlayer2 = null;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((Util.SDK_INT < 24 || exoPlayer2 == null)) {
           // exoPlayer2.setPlayWhenReady(false);
            initializePlayer(VideoUrl);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
            if (Util.SDK_INT >= 24) {
               // exoPlayer2.setPlayWhenReady(false);
                initializePlayer(VideoUrl);
            }

    }



}