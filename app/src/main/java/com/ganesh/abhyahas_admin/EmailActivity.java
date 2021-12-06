package com.ganesh.abhyahas_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import Fragments.Home;

public class EmailActivity extends AppCompatActivity {

    EditText Msg;
    String CompMsg,email,from,CreaterName;
    Button Submit;
    Double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        from = intent.getStringExtra("from");
        amount = intent.getDoubleExtra("amount",0);
        CreaterName = intent.getStringExtra("CreName");


        Msg = (EditText) findViewById(R.id.ComposeMsg);
        Submit = (Button) findViewById(R.id.SubmitMail);


        CompMsg = Msg.getText().toString();


        if (from.equals("Transaction")){

            Msg.setVisibility(View.GONE);

            Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mail = new Intent(Intent.ACTION_SEND);
                    mail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    mail.putExtra(Intent.EXTRA_SUBJECT, "Regarding Amount Settlement");
                    mail.putExtra(Intent.EXTRA_TEXT, "Hello "+ CreaterName + ",\n\nWe have cleared the amount Rs."+amount+" generated up to now.\n\nRegards,\nTeam Abhyahas");
                    mail.setType("message/rfc822");
                    startActivity(Intent.createChooser(mail, "Email"));
                }
            });
        }else if(from.equals("Accept")){

            Msg.setVisibility(View.GONE);

            Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mail = new Intent(Intent.ACTION_SEND);
                    mail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    mail.putExtra(Intent.EXTRA_SUBJECT, "Course Accepted");
                    mail.putExtra(Intent.EXTRA_TEXT, "Hello "+ CreaterName + ",\n\nWe are happy to inform you that your course has been reviewed by Abhyahas Team and approved for the release." +
                            " Now your course will be live on the Abhyahas platform. We would hope you will get good enrollments. \n\nRegards,\nTeam Abhyahas");
                    mail.setType("message/rfc822");
                    startActivity(Intent.createChooser(mail, "Email"));
                }
            });
        }else{
            Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CompMsg = Msg.getText().toString();

                    if(TextUtils.isEmpty(Msg.getText().toString().trim())) {
                        Msg.setError("Text is Required.");
                        return;
                    }
                    Intent mail = new Intent(Intent.ACTION_SEND);
                    mail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    mail.putExtra(Intent.EXTRA_SUBJECT, "Course Rejected");
                    mail.putExtra(Intent.EXTRA_TEXT, CompMsg);
                    mail.setType("message/rfc822");
                    startActivity(Intent.createChooser(mail, "Email"));
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(EmailActivity.this,Container_Activity.class);
        startActivity(intent1);
    }
}